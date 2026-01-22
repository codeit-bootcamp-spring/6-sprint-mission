package com.sprint.mission.discodeit.filter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        // 엑세스 토큰 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token)) { // 토큰 서명 유효성, 만료 여부 검증
            JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
            if (claims == null) {
                log.error("Unable to read Claims from token");
                filterChain.doFilter(request, response);
                return;
            }
            String username = claims.getSubject();
            String role = (String) claims.getClaim("role");

            if (StringUtils.hasText(username) && role != null) {
                if (isTokenValidInRegistry(token)) {

//                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + role));

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated user: {}", username);
                } else {
                    log.warn("Invalid token");
                }

            } else {
                log.warn("토큰 내에 필수 사용자 정보가 누락되었습니다. (subject: {}, role: {})", username, role);
            }
        }
        filterChain.doFilter(request, response); // 다음 필터로 넘김
    }

    // 로그아웃 요청은 제외 (JwtAuthenticationFilter가 LogoutFilter보다 먼저 실행되므로)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/auth/logout");
    }

    // 토큰 추출 - 요청 헤더에 Bearer로 시작하는 토큰 문자열을 꺼냄.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // 접두사 "Bearer "를 떼내고 JWT 문자열만 추출
        }
        return null;
    }

    // 토큰 상태 검증
    private boolean isTokenValidInRegistry(String token) {
        if (jwtRegistry.hasActiveJwtInformationByAccessToken(token)) { // 서비스 로직에 맞는 메서드 호출
            return true;
        } else {
            log.warn("유효하지 않은 토큰입니다.");
            return false;
        }
    }


}
