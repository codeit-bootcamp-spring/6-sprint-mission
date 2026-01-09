package com.sprint.mission.discodeit.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String issuer;
  private String secret;
  private long accessTokenValidityInMs;
  private long refreshTokenValidityInMs;
}
