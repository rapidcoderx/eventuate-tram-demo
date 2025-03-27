package com.digital.tram.balance.messaging;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountEventConsumer {

  private final MessageConsumer messageConsumer;
  private final DomainEventDispatcherFactory domainEventDispatcherFactory;
  private final AccountEventHandler accountEventHandler;

  @Autowired
  public AccountEventConsumer(
      MessageConsumer messageConsumer,
      DomainEventDispatcherFactory domainEventDispatcherFactory,
      AccountEventHandler accountEventHandler) {
    this.messageConsumer = messageConsumer;
    this.domainEventDispatcherFactory = domainEventDispatcherFactory;
    this.accountEventHandler = accountEventHandler;
  }

  @Bean
  public DomainEventDispatcher domainEventDispatcher() {
    return domainEventDispatcherFactory.make(
        "accountEventDispatcher", accountEventHandler.domainEventHandlers());
  }
}
