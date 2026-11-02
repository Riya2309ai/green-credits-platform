package com.greencredits.actionintakeservice.event;

import com.greencredits.actionintakeservice.action.Action;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ActionEventPublisher {
    public static final String TOPIC = "action.submitted";

    private final KafkaTemplate<String, String> kafka;
    private final JsonMapper json;

    public ActionEventPublisher(KafkaTemplate<String, String> kafka, JsonMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    public void publish(Action action){
        ActionSubmittedEvent event=new ActionSubmittedEvent(
                "action-submitted-" + action.getId(),
                action.getId(),
                action.getUserId(),
                action.getActionType().name(),
                action.getQuantity(),
                action.getQrToken(),
                action.getSubmittedAt());

    }

}
