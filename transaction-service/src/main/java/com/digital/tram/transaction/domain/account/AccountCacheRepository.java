package com.digital.tram.transaction.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountCacheRepository extends JpaRepository<AccountCache, String> {
  Optional<AccountCache> findByAccountId(String accountId);
}
