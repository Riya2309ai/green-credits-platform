package com.greencredits.ledger_service.outbox;

import com.greencredits.ledger_service.account.AccountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "outbox-events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false, length = 100)
    private String topic;

    @Column(name="message_key", nullable = false, length = 100)
    private String messageKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean sent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt= Instant.now();

    public  OutboxEvent(String topic, String messageKey, String payload) {
        this.topic = topic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.sent=false;
    }

    public void markSent(){
        this.sent=true;
    }

}
