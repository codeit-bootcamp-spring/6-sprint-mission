package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.Auth.JwtDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDto userDto = (UserDto) authentication.getPrincipal();
        userRepository.findById(userDto.id()).orElseThrow(UserNotFoundException::new);


        String accessToken = jwtTokenProvider.createAccessToken(userDto.username(), userDto.role().name());
        String refreshToken =  jwtTokenProvider.createRefreshToken(userDto.username(), userDto.role().name());

        JwtInformation jwtInfo = new JwtInformation(userDto, accessToken, refreshToken);
        jwtRegistry.registerJwtInformation(jwtInfo);

        Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshCookie);

        JwtDto jwtDto = new JwtDto(userDto, accessToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    }
}
