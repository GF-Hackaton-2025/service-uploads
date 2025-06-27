package br.com.uploads.app.exception.handler;

import br.com.uploads.app.exception.BadRequestException;
import br.com.uploads.app.exception.BusinessException;
import br.com.uploads.app.exception.ForbiddenException;
import br.com.uploads.app.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.core.MethodParameter;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;

class ExceptionAdviceTest {

  @InjectMocks
  private ExceptionAdvice exceptionAdvice;

  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = openMocks(this);
  }

  @AfterEach
  void closeService() throws Exception {
    closeable.close();
  }

  private String extractResponseBody(MockServerHttpResponse response) {
    return response.getBodyAsString().block();
  }

  private ServerWebExchange createExchange() {
    return MockServerWebExchange.from(get("/test").build());
  }

  @Test
  void shouldHandleBusinessException() {
    var exchange = createExchange();
    var ex = new BusinessException("Business rule broken");

    Mono<Void> result = exceptionAdvice.handle(exchange, ex);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("Business rule broken");
  }

  @Test
  void shouldHandleBadRequestException() {
    var exchange = createExchange();
    var ex = new BadRequestException(BAD_REQUEST);

    Mono<Void> result = exceptionAdvice.handle(exchange, ex);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("Bad Request");
  }

  @Test
  void shouldHandleForbiddenException() {
    var exchange = createExchange();
    var ex = new ForbiddenException(FORBIDDEN);

    Mono<Void> result = exceptionAdvice.handle(exchange, ex);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("Forbidden");
  }

  @Test
  void shouldHandleUnauthorizedException() {
    var exchange = createExchange();
    var ex = new UnauthorizedException("Not authorized");

    Mono<Void> result = exceptionAdvice.handle(exchange, ex);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("Not authorized");
  }

  @Test
  void shouldHandleGenericException() {
    var exchange = createExchange();
    var ex = new RuntimeException("Generic error");

    Mono<Void> result = exceptionAdvice.handle(exchange, ex);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("Generic error");
  }

  @Test
  void shouldHandleWebExchangeBindException() throws NoSuchMethodException {
    var exchange = createExchange();

    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
    bindingResult.addError(new FieldError("target", "fieldName", "must not be null"));

    MethodParameter param = new MethodParameter(this.getClass().getDeclaredMethod("shouldHandleWebExchangeBindException"), -1);
    WebExchangeBindException exception = new WebExchangeBindException(param, bindingResult);

    Mono<Void> result = exceptionAdvice.handle(exchange, exception);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("must not be null");
  }

  @Test
  void shouldHandleMethodArgumentNotValidException() throws NoSuchMethodException {
    var exchange = createExchange();

    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
    bindingResult.addError(new FieldError("target", "email", "must be a valid email"));

    MethodParameter param = new MethodParameter(this.getClass().getDeclaredMethod("shouldHandleMethodArgumentNotValidException"), -1);
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(param, bindingResult);

    Mono<Void> result = exceptionAdvice.handle(exchange, exception);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("must be a valid email");
  }

  @Test
  void shouldHandleMissingRequestValueException() throws NoSuchMethodException {
    var exchange = createExchange();
    MethodParameter param = new MethodParameter(this.getClass().getDeclaredMethod("shouldHandleMissingRequestValueException"), -1);

    var exception = new MissingRequestValueException(
      "id",
      String.class,
      "query parameter",
      param
    );

    Mono<Void> result = exceptionAdvice.handle(exchange, exception);

    StepVerifier.create(result).verifyComplete();
    String responseBody = extractResponseBody((MockServerHttpResponse) exchange.getResponse());
    assertThat(responseBody).contains("query parameter");
  }

}

