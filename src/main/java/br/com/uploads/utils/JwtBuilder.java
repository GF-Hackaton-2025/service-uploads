package br.com.uploads.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class JwtBuilder {

  public String create(Algorithm algorithm, String issuer, Consumer<JWTCreator.Builder> adapter) {
    JWTCreator.Builder builder = JWT.create();
    builder.withIssuer(issuer);
    adapter.accept(builder);
    return builder.sign(algorithm);
  }

  public DecodedJWT decode(Algorithm algorithm, String issuer, String token) {
    Verification verification = JWT.require(algorithm);
    verification.withIssuer(issuer);
    JWTVerifier verifier = verification.build();
    return verifier.verify(token);
  }

  public <T> T claim(Algorithm algorithm, String issuer, String token, String key, Class<T> type) {
    DecodedJWT jwt = decode(algorithm, issuer, token);
    Claim claim = jwt.getClaim(key);
    return (T)(claim != null ? claim.as(type) : null);
  }
}
