package com.digital.tram.transaction.messaging.account;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Account Closed Event
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountClosedEvent implements DomainEvent {
    private String accountId;
    private String ownerName;
    private BigDecimal finalBalance;
    private LocalDateTime closedAt;
    private String closureReason;
}