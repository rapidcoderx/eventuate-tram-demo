package com.digital.tram.transaction.controller;

import com.digital.tram.transaction.domain.Transaction;
import com.digital.tram.transaction.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(
                request.getAccountId(),
                request.getAmount(),
                request.getType(),
                request.getDescription()
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String transactionId) {
        return transactionService.getTransaction(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable String accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTransactionRequest {
        private String accountId;
        private BigDecimal amount;
        private String type; // PAYMENT or DEPOSIT
        private String description;
    }
}