package com.sprint.mission.discodeit.security.formLogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.userDetails.DiscodeitUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class DiscodeitAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        mapper.writeValue(response.getWriter(), Map.of("userDto", userDetails.getUserDto()));
    }
}
