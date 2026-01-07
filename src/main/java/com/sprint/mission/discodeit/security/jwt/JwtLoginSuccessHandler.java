package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.Auth.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails){
            try {
                String accessToken = tokenProvider.createAccessToken(userDetails);
                String refreshToken = tokenProvider.createRefreshToken(userDetails);

                Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(refreshToken);
                response.addCookie(refreshCookie);

                JwtDto jwtDto = new JwtDto(
                        userDetails.getUserDto(),
                        accessToken
                );

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                jwtRegistry.registerJwtInformation(
                        new JwtInformation(
                                userDetails.getUserDto(),
                                accessToken,
                                refreshToken
                        )
                );
                log.info("JWT access and refresh tokens issued for user: {}", userDetails.getUsername());
            } catch (JOSEException e) {
                log.error("Failed to generate JWT token for user: {}", userDetails.getUsername(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                ErrorResponse errorResponse = ErrorResponse.of(
                        new RuntimeException("토큰 생성 실패"),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        }else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = ErrorResponse.of(
                    new RuntimeException("Authentication failed: 유효하지 않은 유저 정보"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}