package com.greencredits.ledger_service.account;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(
        name = "uk_accounts_owner_type",
        columnNames = {"owner_id", "type"}))
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false, length = 64)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountType type;

    @Column(nullable = false)
    private long balance;

    @Column(name = "created_at",  nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Account() {}

    public Account(String ownerId , AccountType type) {
        this.ownerId = ownerId;
        this.type = type;
        this.balance = 0;
    }
    public boolean canDebit(long amount){
        return type==AccountType.SYSTEM || balance>=amount;
    }
    public void debit(long amount){
        this.balance-=amount;
    }
    public void credit(long amount){
        this.balance+=amount;
    }

}
