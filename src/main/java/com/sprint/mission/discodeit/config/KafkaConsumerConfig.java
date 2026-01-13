package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.event.errorHandler.BinaryContentCreatedFailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfig {

    private final BinaryContentCreatedFailureHandler binaryContentCreatedFailureHandler;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> binaryContentKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(binaryContentCreatedFailureHandler.getHandler());
        return factory;
    }

}
