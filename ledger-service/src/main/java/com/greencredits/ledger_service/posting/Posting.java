package com.greencredits.ledger_service.posting;

import com.greencredits.ledger_service.account.Account;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;

import java.time.Instant;

public class Posting { //Posting stores the details of a transfer from account A to account B.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;


    @ManyToOne(fetch =  FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_postings_from"))
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_postings_to"))
    private Account toAccount;

    @Column(nullable = false)
    private long amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt=Instant.now();

    public Posting(String idempotencyKey, Account fromAccount, Account toAccount, long amount) {
        this.idempotencyKey = idempotencyKey;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

}
