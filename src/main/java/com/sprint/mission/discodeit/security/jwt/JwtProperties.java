package com.sprint.mission.discodeit.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {
    private String secret;
    private String issuer;
    private long accessTokenValidityInMs;
    private long refreshTokenValidityInMs;
}
