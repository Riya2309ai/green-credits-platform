package com.greencredits.ledger_service.event;

import com.greencredits.ledger_service.posting.CreditIssueService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ActionVerifiedListener {

    private final CreditIssueService creditIssueService;
    private final JsonMapper jsonMapper;


    public ActionVerifiedListener(CreditIssueService creditIssueService, JsonMapper jsonMapper) {
        this.creditIssueService = creditIssueService;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "action.verified")
    public void onMessage(String payload){
        ActionVerifiedEvent event = jsonMapper.readValue(payload, ActionVerifiedEvent.class);
        creditIssueService.issue(event);
    }
}
