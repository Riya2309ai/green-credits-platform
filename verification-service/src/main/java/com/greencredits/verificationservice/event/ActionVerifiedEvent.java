package com.greencredits.verificationservice.event;

public record ActionVerifiedEvent(
        String eventId,
        String actionId,
        String userId,
        long credits) {
}
