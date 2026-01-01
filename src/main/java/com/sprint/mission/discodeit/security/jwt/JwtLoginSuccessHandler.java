package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = authentication.getName();

        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withUsername(username));

        UserDto authenticatedUserDto = userMapper.toDto(authenticatedUser);

        String authenticatedUsername = authenticatedUser.getUsername();
        Role authenticatedUserRole = authenticatedUser.getRole();

        String newAccessToken = jwtTokenProvider.createAccessToken(authenticatedUsername, authenticatedUserRole.name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authenticatedUsername, authenticatedUserRole.name());

        JwtInformation jwtInformation = JwtInformation.builder()
                .refreshToken(newRefreshToken)
                .accessToken(newAccessToken)
                .userDto(authenticatedUserDto)
                .build();

        jwtRegistry.registerJwtInformation(jwtInformation);

        JwtDto jwtDto = new JwtDto(authenticatedUserDto, newAccessToken, null);

        refreshTokenCookieProvider.setNewRefreshTokenCookie(response, newRefreshToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), jwtDto);
    }
}
