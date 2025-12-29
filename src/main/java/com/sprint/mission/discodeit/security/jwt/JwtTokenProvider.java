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

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private JWSSigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    public void init() {
        try{
            byte[] secretKey = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
            this.signer = new MACSigner(secretKey);
            this.verifier = new MACVerifier(secretKey);
        }catch(Exception e){
            //이거 커스텀 에러로 변경
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String userName, String role){
        try{
           Date now = new Date();
           Date expirationTime = new Date(now.getTime() + jwtProperties.getExpiration());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(jwtProperties.getIssuer())
                    .subject(userName)
                    .issueTime(now)
                    .expirationTime(expirationTime)
                    .claim("role",role)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();
        }catch(JOSEException e){
            //이거 커스텀 에러로 변경
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public JWTClaimsSet getClaims(String token){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if(!signedJWT.verify(verifier)){
                //이거 커스텀 에러로 변경
                throw new RuntimeException("Invalid JWT Signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();

            if(expirationTime != null && expirationTime.before(new Date())){
                //이거 커스텀 에러로 변경
                throw new RuntimeException("Expired JWT Token");
            }
            return claims;
        }catch(ParseException | JOSEException e){
            //이거 커스텀 에러로 변경
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token){
        try{
            getClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
