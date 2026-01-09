package com.sprint.mission.discodeit.security.jwt;

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
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;
  private JWSSigner signer;
  private JWSVerifier verifier;

  @PostConstruct
  public void init() {
    try {
      byte[] secretkey = jwtProperties.getSecret().getBytes();
      this.signer = new MACSigner(secretkey);
      this.verifier = new MACVerifier(secretkey);

    } catch (KeyLengthException e) {
      throw new RuntimeException("JWT Secret Key의 길이가 올바르지 않습니다. 최소 256비트(32바이트) 길이여야 합니다.", e);
    } catch (JOSEException e) {
      throw new RuntimeException("JWT 토큰 생성 및 검증을 초기화하는 중에 오류가 발생했습니다.", e);
    }
  }

  public String createAccessToken(DiscodeitUserDetails userDetails) {
    return generateToken(userDetails, jwtProperties.getAccessTokenValidityInMs());
  }

  public String createRefreshToken(DiscodeitUserDetails userDetails) {
    return generateToken(userDetails, jwtProperties.getRefreshTokenValidityInMs());
  }

  // 인증 성공 이후 토큰 생성
  public String generateToken(DiscodeitUserDetails userDetails, long validityInMs) {
    try {
      Date now = new Date();
      Date expirationTime = new Date(now.getTime() + validityInMs);

      JWTClaimsSet cliamsSet = new JWTClaimsSet.Builder()
          .issuer(jwtProperties.getIssuer())
          .subject(userDetails.getUsername())
          .issueTime(now)
          .expirationTime(expirationTime)
          .claim("role", userDetails.getRole().name())
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          cliamsSet
      );

      signedJWT.sign(signer);
      return signedJWT.serialize();

    } catch (JOSEException e) {
      throw new RuntimeException("JWT 토큰 생성 중에 오류가 발생했습니다.", e);
    }
  }

  public JWTClaimsSet getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("JWT 토큰 검증에 실패했습니다.");
      }

      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
      Date expirationTime = claims.getExpirationTime();

      if (expirationTime != null && expirationTime.before(new Date())) {
        throw new RuntimeException("JWT 토큰이 만료되었습니다.");
      }

      return claims;

    } catch (JOSEException e) {
      throw new RuntimeException("JWT 토큰 검증 중에 오류가 발생했습니다.", e);
    } catch (ParseException e) {
      throw new RuntimeException("JWT 토큰 파싱 중에 오류가 발생했습니다.", e);
    }
  }

  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
