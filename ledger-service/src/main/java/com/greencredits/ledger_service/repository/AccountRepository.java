package com.greencredits.ledger_service.repository;

import com.greencredits.ledger_service.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByOwnerId(String ownerId);
}
