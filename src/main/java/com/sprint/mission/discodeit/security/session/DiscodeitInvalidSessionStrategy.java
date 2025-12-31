package com.sprint.mission.discodeit.security.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.session.InvalidSessionStrategy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
public class DiscodeitInvalidSessionStrategy implements InvalidSessionStrategy {
    private final ObjectMapper mapper;

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Cookie newSessionIdCookie = new Cookie("JSESSIONID", null);
        newSessionIdCookie.setMaxAge(0);
        newSessionIdCookie.setPath("/");
        response.addCookie(newSessionIdCookie);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        mapper.writeValue(response.getWriter(),
                Map.of(
                        "code", "SESSION_INVALID",
                        "message", "유효하지 않은 세션입니다."
                ));
    }
}
