package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends RuntimeException {

  private final HttpStatus status;

  public BadRequestException(HttpStatus status) {
    this.status = status;
  }

  public BadRequestException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public BadRequestException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public BadRequestException(Throwable cause, HttpStatus status) {
    super(cause);
    this.status = status;
  }

  public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.status = status;
  }
}
