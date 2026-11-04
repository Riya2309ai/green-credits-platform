package com.greencredits.ledger_service.repository;

import com.greencredits.ledger_service.account.Account;
import com.greencredits.ledger_service.account.AccountType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
   // boolean existsByOwnerId(String ownerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id in :ids order by a.id ")
    List<Account> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);

    Optional<Account> findByOwnerIdAndType(String ownerId, AccountType type);

}
