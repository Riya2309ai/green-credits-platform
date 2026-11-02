package com.greencredits.verificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic actionVerifiedTopic() {
        return TopicBuilder.name("action.verified").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic fraudFlaggedTopic() {
        return TopicBuilder.name("fraud.flagged").partitions(3).replicas(1).build();
    }

    // Same number of partitions as the source topic, so dead letters keep their partition.
    @Bean
    public NewTopic actionSubmittedDeadLetterTopic() {
        return TopicBuilder.name("action.submitted.DLT").partitions(3).replicas(1).build();
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }
}
