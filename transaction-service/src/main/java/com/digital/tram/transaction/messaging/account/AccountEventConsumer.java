package com.digital.tram.transaction.messaging.account;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountEventConsumer {
  private static final Logger logger = LoggerFactory.getLogger(AccountEventConsumer.class);

  private final DomainEventDispatcherFactory domainEventDispatcherFactory;
  private final AccountEventHandler accountEventHandler;

  @Autowired
  public AccountEventConsumer(
      DomainEventDispatcherFactory domainEventDispatcherFactory,
      AccountEventHandler accountEventHandler) {
    this.domainEventDispatcherFactory = domainEventDispatcherFactory;
    this.accountEventHandler = accountEventHandler;
    logger.info(
        "Initialized AccountEventConsumer with dependencies:  DomainEventDispatcherFactory={}, AccountEventHandler={}",
        domainEventDispatcherFactory != null ? "available" : "null",
        accountEventHandler != null ? "available" : "null");
  }

  @Bean
  public DomainEventDispatcher accountEventDispatcher() {
    logger.info("Creating accountEventDispatcher bean");
    return domainEventDispatcherFactory.make(
        "accountEventDispatcher", accountEventHandler.domainEventHandlers());
  }
}
