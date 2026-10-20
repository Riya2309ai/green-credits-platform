package com.greencredits.ledger_service.request;

import com.greencredits.ledger_service.account.AccountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(@NotBlank String ownerId, @NotNull AccountType accountType) {}
