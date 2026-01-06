package com.sprint.mission.discodeit.security.filter;

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
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        // 토큰 검증
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            JWTClaimsSet claims = jwtTokenProvider.getClaims(token);
            String username = claims.getSubject();
            String role = (String) claims.getClaim("role");

            if (isTokenValidInRegistry(token)){

                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

                UserDetails userDetails = User.builder()
                        .username(username)
                        .password("")
                        .authorities(authorities)
                        .build();

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

        }
        filterChain.doFilter(request, response); // 다음 필터로 넘김
    }

    // 토큰 상태 검증
    private boolean isTokenValidInRegistry(String token) {
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(token)) { // 서비스 로직에 맞는 메서드 호출
            log.warn("유효하지 않은 토큰입니다.");
            return false;
        }
        return true;
    }

    // 헤더 형식 검증 - 요청 헤더에 Bearer 토큰이 포함된 경우에만 인증을 시도
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // 접두사 "Bearer "를 떼내고 JWT 문자열만 추출
        }
        return null;
    }

}
