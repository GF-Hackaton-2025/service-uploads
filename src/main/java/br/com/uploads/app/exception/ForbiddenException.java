package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {

  private final HttpStatus status;

  public ForbiddenException(HttpStatus status) {
    this.status = status;
  }

  public ForbiddenException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public ForbiddenException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public ForbiddenException(Throwable cause, HttpStatus status) {
    super(cause);
    this.status = status;
  }

  public ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.status = status;
  }
}
