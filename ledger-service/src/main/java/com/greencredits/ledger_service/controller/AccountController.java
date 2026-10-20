package com.greencredits.ledger_service.controller;

import com.greencredits.ledger_service.account.Account;
import com.greencredits.ledger_service.request.CreateAccountRequest;
import com.greencredits.ledger_service.response.AccountResponse;
import com.greencredits.ledger_service.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.fromAcc(accountService.createAccount(createAccountRequest)));
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id){
        return AccountResponse.fromAcc(accountService.getAccount(id));
    }

}
