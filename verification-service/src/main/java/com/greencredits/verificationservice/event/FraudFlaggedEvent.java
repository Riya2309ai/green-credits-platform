package com.greencredits.verificationservice.event;

public record FraudFlaggedEvent(
        String eventId,
        String actionId,
        String userId,
        String reason){
}
