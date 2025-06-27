package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {

  private final HttpStatus status;

  public ForbiddenException(HttpStatus status) {
    this.status = status;
  }

}
