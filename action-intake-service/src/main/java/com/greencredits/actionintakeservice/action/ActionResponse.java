package com.greencredits.actionintakeservice.action;

import java.time.Instant;

public record ActionResponse(String id,
                             String userId,
                             ActionType type,
                             int quantity,
                             ActionStatus status,
                             boolean published,
                             Instant submittedAt) {

    public static ActionResponse from(Action a){
        return new ActionResponse(a.getId(), a.getUserId(), a.getActionType(), a.getQuantity(), a.getActionStatus(), a.isPublished(), a.getSubmittedAt());
    }

}
