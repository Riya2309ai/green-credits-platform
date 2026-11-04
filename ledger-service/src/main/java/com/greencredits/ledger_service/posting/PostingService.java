package com.greencredits.ledger_service.posting;

import com.greencredits.ledger_service.account.Account;
import com.greencredits.ledger_service.outbox.OutboxEvent;
import com.greencredits.ledger_service.repository.AccountRepository;
import com.greencredits.ledger_service.repository.LedgerEntryRepository;
import com.greencredits.ledger_service.repository.OutboxRepository;
import com.greencredits.ledger_service.repository.PostingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class PostingService {
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final PostingRepository postingRepository;
    private final OutboxRepository outboxRepository;
    private final TransactionTemplate tx;

    public PostingService(AccountRepository accountRepository,
                          LedgerEntryRepository ledgerEntryRepository,
                          PostingRepository postingRepository, OutboxRepository outboxRepository,
                          TransactionTemplate tx
                          ){
        this.accountRepository=accountRepository;
        this.postingRepository=postingRepository;
        this.ledgerEntryRepository=ledgerEntryRepository;
        this.outboxRepository = outboxRepository;
        this.tx=tx;
    }

    public PostingResult post(String idempotencyKey, PostingRequest req) {
        return post(idempotencyKey, req, null);
    }

    /**
     * eventFactory (optional) builds an outbox row from the new posting.
     * It runs inside the same transaction, so the posting and its event commit together.
     */
    public PostingResult post(String idempotencyKey, PostingRequest req, Function<Posting, OutboxEvent> eventFactory) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency-Key header is required (max 100 characters)");
        }
        if (req.amount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        if (req.fromAccountId().equals(req.toAccountId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot post between the same account");
        }
        try {
            return tx.execute(status -> doPost(idempotencyKey, req, eventFactory));
        }
        catch (DataIntegrityViolationException ex){
            // Safety net: another request with the same key committed first.
            return tx.execute(status -> postingRepository.findByIdempotencyKey(idempotencyKey)
                    .map(existing->replay(existing,req))
                    .orElseThrow(()->ex));
        }
    }
    private PostingResult doPost(String idempotencyKey, PostingRequest req, Function<Posting, OutboxEvent> eventFactory) {
        // 1. Lock both rows, always in ID order.
        List<Account> locked=accountRepository.findAllByIdForUpdate(List.of(req.fromAccountId(), req.toAccountId()));
        if(locked.size()!=2){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        Account from = locked.stream().filter(a->a.getId().equals(req.fromAccountId())).findFirst().orElseThrow();
        Account to = locked.stream().filter(a->a.getId().equals(req.toAccountId())).findFirst().orElseThrow();

        // 2. Idempotency check, after the locks.
        Optional<Posting> existing= postingRepository.findByIdempotencyKey(idempotencyKey);
        if(existing.isPresent()){
            return replay(existing.get(),req);
        }

        // 3. Business rule.
        if(!from.canDebit(req.amount())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Insufficient credits");
        }

        // 4. Apply: balances, posting row, two ledger entries, optional outbox row.
        from.debit(req.amount());
        to.credit(req.amount());
        Posting posting=postingRepository.save(new Posting(idempotencyKey, from, to, req.amount()));
        ledgerEntryRepository.save(new LedgerEntry(posting, from, EntryDirection.DEBIT, req.amount()));
        ledgerEntryRepository.save(new LedgerEntry(posting, to, EntryDirection.CREDIT, req.amount()));
        if (eventFactory != null) {
            outboxRepository.save(eventFactory.apply(posting));
        }
        return new PostingResult(PostingResponse.from(posting), true);
    }

    private PostingResult replay(Posting existing, PostingRequest req) {
        boolean sameRequest=existing.getFromAccount().getId().equals(req.fromAccountId()) && existing.getToAccount().getId().equals(req.toAccountId()) && existing.getAmount()==req.amount();
        if(!sameRequest){ // from, to same but amount different case
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Idempotency key was already used with a different request");
        }
        return new PostingResult(PostingResponse.from(existing), false);
    }

}
