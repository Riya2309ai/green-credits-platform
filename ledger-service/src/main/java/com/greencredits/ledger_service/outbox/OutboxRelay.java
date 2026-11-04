package com.greencredits.ledger_service.outbox;

import com.greencredits.ledger_service.repository.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OutboxRelay {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelay(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void relay() {
        for (OutboxEvent event : outboxRepository.findTop100BySentFalseOrderByIdAsc()) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload())
                        .get(5, TimeUnit.SECONDS);
                event.markSent();
                outboxRepository.save(event);
            } catch (Exception e) {
                log.warn("Outbox relay stopped at event {}: {}", event.getId(), e.toString());
                return; // stop here so events stay in order; retried on the next run
            }
        }
    }


}
