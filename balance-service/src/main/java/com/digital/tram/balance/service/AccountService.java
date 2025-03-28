package com.digital.tram.balance.service;

import com.digital.tram.balance.domain.Account;
import com.digital.tram.balance.domain.AccountRepository;
import com.digital.tram.balance.exception.AccountClosedException;
import com.digital.tram.balance.exception.AccountNotFoundException;
import com.digital.tram.balance.messaging.AccountEventPublisher;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountService {

  private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found: ";
  private final AccountRepository accountRepository;
  private final AccountEventPublisher accountEventPublisher;

  @Autowired
  public AccountService(
      AccountRepository accountRepository, AccountEventPublisher accountEventPublisher) {
    this.accountRepository = accountRepository;
    this.accountEventPublisher = accountEventPublisher;
  }

  @Transactional
  public Account createAccount(String ownerName, BigDecimal initialBalance) {
    // Existing code
    String accountId = UUID.randomUUID().toString();
    Account account = new Account(accountId, ownerName, initialBalance);

    // Save account
    Account savedAccount = accountRepository.save(account);
    System.out.println("Creating account with ID: " + accountId);

    // Explicitly publish the event
    try {
      accountEventPublisher.publishAccountCreatedEvent(savedAccount);
      log.info("Published AccountCreatedEvent for account: {}", accountId);
    } catch (Exception e) {
      log.error("Failed to publish AccountCreatedEvent for account: {}", accountId, e);
      throw e; // Re-throw to trigger transaction rollback
    }

    return savedAccount;
  }

  @Transactional(readOnly = true)
  public Optional<Account> getAccount(String accountId) {
    return accountRepository.findByAccountId(accountId);
  }

  @Transactional(readOnly = true)
  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }

  @Transactional(readOnly = true)
  public boolean validateAccount(String accountId, BigDecimal amount) {
    Optional<Account> accountOptional = accountRepository.findByAccountId(accountId);
    return accountOptional
        .map(
            account -> {
              if (account.isClosed()) {
                return false;
              }
              return account.hasSufficientBalance(amount);
            })
        .orElse(false);
  }

  @Transactional
  public Account updateBalance(String accountId, BigDecimal amount, boolean isCredit) {
    Account account =
        accountRepository
            .findByAccountId(accountId)
            .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + accountId));

    if (account.isClosed()) {
      throw new AccountClosedException("Cannot update balance for closed account: " + accountId);
    }

    if (isCredit) {
      account.credit(amount);
    } else {
      account.debit(amount);
    }

    Account savedAccount = accountRepository.save(account);

    // Publish event for account update
    accountEventPublisher.publishAccountUpdatedEvent(savedAccount);

    return savedAccount;
  }

  @Transactional
  public Account updateAccountOwner(String accountId, String newOwnerName) {
    Account account =
        accountRepository
            .findByAccountId(accountId)
            .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + accountId));

    if (account.isClosed()) {
      throw new AccountClosedException("Cannot update owner for closed account: " + accountId);
    }

    account.setOwnerName(newOwnerName);
    Account savedAccount = accountRepository.save(account);

    // Publish event for account update
    accountEventPublisher.publishAccountUpdatedEvent(savedAccount);

    return savedAccount;
  }

  @Transactional
  public Account closeAccount(String accountId, String closureReason) {
    Account account =
        accountRepository
            .findByAccountId(accountId)
            .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + accountId));

    if (account.isClosed()) {
      throw new AccountClosedException("Account is already closed: " + accountId);
    }

    account.setClosed(true);
    Account savedAccount = accountRepository.save(account);

    // Publish event for account closure
    accountEventPublisher.publishAccountClosedEvent(savedAccount, closureReason);

    return savedAccount;
  }
}
