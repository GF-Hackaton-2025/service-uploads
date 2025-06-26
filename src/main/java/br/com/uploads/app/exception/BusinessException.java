package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  private final HttpStatus status;

  public BusinessException(String message) {
    super(message);
    this.status = HttpStatus.UNPROCESSABLE_ENTITY;
  }

}
