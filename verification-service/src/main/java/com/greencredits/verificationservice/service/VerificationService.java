package com.greencredits.verificationservice.service;

import com.greencredits.verificationservice.assessment.QrClaim;
import com.greencredits.verificationservice.assessment.RiskAssessment;
import com.greencredits.verificationservice.event.ActionSubmittedEvent;
import com.greencredits.verificationservice.event.VerificationEventPublisher;
import com.greencredits.verificationservice.repository.QrClaimRepository;
import com.greencredits.verificationservice.repository.RiskAssessmentRepository;
import com.greencredits.verificationservice.rules.ActionRules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.auditing.ReactiveIsNewAwareAuditingHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class VerificationService {

    // Demo assumption.
    private static final int MAX_CLAIMS_PER_HOUR = 5;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final QrClaimRepository qrClaimRepository;
    private final VerificationEventPublisher verificationEventPublisher;

    public VerificationService(RiskAssessmentRepository riskAssessmentRepository, QrClaimRepository qrClaimRepository, VerificationEventPublisher verificationEventPublisher) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.qrClaimRepository = qrClaimRepository;
        this.verificationEventPublisher = verificationEventPublisher;
    }

    public void process(ActionSubmittedEvent event) {
        RiskAssessment existing = riskAssessmentRepository.findById(event.eventId()).orElse(null);
        if(existing!=null){
            if(!existing.isPublished()){
                tryPublish(existing);
            }
            return;
        }
        String reason = findFraudReason(event);
        RiskAssessment riskAssessment=reason==null ?
                RiskAssessment.verified(event,ActionRules.creditsFor(event.actionType(), event.quantity())) :
                RiskAssessment.flagged(event, reason);
        riskAssessmentRepository.save(riskAssessment);
        tryPublish(riskAssessment);
    }

    // Publishes decisions that were saved but not yet sent to Kafka.
    @Scheduled(fixedDelay = 10_000)
    public void retryUnpublished() {
        for (RiskAssessment a : riskAssessmentRepository.findByPublishedFalse()) {
            tryPublish(a);
        }
    }

    private String findFraudReason(ActionSubmittedEvent event) {
        if(!ActionRules.isKnown(event.actionType())){
            return "UNKNOWN_ACTION_TYPE";
        }
        if(event.quantity()<=0 || event.quantity()>ActionRules.maxQuantity(event.actionType())){
            return "QUANTITY_OUT_OF_RANGE";
        }

        long recent=riskAssessmentRepository.countByUserIdAndCreatedAtAfter(event.userId(), Instant.now().minus(1, ChronoUnit.HOURS));
        if(recent>MAX_CLAIMS_PER_HOUR){
            return  "TOO_MANY_CLAIMS";
        }
        if(!claimQr(event)){
            return "QR_TOKEN_REUSED";
        }
        return null;
    }

    // The QR token is the document id, so only one claim can ever insert it.
    private boolean claimQr(ActionSubmittedEvent event) {
        try{
            qrClaimRepository.insert(new QrClaim(event.qrToken(), event.actionId()));
            return true;
        }
        catch (DuplicateKeyException e){
            // Same action seen again (redelivery after a crash) is not fraud.
            return qrClaimRepository.findById(event.qrToken())
                    .map(c->c.getActionId().equals(event.actionId()))
                    .orElse(false);
        }
    }

    private void tryPublish(RiskAssessment assessment) {
        try{
            verificationEventPublisher.publish(assessment);
            assessment.markPublished();
            riskAssessmentRepository.save(assessment);
        }
        catch (Exception ex){
            log.warn("Could not publish decision for action {} , will retry: {}",assessment.getId(),ex.toString());
        }
    }
}
