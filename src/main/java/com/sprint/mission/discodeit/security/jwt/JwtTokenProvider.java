package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private JWSSigner jwsSigner;
    private JWSVerifier jwsVerifier;

    @PostConstruct
    public void init() {
        try {
            byte[] secretKey = jwtProperties.getSecret().getBytes();
            this.jwsSigner = new MACSigner(secretKey);
            this.jwsVerifier = new MACVerifier(secretKey);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public String createAccessToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getAccessTokenValidityInMs());
    }

    public String createRefreshToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getRefreshTokenValidityInMs());
    }

    public String generateToken(String username, String role, long validityInMs) {
        try {
            Date now = new Date();
            Date expirationDate = new Date(
                now.getTime() + validityInMs);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(jwtProperties.getIssuer())
                .subject(username)
                .issueTime(now)
                .expirationTime(expirationDate)
                .claim("role", role)
                .build();

            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
            );

            signedJWT.sign(jwsSigner);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public JWTClaimsSet getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(jwsVerifier)) {
                throw new RuntimeException("Invalid JWT signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime != null && expirationTime.before(new Date())) {
                throw new RuntimeException("JWT token has expired");
            }
            return claims;
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
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
