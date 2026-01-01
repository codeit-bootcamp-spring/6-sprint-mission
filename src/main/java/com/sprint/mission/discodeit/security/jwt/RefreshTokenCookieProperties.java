package com.sprint.mission.discodeit.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt.refresh-token-cookie")
public class RefreshTokenCookieProperties {
    private String name;
    private int maxAge;
    private String path;
    private boolean secure;
    private boolean httpOnly;
}
