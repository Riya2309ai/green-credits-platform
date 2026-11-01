package com.greencredits.ledger_service.repository;

import com.greencredits.ledger_service.posting.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    long countByAccountId(Long accountId);
}
