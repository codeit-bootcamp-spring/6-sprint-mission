package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

        // 세션이 만들어진 뒤 세션 ID를 헤더로 내려준다
        String sessionId = request.getSession(true).getId();
        response.setHeader("JSESSIONID", sessionId);

        // CSRF 토큰을 Set-Cookie로 내려준다 (JS가 읽을 수 있도록 HttpOnly=false)
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            ResponseCookie xsrfCookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
                .httpOnly(false)
                .path("/")
                .build();
            response.addHeader("Set-Cookie", xsrfCookie.toString());
        }

        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        ServletOutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, principal.getUserDto());
        outputStream.flush();
    }
}
