package com.sprint.mission.discodeit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.util.Map;

/**
 * JSON 로그인을 위한 필터.
 * 프론트엔드에서 요청이 Content-Type : application/json 형식으로 들어오지만,
 * .formLogin()으로는 application/x-www-form-urlencoded 형식만 처리 가능함.
 */
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        // JSON 요청인지 확인
        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            return super.attemptAuthentication(request, response);
        }

        try {
            // JSON 바디를 Map으로 파싱
            Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);

            String username = loginData.get("username");
            String password = loginData.get("password");

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);

            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new RuntimeException("로그인 요청 파싱 실패", e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 컨텍스트 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);

        // SecurityContextRepository를 통해 세션에 저장
        SecurityContextRepository repo = new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
        repo.saveContext(context, request, response);

        // 기존 핸들러 실행
        getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }
}
