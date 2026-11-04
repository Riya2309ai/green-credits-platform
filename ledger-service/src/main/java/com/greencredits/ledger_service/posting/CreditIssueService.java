package com.greencredits.ledger_service.posting;

import com.greencredits.ledger_service.account.Account;
import com.greencredits.ledger_service.account.AccountType;
import com.greencredits.ledger_service.event.ActionVerifiedEvent;
import com.greencredits.ledger_service.event.CreditsIssuedEvent;
import com.greencredits.ledger_service.outbox.OutboxEvent;
import com.greencredits.ledger_service.repository.AccountRepository;
import com.greencredits.ledger_service.repository.LedgerEntryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class CreditIssueService {
    private final AccountRepository accountRepository;
    private final PostingService postingService;
    private final JsonMapper jsonMapper;
    private final String issuerOwnerId;

    public CreditIssueService(AccountRepository accountRepository, PostingService postingService, JsonMapper jsonMapper, @Value("${ledger.issuer-owner-id:green-issuer}") String issuerOwnerId) {
        this.accountRepository = accountRepository;
        this.postingService = postingService;
        this.jsonMapper = jsonMapper;
        this.issuerOwnerId = issuerOwnerId;
    }


    public PostingResult issue(ActionVerifiedEvent event) {
        Account issuer = findOrCreate(issuerOwnerId, AccountType.SYSTEM);
        Account user = findOrCreate(event.userId(), AccountType.USER);

        // actionId makes the key unique per action, so a redelivered event cannot pay twice.
        return postingService.post(
                "earn-" + event.actionId(),
                new PostingRequest(issuer.getId(), user.getId(), event.credits()),
                posting -> new OutboxEvent("credits.issued", event.userId(),
                        jsonMapper.writeValueAsString(new CreditsIssuedEvent(
                                "credits-issued-" + event.actionId(),
                                event.actionId(),
                                event.userId(),
                                event.credits(),
                                posting.getId()))));
    }
    private Account findOrCreate(String ownerId, AccountType type) {
        return accountRepository.findByOwnerIdAndType(ownerId, type).orElseGet(() -> {
            try {
                return accountRepository.save(new Account(ownerId, type));
            } catch (DataIntegrityViolationException e) {
                // Someone created it a moment ago; use theirs.
                return accountRepository.findByOwnerIdAndType(ownerId, type).orElseThrow(() -> e);
            }
        });
    }
}
