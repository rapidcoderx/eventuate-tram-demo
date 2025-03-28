package com.digital.tram.balance.messaging;

import io.eventuate.tram.events.common.DomainEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdatedEvent implements DomainEvent {
  private String accountId;
  private String ownerName;
  private BigDecimal newBalance;
  private String updatedAt; // Changed from LocalDateTime

  // Add a constructor that accepts LocalDateTime
  public AccountUpdatedEvent(String accountId, String ownerName, BigDecimal newBalance,
                             LocalDateTime updatedAt) {
    this.accountId = accountId;
    this.ownerName = ownerName;
    this.newBalance = newBalance;
    this.updatedAt = updatedAt.toString(); // Convert to String
  }
}