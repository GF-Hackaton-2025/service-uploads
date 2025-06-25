package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedException extends RuntimeException {

  private final HttpStatus status;

  public UnauthorizedException(HttpStatus status) {
    this.status = status;
  }

  public UnauthorizedException(String message) {
    super(message);
    this.status = HttpStatus.UNAUTHORIZED;
  }

  public UnauthorizedException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public UnauthorizedException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public UnauthorizedException(Throwable cause, HttpStatus status) {
    super(cause);
    this.status = status;
  }

  public UnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.status = status;
  }
}
