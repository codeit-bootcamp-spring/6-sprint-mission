package com.sprint.mission.discodeit.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  @Value("${jwt.access-token.secret}")
  private String accessSecret;
  @Value("${jwt.access-token.expiration}")
  private long accessExpiration;
  @Value("${jwt.refresh-token.secret}")
  private String refreshSecret;
  @Value("${jwt.refresh-token.expiration}")
  private long refreshExpiration;
  @Value("${jwt.issuer}")
  private String issuer;

}
