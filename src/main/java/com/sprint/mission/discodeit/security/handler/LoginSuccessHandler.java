package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.principal.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. 인증된 유저 정보 꺼내기 (UserDetails 구현체)
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        UserResponseDto userResponseDto = userDetails.getUserResponseDto();

        // 2. HTTP 응답 설정
        response.setStatus(HttpServletResponse.SC_OK); // 200 OK
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3. DTO를 JSON으로 변환하여 응답 바디에 쓰기
        String result = objectMapper.writeValueAsString(userResponseDto);
        response.getWriter().write(result);
    }

}
