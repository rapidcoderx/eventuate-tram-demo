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
public class AccountClosedEvent implements DomainEvent {
  private String accountId;
  private String ownerName;
  private BigDecimal finalBalance;
  private String closedAt; // Changed from LocalDateTime
  private String closureReason;

  // Add a constructor that accepts LocalDateTime
  public AccountClosedEvent(
      String accountId,
      String ownerName,
      BigDecimal finalBalance,
      LocalDateTime closedAt,
      String closureReason) {
    this.accountId = accountId;
    this.ownerName = ownerName;
    this.finalBalance = finalBalance;
    this.closedAt = closedAt.toString(); // Convert to String
    this.closureReason = closureReason;
  }
}
