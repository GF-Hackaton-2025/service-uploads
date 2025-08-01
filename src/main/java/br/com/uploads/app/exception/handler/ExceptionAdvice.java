package br.com.uploads.app.exception.handler;

import br.com.uploads.app.exception.BadRequestException;
import br.com.uploads.app.exception.BusinessException;
import br.com.uploads.app.exception.ForbiddenException;
import br.com.uploads.app.exception.UnauthorizedException;
import br.com.uploads.webui.domain.ErrorField;
import br.com.uploads.webui.domain.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static br.com.uploads.webui.constants.Constants.YYYY_MM_DD_HH_MM_SS_SSS;
import static br.com.uploads.webui.constants.Errors.ERROR_MESSAGE;
import static br.com.uploads.webui.constants.Errors.VALIDATION_FIELD_ERROR;

@Component
@Order(-2)
public class ExceptionAdvice implements WebExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    if (exchange.getResponse().isCommitted())
      return Mono.error(ex);

    return switch (ex) {
      case BusinessException e -> writeErrorResponse(exchange, e.getStatus(), e.getMessage());
      case BadRequestException e -> writeErrorResponse(exchange, e.getStatus(), e.getMessage());
      case ForbiddenException e -> writeErrorResponse(exchange, e.getStatus(), e.getMessage());
      case UnauthorizedException e -> writeErrorResponse(exchange, e.getStatus(), e.getMessage());
      case WebExchangeBindException e -> handleValidationException(exchange, e);
      case ConstraintViolationException e -> handleConstraintViolationException(exchange, e);
      case MethodArgumentNotValidException e -> handleMethodArgumentNotValidException(exchange, e);
      case MissingRequestValueException e -> handleMissingRequestValueException(exchange, e);
      default -> writeErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    };
  }

  private Mono<Void> handleValidationException(ServerWebExchange request, WebExchangeBindException ex) {
    log.error(ERROR_MESSAGE, ex);

    List<ErrorField> errorList = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(e -> {
      var error = ErrorField.builder()
        .field(e.getField())
        .message(e.getDefaultMessage())
        .build();
      errorList.add(error);
    });

    var errorResponse = ErrorResponse.builder()
      .path(request.getRequest().getPath().value())
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
      .message(VALIDATION_FIELD_ERROR)
      .httpCode(HttpStatus.BAD_REQUEST.value())
      .httpDescription(HttpStatus.BAD_REQUEST.getReasonPhrase())
      .errors(!errorList.isEmpty() ? errorList : null)
      .build();

    return writeJson(request, HttpStatus.BAD_REQUEST, errorResponse);
  }

  private Mono<Void> handleMethodArgumentNotValidException(ServerWebExchange request, MethodArgumentNotValidException ex) {
    log.error(ERROR_MESSAGE, ex);

    List<ErrorField> errorList = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(e -> {
      var error = ErrorField.builder()
        .field(e.getField())
        .message(e.getDefaultMessage())
        .build();
      errorList.add(error);
    });

    var errorResponse = ErrorResponse.builder()
      .path(request.getRequest().getPath().value())
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS)))
      .message(VALIDATION_FIELD_ERROR)
      .httpCode(HttpStatus.BAD_REQUEST.value())
      .httpDescription(HttpStatus.BAD_GATEWAY.getReasonPhrase())
      .errors(!errorList.isEmpty() ? errorList : null)
      .build();

    return writeJson(request, HttpStatus.BAD_REQUEST, errorResponse);
  }

  private Mono<Void> handleMissingRequestValueException(ServerWebExchange request, MissingRequestValueException ex) {
    log.error(ERROR_MESSAGE, ex);

    List<ErrorField> errorList = new ArrayList<>();

    var error = ErrorField.builder()
      .field(ex.getName())
      .message(ex.getReason())
      .build();
    errorList.add(error);

    var errorResponse = ErrorResponse.builder()
      .path(request.getRequest().getPath().value())
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS)))
      .message(VALIDATION_FIELD_ERROR)
      .httpCode(HttpStatus.BAD_REQUEST.value())
      .httpDescription(HttpStatus.BAD_GATEWAY.getReasonPhrase())
      .errors(errorList)
      .build();

    return writeJson(request, HttpStatus.BAD_REQUEST, errorResponse);
  }

  private Mono<Void> handleConstraintViolationException(ServerWebExchange request, ConstraintViolationException ex) {
    log.error(ERROR_MESSAGE, ex);

    List<ErrorField> errorList = new ArrayList<>();
    ex.getConstraintViolations().forEach(e -> {
      var error = ErrorField.builder()
        .field(String.valueOf(e.getPropertyPath().toString().split("\\.")[1]))
        .message(e.getMessage())
        .build();
      errorList.add(error);
    });

    var errorResponse = ErrorResponse.builder()
      .path(request.getRequest().getPath().value())
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS)))
      .message("Validation header/queryParams with error")
      .httpCode(HttpStatus.BAD_REQUEST.value())
      .httpDescription(HttpStatus.BAD_GATEWAY.getReasonPhrase())
      .errors(!errorList.isEmpty() ? errorList : null)
      .build();

    return writeJson(request, HttpStatus.BAD_REQUEST, errorResponse);
  }

  private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
    ErrorResponse errorResponse = ErrorResponse.builder()
      .path(exchange.getRequest().getPath().value())
      .message(message)
      .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS)))
      .httpCode(status.value())
      .httpDescription(status.getReasonPhrase())
      .build();

    return writeJson(exchange, status, errorResponse);
  }

  private Mono<Void> writeJson(ServerWebExchange exchange, HttpStatus status, Object body) {
    try {
      byte[] bytes = objectMapper.writeValueAsBytes(body);
      exchange.getResponse().setStatusCode(status);
      exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    } catch (Exception e) {
      log.error("Error serializing error response", e);
      exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
      byte[] fallback = "{\"error\":\"Internal Server Error\"}".getBytes(StandardCharsets.UTF_8);
      return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(fallback)));
    }
  }

}
