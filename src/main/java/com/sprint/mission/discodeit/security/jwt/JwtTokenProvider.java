package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public final class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private JWSSigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    private void init() {
        String jwtSecret = this.jwtProperties.getSecret();
        try {
            this.signer = new MACSigner(jwtSecret);
            this.verifier = new MACVerifier(jwtSecret);
        } catch (JOSEException e) {
            log.error("JWT Initialization Error. secret length={}", jwtSecret.length());
            JwtException jwtException = new JwtException(ErrorCode.INTERNAL_SERVER_ERROR);
            jwtException.addDetail("message", "Error Initializing JWT");
            throw jwtException;
        }
    }

    public String createAccessToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getAccessTokenValiditySeconds());
    }

    public String createRefreshToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getRefreshTokenValiditySeconds());
    }

    public String generateToken(String username, String role, long validitySeconds) {
        try {
            Date now = new Date();
            Date expirationTime = new Date(now.getTime() + validitySeconds * 1000);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(jwtProperties.getIssuer())
                    .expirationTime(expirationTime)
                    .issueTime(now)
                    .subject(username)
                    .claim(JwtClaimNames.role.name(), role)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("JWT Token Signing Failed. signer={}", this.signer);
            JwtException jwtException = new JwtException(ErrorCode.INTERNAL_SERVER_ERROR);
            jwtException.addDetail("message", "Error Generating Token");
            throw jwtException;
        }
    }

    public JWTClaimsSet getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(verifier)) {
                log.error("Token Verification Error. Verify=false, Token={}", token);
                throw new JwtException(ErrorCode.JWT_SIGNATURE_INVALID);
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expirationTime = claimsSet.getExpirationTime();

            if (expirationTime != null && expirationTime.before(new Date())) {
                log.error("Expired JWT Token. Expiration={}", expirationTime);
                throw new JwtException(ErrorCode.JWT_TOKEN_EXPIRED);
            }

            return claimsSet;
        } catch (ParseException e) {
            log.error("Token Parsing Failed. Token={}", token);
            throw new JwtException(ErrorCode.JWT_TOKEN_INVALID);
        } catch (JOSEException e) {
            log.error("SignedJWT Couldn't be verified. token={}", token);
            throw new JwtException(ErrorCode.JWT_TOKEN_INVALID);
        }
    }

    public boolean validateToken(String token) {
        JWTClaimsSet claimsSet = getClaims(token);
        return true;
    }
}
