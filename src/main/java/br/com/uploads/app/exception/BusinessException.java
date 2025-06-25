package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  private final HttpStatus status;

  public BusinessException(HttpStatus status) {
    this.status = status;
  }

  public BusinessException(String message) {
    super(message);
    this.status = HttpStatus.UNPROCESSABLE_ENTITY;
  }

  public BusinessException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public BusinessException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public BusinessException(Throwable cause, HttpStatus status) {
    super(cause);
    this.status = status;
  }

  public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.status = status;
  }
}
