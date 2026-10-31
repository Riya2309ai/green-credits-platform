package com.greencredits.ledger_service.repository;

import com.greencredits.ledger_service.posting.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostingRepository extends JpaRepository<Posting, Long> {
    Optional<Posting> findByIdempotencyKey(String  idempotencyKey);
}
