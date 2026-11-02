package com.greencredits.verificationservice.assessment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Document(collection = "qr_claims")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrClaim {

    @Id
    private String qrToken;

    private String actionId;
    private Instant claimedAt;

    public QrClaim(String qrToken, String actionId) {
        this.qrToken = qrToken;
        this.actionId = actionId;
        this.claimedAt = Instant.now();
    }
}
