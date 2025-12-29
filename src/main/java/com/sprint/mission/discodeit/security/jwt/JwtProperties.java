package com.sprint.mission.discodeit.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {
    private String secret;
    private long expiration;
    private String issuer;
}
