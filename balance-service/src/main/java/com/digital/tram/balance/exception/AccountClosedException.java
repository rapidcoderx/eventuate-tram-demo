package com.digital.tram.balance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountClosedException extends RuntimeException {
  public AccountClosedException(String message) {
    super(message);
  }
}
