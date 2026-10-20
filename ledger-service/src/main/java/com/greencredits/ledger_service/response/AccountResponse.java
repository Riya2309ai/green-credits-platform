package com.greencredits.ledger_service.response;

import com.greencredits.ledger_service.account.Account;
import com.greencredits.ledger_service.account.AccountType;

import java.time.Instant;

public record AccountResponse(Long id, String ownerId, AccountType accountType, long  balance, Instant createdAt) {
    public static AccountResponse fromAcc(Account a){
        return new AccountResponse(a.getId(), a.getOwnerId(), a.getType(), a.getBalance(), a.getCreatedAt());
    }
}
