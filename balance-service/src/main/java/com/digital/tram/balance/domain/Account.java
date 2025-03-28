package com.digital.tram.balance.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"version"})
public class Account {
  @Id private String accountId;

  private String ownerName;

  private BigDecimal balance;

  private boolean closed = false;

  @Version private Long version;

  public Account(String accountId, String ownerName, BigDecimal initialBalance) {
    this.accountId = accountId;
    this.ownerName = ownerName;
    this.balance = initialBalance;
    this.closed = false;
  }

  public void credit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }

  public void debit(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }

  public boolean hasSufficientBalance(BigDecimal amount) {
    return this.balance.compareTo(amount) >= 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Account account)) return false;
    return accountId != null && accountId.equals(account.accountId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}