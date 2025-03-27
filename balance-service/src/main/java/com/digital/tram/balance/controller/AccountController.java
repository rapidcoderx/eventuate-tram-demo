package com.digital.tram.balance.controller;

import com.digital.tram.balance.domain.Account;
import com.digital.tram.balance.service.AccountService;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request.getOwnerName(), request.getInitialBalance());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {
        return accountService.getAccount(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountId}/validate")
    public ResponseEntity<ValidationResponse> validateAccount(
            @PathVariable String accountId,
            @RequestParam BigDecimal amount) {
        boolean isValid = accountService.validateAccount(accountId, amount);
        return ResponseEntity.ok(new ValidationResponse(isValid));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAccountRequest {
        private String ownerName;
        private BigDecimal initialBalance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResponse {
        private boolean valid;
    }
}