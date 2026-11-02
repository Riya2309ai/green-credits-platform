package com.greencredits.verificationservice.event;

import java.time.Instant;

public record ActionSubmittedEvent(
        String eventId,
        String actionId,
        String userId,
        String actionType,
        int quantity,
        String qrToken,
        Instant submittedAt) {
}
