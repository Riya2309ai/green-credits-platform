package com.greencredits.actionintakeservice.action;

import com.greencredits.actionintakeservice.event.ActionEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ActionService {
    private final ActionRepository actionRepository;
    private final ActionEventPublisher publisher;

    public ActionService(ActionRepository actionRepository, ActionEventPublisher publisher) {
        this.actionRepository = actionRepository;
        this.publisher = publisher;
    }

    public Action get(String id){
        return actionRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Action not Found"));
    }

    // Retries anything that was saved but not yet published to Kafka.
    @Scheduled(fixedDelay = 10_000)
    public void retryUnpublished(){
        for(Action action: actionRepository.findByPublishedFalse()){
            tryPublish(action);
        }
    }

    public Action submit(SubmitActionRequest req){
        Action action=actionRepository.save(new Action(req.userId(), req.type(), req.quantity(), req.qrToken()));
        tryPublish(action);
        return action;
    }
    public void tryPublish(Action action){
        try{
            publisher.publish(action);
            action.markPublished();
            actionRepository.save(action);
        }
        catch (Exception ex){
            log.warn("Could not publish action {}, will retry: {}", action.getId(), ex.toString());
        }
    }

}
