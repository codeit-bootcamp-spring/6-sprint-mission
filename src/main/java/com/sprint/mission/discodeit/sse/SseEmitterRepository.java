package com.sprint.mission.discodeit.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SseEmitterRepository {


    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save (UUID id , SseEmitter sseEmitter){

        if(data.containsKey(id)){
            data.get(id).add(sseEmitter);
        }else{
            data.put(id, Collections.singletonList(sseEmitter));
        }
    }

    public List<SseEmitter> findAllById(UUID id){

        return data.getOrDefault(id, null);
    }

    public Map<UUID,List<SseEmitter>> findAll(){
        return data;
    }

    public Integer count(UUID id){
        return data.size();
    }

    public void deleteByUserId(UUID id){
        data.remove(id);
    }

    public void deleteByUserIdSseEmitter(UUID id , SseEmitter sseEmitter){
        if(data.containsKey(id)){
            if(sseEmitter != null && !data.get(id).isEmpty()){
                data.get(id).remove(sseEmitter);
            }
            data.remove(id);
        }
    }
}
