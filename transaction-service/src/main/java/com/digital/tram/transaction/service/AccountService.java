package com.digital.tram.transaction.service;

import com.digital.tram.transaction.domain.account.AccountCache;
import com.digital.tram.transaction.domain.account.AccountCacheRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AccountService {

  private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

  private final AccountCacheRepository accountCacheRepository;
  private final WebClient webClient;

  @Autowired
  public AccountService(
      AccountCacheRepository accountCacheRepository, WebClient.Builder webClientBuilder) {
    this.accountCacheRepository = accountCacheRepository;
    this.webClient = webClientBuilder.build();
  }

  /**
   * Validates if an account has sufficient balance for a transaction. First tries to use the local
   * cache, and if not available, falls back to the API call.
   */
  @Transactional(readOnly = true)
  public boolean validateAccount(String accountId, BigDecimal amount) {
    // Try to use local cached account data first
    Optional<AccountCache> accountCacheOptional = accountCacheRepository.findByAccountId(accountId);

    if (accountCacheOptional.isPresent()) {
      AccountCache accountCache = accountCacheOptional.get();

      // Don't allow transactions on closed accounts
      if (accountCache.isClosed()) {
        logger.info("Validation failed for account {} - account is closed", accountId);
        return false;
      }

      boolean hasSufficientBalance = accountCache.hasSufficientBalance(amount);
      logger.info(
          "Validating account {} from local cache - has sufficient balance: {}",
          accountId,
          hasSufficientBalance);
      return hasSufficientBalance;
    }

    // Fall back to API call if account not in cache
    logger.info("Account {} not found in local cache, falling back to API call", accountId);
    return validateAccountViaApi(accountId, amount);
  }

  private boolean validateAccountViaApi(String accountId, BigDecimal amount) {
    try {
      Boolean result =
          webClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path("/accounts/{accountId}/validate")
                          .queryParam("amount", amount)
                          .build(accountId))
              .retrieve()
              .bodyToMono(ValidationResponse.class)
              .map(ValidationResponse::isValid)
              .timeout(Duration.ofSeconds(5))
              .onErrorReturn(false)
              .blockOptional()
              .orElse(false);

      logger.info("Validating account {} via API - result: {}", accountId, result);
      return Boolean.TRUE.equals(result);
    } catch (Exception e) {
      logger.error("Error validating account {} via API", accountId, e);
      return false;
    }
  }

  @Getter
  @Setter
  private static class ValidationResponse {
    private boolean valid;
  }
}
