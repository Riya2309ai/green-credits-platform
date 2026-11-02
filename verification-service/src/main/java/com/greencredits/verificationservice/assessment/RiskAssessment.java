package com.greencredits.verificationservice.assessment;

import com.greencredits.verificationservice.event.ActionSubmittedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "risk_assessments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiskAssessment {
    @Id
    private String id; // the actionId

    private String userId;
    private String actionType;
    private int quantity;
    private String qrToken;
    private Decision decision;
    private String reason;
    private long credits;
    private boolean published;
    private Instant createdAt;

    private RiskAssessment(ActionSubmittedEvent e, Decision decision, String reason, long credits) {
        this.id = e.actionId();
        this.userId = e.userId();
        this.actionType = e.actionType();
        this.quantity = e.quantity();
        this.qrToken = e.qrToken();
        this.decision = decision;
        this.reason = reason;
        this.credits = credits;
        this.published = false;
        this.createdAt = Instant.now();
    }
    public static RiskAssessment verified(ActionSubmittedEvent e, long credits) {
        return new RiskAssessment(e, Decision.VERIFIED, null, credits);
    }

    public static RiskAssessment flagged(ActionSubmittedEvent e, String reason) {
        return new RiskAssessment(e, Decision.FLAGGED, reason, 0);
    }

    public void markPublished() {
        this.published = true;
    }


}
