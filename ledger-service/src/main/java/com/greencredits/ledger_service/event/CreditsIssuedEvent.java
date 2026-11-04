package com.greencredits.ledger_service.event;

public record CreditsIssuedEvent(String eventId,
                                 String actionId,
                                 String userId,
                                 long credits,
                                 Long postingId) {
}
