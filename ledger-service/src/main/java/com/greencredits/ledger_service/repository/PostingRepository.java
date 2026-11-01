package com.greencredits.ledger_service.repository;

import com.greencredits.ledger_service.posting.Posting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostingRepository extends JpaRepository<Posting, Long> {
    Optional<Posting> findByIdempotencyKey(String idempotencyKey);
}
