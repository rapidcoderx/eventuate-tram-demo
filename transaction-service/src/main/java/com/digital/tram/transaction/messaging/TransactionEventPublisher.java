package com.digital.tram.transaction.messaging;

import com.digital.tram.transaction.domain.Transaction;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class TransactionEventPublisher {

    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public TransactionEventPublisher(DomainEventPublisher domainEventPublisher) {
        this.domainEventPublisher = domainEventPublisher;
    }

    public void publishTransactionCreatedEvent(Transaction transaction) {
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                transaction.getTransactionId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription()
        );

        domainEventPublisher.publish(Transaction.class, transaction.getTransactionId(),
                Collections.singletonList(event));
    }
}