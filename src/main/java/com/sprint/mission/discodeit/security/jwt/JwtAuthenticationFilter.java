package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.common.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;
  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String HEADER_AUTHORIZATION = "Authorization";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = resolveToken(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

      JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
      String username = claims.getSubject();

//      String roleString = (String) claims.getClaim("role");
//      Role role = Role.valueOf(roleString);
//      List<SimpleGrantedAuthority> authorities = Collections.singletonList(
//          new SimpleGrantedAuthority("ROLE_" + role)
//      );
//      UserDetails userDetails = new User(username, "", authorities);

      // stateless jwtToken을 가져와 db조회를 하고 userDetails를 생성
      DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());

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
