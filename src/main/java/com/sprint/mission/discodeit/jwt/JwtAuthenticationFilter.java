package com.sprint.mission.discodeit.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.utils.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiscodeitUserDetailsService userDetailsService;
    private final JwtRegistry jwtRegistry;
    private final TokenUtils tokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = tokenUtils.getTokenFromRequest(request);
        JWTClaimsSet claimsSet = jwtTokenProvider.parseToken(token);

        if (token == null || claimsSet == null
                || jwtRegistry.hasActiveJwtInformationByAccessToken(token) == false) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails details = userDetailsService.loadUserByUsername(claimsSet.getSubject());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
