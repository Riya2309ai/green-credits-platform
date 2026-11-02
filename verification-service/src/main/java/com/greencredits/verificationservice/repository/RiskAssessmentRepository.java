package com.greencredits.verificationservice.repository;

import com.greencredits.verificationservice.assessment.RiskAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface RiskAssessmentRepository extends MongoRepository<RiskAssessment, String> {

    long countByUserIdAndCreatedAtAfter(String userId, Instant after);

    List<RiskAssessment> findByPublishedFalse();
}
