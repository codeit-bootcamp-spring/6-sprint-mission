package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.TokenInfo;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;

    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

    public JwtTokenProvider(
            @Value("${discodeit.jwt.access-token.secret}") String accessTokenSecret,
            @Value("${discodeit.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
            @Value("${discodeit.jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${discodeit.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs)
            throws JOSEException {

        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    }

    public String createAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String createRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    public String generateToken(DiscodeitUserDetails userDetails, int validityInMs,
                                JWSSigner signer, String tokenType) throws JOSEException {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + validityInMs);

        String tokenId = UUID.randomUUID().toString();
        UserDto userDto = userDetails.getUserDto();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDto.username())
                .jwtID(tokenId)
                .claim("userId", userDto.id().toString())
                .claim("type", tokenType)
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issueTime(now)
                .expirationTime(expirationTime)
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public JWTClaimsSet getClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(accessTokenVerifier)) {
                throw new AuthException(ErrorCode.INVALID_AUTH);
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();

            if (expirationTime != null && expirationTime.before(new Date())) {
                throw new AuthException(ErrorCode.INVALID_AUTH);
            }
            return claims;
        } catch (ParseException | JOSEException e) {
            log.error(e.getMessage());
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