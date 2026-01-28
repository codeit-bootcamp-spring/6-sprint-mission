package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.model.MessageDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

// todo 리스너에 mdc 적용
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  @Async("websocketOutboundExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreatedEvent(MessageCreatedEvent event) {
    MessageDto dto = messageMapper.toDto(event.message());
    String destination = "/sub/channels." + event.channelId() + ".messages";

    messagingTemplate.convertAndSend(destination, dto);
  }

  @EventListener
  public void handleConnect(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    log.debug("새 웹소켓 연결: 사용자 = {}, 세션ID = {}",
        extractUsername(accessor), accessor.getSessionId());
  }

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    log.debug("사용자 연결 종료: 사용자 = {}, 세션ID = {}",
        extractUsername(accessor), event.getSessionId());
  }

  @EventListener
  public void handleSubscribe(SessionSubscribeEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    log.debug("새로운 구독 생성: 사용자 = {}, 세션ID = {}, 목적지 = {}",
        extractUsername(accessor), accessor.getSessionId(), accessor.getDestination());
  }

  private String extractUsername(StompHeaderAccessor accessor) {
    if (accessor.getUser() != null) {
      return accessor.getUser().getName();
    } else {
      log.warn("StompHeaderAccessor에 사용자 정보가 없습니다.");
      return null;
    }
  }
}
