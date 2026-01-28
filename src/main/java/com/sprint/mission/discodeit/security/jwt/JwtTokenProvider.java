package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private JWSSigner signer;
    private JWSVerifier verifier;

    // 토큰 서명과 검증에 필요한 암호화 도구를 초기화
    @PostConstruct
    public void init() {
        try {
            byte[] secretKey = jwtProperties.getSecret().getBytes();
            this.signer = new MACSigner(secretKey); // 토큰에 서명하기 위한 객체
            this.verifier = new MACVerifier(secretKey); // 변조 여부 검증하기 위한 객체
        } catch (JOSEException e) {
            throw new RuntimeException("JWT Key Init Failed", e);
        }
    }

    public String createAccessToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getAccessTokenValidityInMs());
    }

    public String createRefreshToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getRefreshTokenValidityInMs());
    }

    // JWT 토큰 생성
    public String generateToken(String username, String role, long validityInMilliseconds) {
        try {
            Date now = new Date();
            Date expirationTime = new Date(now.getTime() + validityInMilliseconds);

            // Claims 구성
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(jwtProperties.getIssuer())
                    .subject(username)
                    .issueTime(now)
                    .expirationTime(expirationTime)
                    .claim("role", role)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256), // 헤더 설정
                    claimsSet
            );

            signedJWT.sign(signer); // 서명
            return signedJWT.serialize();

        } catch (JOSEException e) {
            log.error("Error generating token", e);
            throw new RuntimeException("Error generating token", e);
        }
    }

    // 토큰 내부 데이터(Claims) 추출
    public JWTClaimsSet getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid JWT Signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();

            if (expirationTime != null && expirationTime.before(new Date())) {
                throw new RuntimeException("Expired JWT Token");
            }

            return claims;
        } catch (ParseException | JOSEException e) {
            log.error("Invalid Token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT Token", e);
        }
    }

    // 토큰 유효성 검사
    public boolean validateAccessToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            // 필요 시 토큰 타입 체크 추가
            return claims.getStringClaim("type").equals("ACCESS");
        } catch (Exception e) {
            return false;
        }
    }

    // Refresh Token 전용 검증
    public boolean validateRefreshToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            return claims.getStringClaim("type").equals("REFRESH");
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            JWTClaimsSet claims = getClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid Token while extracting username", e);
        }
    }

}
