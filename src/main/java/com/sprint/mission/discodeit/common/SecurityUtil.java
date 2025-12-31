package com.sprint.mission.discodeit.common;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

  private final DiscodeitUserDetailsService detailsService;

  public void refreshAuthentication(User user) {

    // 1. 업데이트된 사용자 정보로 UserDetails 다시 로드
    UserDetails newUserDetails = detailsService.loadUserByUsername(user.getUsername());

    // 2. 새로운 Authentication 객체 생성
    Authentication newAuth = new UsernamePasswordAuthenticationToken(
        newUserDetails,
        null,
        newUserDetails.getAuthorities()
    );

    // 3. SecurityContext에 새로운 Authentication 설정
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(newAuth);

    // 4. 세션에 SecurityContext 반영
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
      }
    }
  }
}
