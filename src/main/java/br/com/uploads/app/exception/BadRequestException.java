package br.com.uploads.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends RuntimeException {

  private final HttpStatus status;

  public BadRequestException(HttpStatus status) {
    this.status = status;
  }

}
