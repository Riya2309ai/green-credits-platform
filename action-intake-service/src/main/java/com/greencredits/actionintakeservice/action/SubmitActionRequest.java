package com.greencredits.actionintakeservice.action;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubmitActionRequest (@NotBlank String userId,
                                   @NotNull ActionType type,
                                   @Positive int quantity,
                                   @NotBlank String qrToken){
}
