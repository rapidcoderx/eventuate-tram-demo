package com.digital.tram.transaction.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A local representation of an Account from the Balance Service, maintained through event sourcing.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AccountCache {
  @Id private String accountId;

  private String ownerName;

  private BigDecimal balance;

  private boolean closed;

  private LocalDateTime lastUpdated;

  public AccountCache(String accountId, String ownerName, BigDecimal initialBalance) {
    this.accountId = accountId;
    this.ownerName = ownerName;
    this.balance = initialBalance;
    this.closed = false;
    this.lastUpdated = LocalDateTime.now();
  }

  public boolean hasSufficientBalance(BigDecimal amount) {
    return !closed && this.balance.compareTo(amount) >= 0;
  }

  public void updateBalance(BigDecimal newBalance) {
    this.balance = newBalance;
    this.lastUpdated = LocalDateTime.now();
  }

  public void close() {
    this.closed = true;
    this.lastUpdated = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AccountCache that)) return false;
    return accountId != null && accountId.equals(that.accountId);
  }

  @Override
  public int hashCode() {
    return accountId != null ? accountId.hashCode() : 0;
  }
}
