package com.digital.tram.balance.service;

import com.digital.tram.balance.domain.Account;
import com.digital.tram.balance.domain.AccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(String ownerName, BigDecimal initialBalance) {
        String accountId = UUID.randomUUID().toString();
        Account account = new Account(accountId, ownerName, initialBalance);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccount(String accountId) {
        return accountRepository.findByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public boolean validateAccount(String accountId, BigDecimal amount) {
        Optional<Account> accountOptional = accountRepository.findByAccountId(accountId);
        return accountOptional.map(account -> account.hasSufficientBalance(amount)).orElse(false);
    }

    @Transactional
    public void updateBalance(String accountId, BigDecimal amount, boolean isCredit) {
        accountRepository.findByAccountId(accountId).ifPresent(account -> {
            if (isCredit) {
                account.credit(amount);
            } else {
                account.debit(amount);
            }
            accountRepository.save(account);
        });
    }
}