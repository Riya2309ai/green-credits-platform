package com.greencredits.verificationservice.event;

import com.greencredits.verificationservice.service.VerificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ActionSubmittedListener {
    private final VerificationService service;
    private final JsonMapper json;

    public ActionSubmittedListener(VerificationService service, JsonMapper json) {
        this.service = service;
        this.json = json;
    }

    @KafkaListener(topics = "action.submitted")
    public void onMessage(String payload){
        ActionSubmittedEvent event = json.readValue(payload, ActionSubmittedEvent.class);
        service.process(event);
    }

}
