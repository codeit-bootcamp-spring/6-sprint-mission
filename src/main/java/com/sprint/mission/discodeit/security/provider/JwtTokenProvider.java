package com.sprint.mission.discodeit.security.provider;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.config.JwtProperties;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;
  private JWSSigner signer;
  private JWSVerifier verifier;

  public void init() {

    try {
      JWSSigner signer = new MACSigner(jwtProperties.getAccessSecret());
      JWSVerifier verifier = new MACVerifier(jwtProperties.getAccessSecret());
    } catch (JOSEException e) {
      log.error("JWT init failed.", e);
      throw new RuntimeException(e);
    }

  }

  public String generateToken(String username, String role) {

    //Generate JWT claim
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .issuer(jwtProperties.getIssuer())
        .subject(username)
        .claim("role", role)
        .build();

    // Generate JWT token
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    try {
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      log.error("JWT sign failed.", e);
      throw new RuntimeException(e);
    }

  }

  public JWTClaimsSet getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet();
    } catch (ParseException e) {
      log.error("JWT parse failed.", e);
      throw new RuntimeException(e);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.verify(verifier);
    } catch (ParseException | JOSEException e) {
      log.error("JWT validation failed.", e);
      return false;
    }
  }

  public String renewToken(String token) {
    try {
      JWTClaimsSet claims = getClaims(token);
      String username = claims.getSubject();
      String role = claims.getClaim("role").toString();
      return generateToken(username, role);
    } catch (Exception e) {
      log.error("JWT renewal failed.", e);
      throw new RuntimeException(e);
    }
  }

}
