package com.greencredits.ledger_service.repository;

import com.greencredits.ledger_service.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findTop100BySentFalseOrderByIdAsc();
}
