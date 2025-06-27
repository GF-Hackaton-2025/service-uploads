package br.com.uploads.config;

import br.com.uploads.app.exception.UnauthorizedException;
import br.com.uploads.utils.JwtBuilder;
import br.com.uploads.webui.constants.Errors;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@Component
public class HandlerInterceptor implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return Mono.just(exchange.getRequest().getURI().getPath())
      .filter(path -> startsWith(path, "/api/v1"))
      .flatMap(path -> validateToken(exchange))
      .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)))
      .then();
  }

  private Mono<Void> validateToken(ServerWebExchange exchange) {
    var token = exchange.getRequest().getHeaders().getFirst("Authorization");

    if (isEmpty(token))
      return Mono.error(new UnauthorizedException(Errors.ERROR_UNAUTHORIZED));

    if (!token.startsWith("Bearer "))
      return Mono.error(new UnauthorizedException(Errors.ERROR_NO_JWT_TOKEN));

    var accessToken = token.substring(7);
    JwtBuilder.decode(JwtConfig.algorithm, JwtConfig.issuer, accessToken);
    return Mono.empty();
  }
}
