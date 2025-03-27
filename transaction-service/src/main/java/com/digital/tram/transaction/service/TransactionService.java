package com.digital.tram.transaction.service;

import com.digital.tram.transaction.domain.Transaction;
import com.digital.tram.transaction.domain.TransactionRepository;
import com.digital.tram.transaction.messaging.TransactionEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher eventPublisher;
    private final WebClient webClient;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionEventPublisher eventPublisher,
            WebClient.Builder webClientBuilder) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Transactional
    public Transaction createTransaction(String accountId, BigDecimal amount,
                                         String type, String description) {
        // First validate account with the balance service
        boolean isValid = validateAccount(accountId, amount);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid account or insufficient balance");
        }

        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
                transactionId, accountId, amount, type, description);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publish event to be consumed by the balance service
        eventPublisher.publishTransactionCreatedEvent(savedTransaction);

        return savedTransaction;
    }

    private boolean validateAccount(String accountId, BigDecimal amount) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{accountId}/validate")
                        .queryParam("amount", amount)
                        .build(accountId))
                .retrieve()
                .bodyToMono(ValidationResponse.class)
                .map(ValidationResponse::isValid)
                .block();
    }

    public List<Transaction> getTransactionsByAccount(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public Optional<Transaction> getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    private static class ValidationResponse {
        private boolean valid;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }
}