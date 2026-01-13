package com.sprint.mission.discodeit.event.errorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.event.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.event.FileUploadFailedEvent;
import com.sprint.mission.discodeit.event.topic.Topics;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedFailureHandler {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Getter
    private DefaultErrorHandler handler;

    @PostConstruct
    private void setHandler() {
        ExponentialBackOffWithMaxRetries backOffWithRetry = new ExponentialBackOffWithMaxRetries(3);
        backOffWithRetry.setInitialInterval(1000L);
        backOffWithRetry.setMaxInterval(5000L);
        backOffWithRetry.setMultiplier(2.0);

        DefaultErrorHandler handler = new DefaultErrorHandler(
                (record, ex) -> {
                    try {
                        BinaryContentCreatedEvent originalEvent =
                                objectMapper.readValue((String) record.value(), BinaryContentCreatedEvent.class);
                        String error = "BinaryContent를 찾을 수 없습니다.";
                        String binaryContentId = originalEvent.getBinaryContentId().toString();

                        FileUploadFailedEvent newEvent = new FileUploadFailedEvent(
                                MDC.get(MDCLoggingInterceptor.REQUEST_ID),
                                binaryContentId,
                                error
                        );

                        String newKafkaEvent =  objectMapper.writeValueAsString(newEvent);
                        kafkaTemplate.send(Topics.FILE_UPLOAD_FAILED,  newKafkaEvent);
                        log.info("binaryContentCreatedFailureHandler : publish : FileUploadFailedEvent - Success");
                    } catch (JsonProcessingException e) {
                        log.error("binaryContentCreatedFailureHandler - Json Processing Error");
                    }
                },
                backOffWithRetry
        );
        handler.addNotRetryableExceptions(BinaryContentNotFoundException.class);
        this.handler = handler;
    }
}
