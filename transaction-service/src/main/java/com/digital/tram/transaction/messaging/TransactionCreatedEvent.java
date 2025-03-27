package com.digital.tram.transaction.messaging;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreatedEvent implements DomainEvent {
    private String transactionId;
    private String accountId;
    private BigDecimal amount;
    private String type; // PAYMENT or DEPOSIT
    private String description;
}