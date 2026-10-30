package com.greencredits.ledger_service.posting;

import com.greencredits.ledger_service.account.Account;
import jakarta.persistence.*;

import java.time.Instant;

public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "posting_id", nullable = false, foreignKey = @ForeignKey(name = "fk_entries_posting"))
    private Posting posting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_entries_account"))
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private EntryDirection direction;

    @Column(nullable = false)
    private long amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt=Instant.now();

    public LedgerEntry(Posting posting, Account account, EntryDirection direction, long amount) {
        this.posting = posting;
        this.account=account;
        this.direction=direction;
        this.amount=amount;
    }
}
