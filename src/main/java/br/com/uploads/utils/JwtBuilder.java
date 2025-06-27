package br.com.uploads.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtBuilder {

  public DecodedJWT decode(Algorithm algorithm, String issuer, String token) {
    Verification verification = JWT.require(algorithm);
    verification.withIssuer(issuer);
    JWTVerifier verifier = verification.build();
    return verifier.verify(token);
  }

}
