package com.greencredits.verificationservice.event;

import com.greencredits.verificationservice.assessment.Decision;
import com.greencredits.verificationservice.assessment.RiskAssessment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.TimeUnit;

@Component
public class VerificationEventPublisher {
    public static final String VERIFIED_TOPIC = "action.verified";
    public static final String FLAGGED_TOPIC = "fraud.flagged";

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    public VerificationEventPublisher(KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    public void publish(RiskAssessment a) throws Exception {
        if (a.getDecision() == Decision.VERIFIED) {
            send(VERIFIED_TOPIC, a.getUserId(), new ActionVerifiedEvent(
                    "action-verified-" + a.getId(), a.getId(), a.getUserId(), a.getCredits()));
        } else {
            send(FLAGGED_TOPIC, a.getUserId(), new FraudFlaggedEvent(
                    "fraud-flagged-" + a.getId(), a.getId(), a.getUserId(), a.getReason()));
        }
    }
    private void send(String topic, String key, Object event) throws Exception {
        String payload = json.writeValueAsString(event);
        kafka.send(topic, key, payload).get(5, TimeUnit.SECONDS);
    }
}
