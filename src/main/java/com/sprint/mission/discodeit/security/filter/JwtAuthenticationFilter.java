package com.sprint.mission.discodeit.security.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.exception.user.InvalidJwtTokenException;
import com.sprint.mission.discodeit.security.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.registry.JwtRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtRegistry jwtRegistry;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = extractToken(request);

    if (token.isBlank() || !jwtTokenProvider.validateAccessToken(token)) {
      log.error("Invalid access token");
      throw new InvalidJwtTokenException();
    } else {

      if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        log.error("Access token is not active or has been invalidated");
        throw new InvalidJwtTokenException();
      }

      JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
      String username = claims.getSubject();
      String role = claims.getClaim("role").toString();

      List<SimpleGrantedAuthority> authorities = new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority(role)));
      UserDetails userDetails = new User(username, role, authorities);

      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    filterChain.doFilter(request, response);

  }

  private String extractToken(HttpServletRequest request) {
    return request.getHeader("Authorization").replaceFirst("Bearer ", "");
  }

}
