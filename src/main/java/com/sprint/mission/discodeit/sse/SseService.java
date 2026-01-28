package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.sse.dto.SseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    private static final int MAX_CONNECTIONS = 100;

    public SseEmitter connect(UUID receiverId, UUID lastEventId){

        SseEmitter emitter = new SseEmitter(60 * 60 * 100L);

        Integer count = sseEmitterRepository.count(receiverId);
        if(count > MAX_CONNECTIONS && ping(emitter)){
            try{
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("SSE 연결 오류 발생")
                );
                emitter.complete();
                return emitter;
            }catch (IOException e){
                return emitter;
            }
        }

        sseEmitterRepository.save(receiverId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("알림 스트림에 연결되었습니다")
                    .id("connect-" + receiverId)
            );

        } catch (IOException e) {
            sseEmitterRepository.deleteByUserIdSseEmitter(receiverId,emitter);
        }

        //유실 복구 파트
        eventLossRecovery(receiverId, lastEventId, emitter);

        return emitter;
    }


    public void send(Collection<UUID> receiverIds, String eventName, Object data){

        UUID eventId = createdEventId(eventName, data);

        Map<UUID, List<SseEmitter>> targets = new HashMap<>();

        for (UUID receiverId : receiverIds){
            List<SseEmitter> emitters = sseEmitterRepository.findAllById(receiverId);
            if(emitters != null && !emitters.isEmpty()){
                targets.put(receiverId, emitters);
            }
        }

        eventDelivery(eventName, data, targets, eventId);
    }


    public void broadcast(String eventName, Object data) {
        Map<UUID,List<SseEmitter>> targets = sseEmitterRepository.findAll();

        UUID eventId = createdEventId(eventName,data);

        eventDelivery(eventName, data, targets, eventId);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        Map<UUID,List<SseEmitter>> allEmitters = sseEmitterRepository.findAll();

        for (Map.Entry<UUID, List<SseEmitter>> entry : allEmitters.entrySet()){
            UUID userId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();

            emitters.removeIf(emitter -> {
               boolean isAlive = ping(emitter);
               if(!isAlive){
                   //죽은 SSE연결 지우기
                   sseEmitterRepository.deleteByUserIdSseEmitter(userId,emitter);
                   return true;
               }
               return true;
            });

            // 유저에 모든 SSE가 끝어지면 SSE맵에서 키삭제
            if (emitters.isEmpty()){
                sseEmitterRepository.deleteByUserId(userId);
            }
        }
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name("ping")
                    .data(""));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void eventLossRecovery(UUID receiverId, UUID lastEventId, SseEmitter emitter) {
        if (lastEventId != null){
            List<SseMessage> missedEvents = sseMessageRepository.findAllAfter(lastEventId);

            for(SseMessage event : missedEvents){
                if(event.id().equals(receiverId)){
                    try {
                        emitter.send(SseEmitter.event()
                                .id(event.id().toString())
                                .name(event.name())
                                .data(event.date())
                        );
                    } catch (IOException e) {
                        sseEmitterRepository.deleteByUserIdSseEmitter(receiverId, emitter);
                    }
                }
            }
        }
    }

    private void eventDelivery(String eventName, Object data, Map<UUID, List<SseEmitter>> targets, UUID eventId) {
        targets.entrySet().parallelStream().forEach(entry -> {
            UUID receiverId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();

            for (SseEmitter emitter : emitters){
                try {
                    emitter.send(SseEmitter.event()
                            .id(eventId.toString())
                            .name(eventName)
                            .data(data)
                    );
                }catch (IOException e) {
                    sseEmitterRepository.deleteByUserIdSseEmitter(receiverId,emitter);
                }
            }
        });
    }

    private UUID createdEventId(String eventName, Object data) {
        UUID eventId = UUID.randomUUID();

        SseMessage sseMessage = new SseMessage(eventId, eventName, data);

        sseMessageRepository.save(eventId, sseMessage);
        return eventId;
    }

}
