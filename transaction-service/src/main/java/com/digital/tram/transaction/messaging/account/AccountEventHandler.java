package com.digital.tram.transaction.messaging.account;

import com.digital.tram.transaction.domain.account.AccountCache;
import com.digital.tram.transaction.domain.account.AccountCacheRepository;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountEventHandler {

  private static final Logger logger = LoggerFactory.getLogger(AccountEventHandler.class);
  private final AccountCacheRepository accountCacheRepository;

  @Autowired
  public AccountEventHandler(AccountCacheRepository accountCacheRepository) {
    this.accountCacheRepository = accountCacheRepository;
  }

  public DomainEventHandlers domainEventHandlers() {
    return DomainEventHandlersBuilder.forAggregateType("com.digital.tram.balance.domain.Account")
        .onEvent(AccountCreatedEvent.class, this::handleAccountCreatedEvent)
        .onEvent(AccountUpdatedEvent.class, this::handleAccountUpdatedEvent)
        .onEvent(AccountClosedEvent.class, this::handleAccountClosedEvent)
        .build();
  }

  @Transactional
  public void handleAccountCreatedEvent(DomainEventEnvelope<AccountCreatedEvent> envelope) {
    AccountCreatedEvent event = envelope.getEvent();
    logger.info("Received account created event: {}", event);

    AccountCache accountCache =
        new AccountCache(event.getAccountId(), event.getOwnerName(), event.getInitialBalance());

    accountCacheRepository.save(accountCache);
    logger.info("Created local account cache for account: {}", event.getAccountId());
  }

  @Transactional
  public void handleAccountUpdatedEvent(DomainEventEnvelope<AccountUpdatedEvent> envelope) {
    AccountUpdatedEvent event = envelope.getEvent();
    logger.info("Received account updated event: {}", event);

    accountCacheRepository
        .findByAccountId(event.getAccountId())
        .ifPresent(
            accountCache -> {
              accountCache.setOwnerName(event.getOwnerName());
              accountCache.updateBalance(event.getNewBalance());
              accountCacheRepository.save(accountCache);
              logger.info("Updated local account cache for account: {}", event.getAccountId());
            });
  }

  @Transactional
  public void handleAccountClosedEvent(DomainEventEnvelope<AccountClosedEvent> envelope) {
    AccountClosedEvent event = envelope.getEvent();
    logger.info("Received account closed event: {}", event);

    accountCacheRepository
        .findByAccountId(event.getAccountId())
        .ifPresent(
            accountCache -> {
              accountCache.setBalance(event.getFinalBalance());
              accountCache.close();
              accountCacheRepository.save(accountCache);
              logger.info(
                  "Marked local account cache as closed for account: {}", event.getAccountId());
            });
  }
}
