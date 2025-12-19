package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/*
 * 스프링 시큐리티 6부터는 기본적으로 csrf 토큰을 암호화, 지연 로딩
 * SPA와 HTML Form을 동시에 지원하기 위해 커스텀 핸들러 작성
 * - SPA: plain(원본) 토큰 사용
 * - Form: xor(암호화) 토큰 사용
 * 공식 문서 예시코드
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
    // 1. HTML 폼 태그 지원을 위해 XOR 암호화된 토큰을 request attribute에 저장
    this.xor.handle(request, response, csrfToken);
    // 2. 쿠키(XSRF-TOKEN)에 값을 쓰기 위해 토큰을 강제로 로딩
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());

    // 헤더에 값이 있으면(SPA) -> 원본(Plain) 비교
    // 없으면(Form) -> 암호화된 값(Xor) 해독 후 비교
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
  }
}
