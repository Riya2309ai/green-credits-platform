package com.greencredits.ledger_service.posting;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PostingRequest(
        @NotNull Long fromAccountId,
        @NotNull Long toAccountId,
        @Positive long amount) {
}
