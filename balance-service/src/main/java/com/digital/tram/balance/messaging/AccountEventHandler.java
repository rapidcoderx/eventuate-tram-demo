package com.digital.tram.balance.messaging;

import com.digital.tram.balance.service.AccountService;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccountEventHandler.class);
    private final AccountService accountService;

    @Autowired
    public AccountEventHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("com.digital.tram.transaction.domain.Transaction")
                .onEvent(TransactionCreatedEvent.class, this::handleTransactionCreatedEvent)
                .build();
    }

    private void handleTransactionCreatedEvent(DomainEventEnvelope<TransactionCreatedEvent> eventEnvelope) {
        TransactionCreatedEvent event = eventEnvelope.getEvent();
        logger.info("Received transaction event: {}", event);

        String accountId = event.getAccountId();
        BigDecimal amount = event.getAmount();

        // For a payment transaction, debit the account
        // For a deposit transaction, credit the account
        boolean isCredit = "DEPOSIT".equals(event.getType());

        accountService.updateBalance(accountId, amount, isCredit);
        logger.info("Updated balance for account {}, amount: {}, isCredit: {}",
                accountId, amount, isCredit);
    }
}