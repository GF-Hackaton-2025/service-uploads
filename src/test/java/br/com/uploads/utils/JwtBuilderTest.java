package br.com.uploads.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtBuilderTest {

  private static final String ISSUER = "test-issuer";
  private static final String SECRET = "secret-key";

  @Test
  void shouldDecodeValidToken() {
    Algorithm algorithm = Algorithm.HMAC256(SECRET);
    String token = JWT.create()
      .withIssuer(ISSUER)
      .withSubject("user123")
      .sign(algorithm);

    DecodedJWT decodedJWT = JwtBuilder.decode(algorithm, ISSUER, token);

    assertEquals("user123", decodedJWT.getSubject());
    assertEquals(ISSUER, decodedJWT.getIssuer());
  }

  @Test
  void shouldThrowExceptionForInvalidToken() {
    Algorithm algorithm = Algorithm.HMAC256(SECRET);
    String invalidToken = "invalid.token.value";

    assertThrows(JWTVerificationException.class, () -> {
      JwtBuilder.decode(algorithm, ISSUER, invalidToken);
    });
  }
}

