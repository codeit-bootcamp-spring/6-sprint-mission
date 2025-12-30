package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final JwtProperties jwtProperties;

    private static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            response.setStatus(HttpServletResponse.SC_OK);
            UserDto userDto = userDetails.getUserDto();
            String accessToken = jwtTokenProvider.createAccessToken(
                userDto.username(),
                userDto.role().toString()
            );
            JwtDto jwtDto = JwtDto.of(
                userDto,
                accessToken
            );
            String refreshToken = jwtTokenProvider.createRefreshToken(userDto.username(),
                userDto.role().toString());
            jwtRegistry.registerJwtInformation(
                new JwtInformation(userDto, accessToken, refreshToken)
            );
            response.addHeader("Set-Cookie", buildRefreshTokenCookie(refreshToken).toString());
            response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(
                new RuntimeException("Authentication failed: Invalid user details"),
                HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        long maxAgeSeconds = Duration.ofMillis(jwtProperties.getRefreshTokenValidityInMs()).getSeconds();
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
            .httpOnly(true)
            .path("/")
            .maxAge(maxAgeSeconds)
            .build();
    }
}
