package com.greencredits.actionintakeservice.action;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "actions")
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Action {
    @Id
    private String id;

    private String userId;
    private ActionType actionType;
    private int quantity;
    private String qrToken;

    private ActionStatus actionStatus;
    private boolean published;
    private Instant submittedAt;

    public Action(String userId, ActionType actionType, int quantity, String qrToken) {
        this.userId=userId;
        this.actionType=actionType;
        this.quantity=quantity;
        this.qrToken=qrToken;

        this.actionStatus=ActionStatus.SUBMITTED;
        this.published=false;
        this.submittedAt=Instant.now();
    }
    public void markPublished(){
        this.published=true;
    }
}
