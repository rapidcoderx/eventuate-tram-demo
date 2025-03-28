package com.digital.tram.transaction.service;

import com.digital.tram.transaction.domain.Transaction;
import com.digital.tram.transaction.domain.TransactionRepository;
import com.digital.tram.transaction.messaging.TransactionEventPublisher;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

  private final TransactionRepository transactionRepository;
  private final TransactionEventPublisher eventPublisher;
  private final AccountService accountService;

  @Autowired
  public TransactionService(
      TransactionRepository transactionRepository,
      TransactionEventPublisher eventPublisher,
      AccountService accountService) {
    this.transactionRepository = transactionRepository;
    this.eventPublisher = eventPublisher;
    this.accountService = accountService;
  }

  @Transactional
  public Transaction createTransaction(
      String accountId, BigDecimal amount, String type, String description) {
    // Validate account using the AccountService which now uses the local cache when possible
    boolean isValid = accountService.validateAccount(accountId, amount);

    if (!isValid) {
      logger.warn(
          "Transaction creation failed - invalid account or insufficient balance for accountId: {}",
          accountId);
      throw new IllegalArgumentException("Invalid account or insufficient balance");
    }

    String transactionId = UUID.randomUUID().toString();
    Transaction transaction = new Transaction(transactionId, accountId, amount, type, description);

    Transaction savedTransaction = transactionRepository.save(transaction);
    logger.info("Created new transaction with ID: {}", transactionId);

    // Publish event to be consumed by the balance service
    eventPublisher.publishTransactionCreatedEvent(savedTransaction);
    logger.info("Published TransactionCreatedEvent for transaction: {}", transactionId);

    return savedTransaction;
  }

  public List<Transaction> getTransactionsByAccount(String accountId) {
    return transactionRepository.findByAccountId(accountId);
  }

  public Optional<Transaction> getTransaction(String transactionId) {
    return transactionRepository.findById(transactionId);
  }
}
