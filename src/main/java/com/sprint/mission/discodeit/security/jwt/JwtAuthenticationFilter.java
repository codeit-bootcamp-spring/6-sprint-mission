package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String HEADER_AUTHORIZATION = "Authorization";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = resolveToken(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

      JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
      String username = claims.getSubject();
      String role = (String) claims.getClaim("role");

      List<SimpleGrantedAuthority> authorities = Collections.singletonList(
          new SimpleGrantedAuthority("ROLE_" + role)
      );

      // stateless 인증 설정
      UserDetails userdetails = new User(username, "", authorities);
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(
              userdetails,
              null,
              userdetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
      // "Bearer " 이후의 토큰 부분 반환
      return bearerToken.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

}
