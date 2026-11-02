package com.greencredits.verificationservice.repository;

import com.greencredits.verificationservice.assessment.QrClaim;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QrClaimRepository extends MongoRepository<QrClaim, String> {
}
