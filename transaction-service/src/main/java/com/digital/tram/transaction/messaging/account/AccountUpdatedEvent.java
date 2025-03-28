package com.digital.tram.transaction.messaging.account;


import io.eventuate.tram.events.common.DomainEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Account Updated Event
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdatedEvent implements DomainEvent {
    private String accountId;
    private String ownerName;
    private BigDecimal newBalance;
    private LocalDateTime updatedAt;
}
