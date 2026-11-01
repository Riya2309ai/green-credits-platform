package com.greencredits.ledger_service.posting;

import java.time.Instant;

public record PostingResponse(
        Long postingId,
        String idempotencyKey,
        Long fromAccountId,
        Long toAccountId,
        long amount,
        Instant createdAt) {
    public static PostingResponse from(Posting p) {
        return new PostingResponse(p.getId(), p.getIdempotencyKey(), p.getFromAccount().getId(), p.getToAccount().getId(), p.getAmount(), p.getCreatedAt());

    }
}
