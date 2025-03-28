package com.digital.tram.balance.messaging;

import com.digital.tram.balance.domain.Account;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import java.time.LocalDateTime;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountEventPublisher {

  private final DomainEventPublisher domainEventPublisher;

  @Autowired
  public AccountEventPublisher(DomainEventPublisher domainEventPublisher) {
    this.domainEventPublisher = domainEventPublisher;
  }

  public void publishAccountCreatedEvent(Account account) {
    AccountCreatedEvent event =
        new AccountCreatedEvent(
            account.getAccountId(),
            account.getOwnerName(),
            account.getBalance(),
            LocalDateTime.now());

    domainEventPublisher.publish(
        Account.class, account.getAccountId(), Collections.singletonList(event));
  }

  public void publishAccountUpdatedEvent(Account account) {
    AccountUpdatedEvent event =
        new AccountUpdatedEvent(
            account.getAccountId(),
            account.getOwnerName(),
            account.getBalance(),
            LocalDateTime.now());

    domainEventPublisher.publish(
        Account.class, account.getAccountId(), Collections.singletonList(event));
  }

  public void publishAccountClosedEvent(Account account, String closureReason) {
    AccountClosedEvent event =
        new AccountClosedEvent(
            account.getAccountId(),
            account.getOwnerName(),
            account.getBalance(),
            LocalDateTime.now(),
            closureReason);

    domainEventPublisher.publish(
        Account.class, account.getAccountId(), Collections.singletonList(event));
  }
}
