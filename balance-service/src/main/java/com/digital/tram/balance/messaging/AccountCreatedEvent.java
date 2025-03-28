package com.digital.tram.balance.messaging;

import io.eventuate.tram.events.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent implements DomainEvent {
  private String accountId;
  private String ownerName;
  private BigDecimal initialBalance;
  private String createdAt;

  // Add a constructor that accepts LocalDateTime
  public AccountCreatedEvent(
      String accountId, String ownerName, BigDecimal initialBalance, LocalDateTime createdAt) {
    this.accountId = accountId;
    this.ownerName = ownerName;
    this.initialBalance = initialBalance;
    this.createdAt = createdAt.toString(); // Convert to String
  }
}
