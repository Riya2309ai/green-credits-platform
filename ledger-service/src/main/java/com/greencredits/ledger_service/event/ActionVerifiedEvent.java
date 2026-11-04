package com.greencredits.ledger_service.event;

public record ActionVerifiedEvent(String eventId,
                                  String actionId,
                                  String userId,
                                  long credits){
}
