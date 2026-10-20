package com.greencredits.ledger_service.service;

import com.greencredits.ledger_service.account.Account;
import com.greencredits.ledger_service.repository.AccountRepository;
import com.greencredits.ledger_service.request.CreateAccountRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {
        private final AccountRepository accountRepository;

        public AccountService(AccountRepository accountRepository) {
            this.accountRepository = accountRepository;
        }

        public Account createAccount(CreateAccountRequest createAccountRequest){
           try {
               return accountRepository.save(new Account(createAccountRequest.ownerId(), createAccountRequest.accountType()));
           }
           catch (DataIntegrityViolationException e) {
               throw new ResponseStatusException(HttpStatus.CONFLICT, "Account already exists");
           }
        }

        public Account getAccount(Long id){
            return accountRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        }

}
