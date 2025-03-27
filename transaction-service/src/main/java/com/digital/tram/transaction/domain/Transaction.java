package com.digital.tram.transaction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String transactionId;

    private String accountId;
    private BigDecimal amount;
    private String type; // PAYMENT or DEPOSIT
    private String description;
    private String status; // PENDING, COMPLETED, FAILED
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public Transaction(String transactionId, String accountId, BigDecimal amount,
                       String type, String description) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}