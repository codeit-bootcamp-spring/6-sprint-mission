## 요구사항

### 기본 요구사항
1. JWT 컴포넌트 구현
- [x] JWT 의존성을 추가하세요.
- [x] 토큰을 발급, 갱신, 유효성 검사를 담당하는 컴포넌트(JwtTokenProvider)를 구현하세요.

2. 리팩토링 - 로그인
- [x] 세션 생성 정책을 STATELESS로 변경하고, sessionConcurrency 설정을 삭제하세요.
- [x] AuthenticationSuccessHandler 컴포넌트를 대체하세요.

3. JWT 인증 필터 구현
- [x] 엑세스 토큰을 통해 인증하는 필터(JwtAuthenticationFilter)를 구현하세요.

4. 리프레시 토큰을 활용한 엑세스 토큰 재발급
- [x] 리프레시 토큰을 활용해 엑세스 토큰을 재발급하는 API를 구현하세요.
- [x] 리프레시 토큰 Rotation을 통해 보안을 강화하세요.
- [x] 토큰 재발급 API로 대체할 수 있는 컴포넌트를 모두 삭제하세요.

5. 리팩토링 - 로그아웃
- [x] 쿠키에 저장된 리프레시 토큰을 삭제하는 LogoutHandler를 구현하세요.
- [x] 구현한 핸들러를 추가하세요.

### 심화 요구사항

1. 리팩토링 - 토큰 상태 관리
- [x] 토큰의 상태를 관리하는 JwtRegistry를 구현하세요.
- [x] JwtAuthenticationFilter에서 JwtRegistry를 활용해 토큰의 상태를 검사하는 로직을 추가하세요.
- [x] JwtRegistry를 활용해 동시 로그인 제한 기능을 리팩토링하세요.
- [x] JwtRegistry를 활용해 권한이 변경된 사용자가 로그인 상태라면 강제로 로그아웃되도록 하세요.
- [x] JwtRegistry를 활용해 사용자의 로그인 여부를 판단하도록 리팩토링하세요.
- [x] JwtLogoutHandler에서 JwtRegistry를 활용해 로그아웃 시 토큰을 무효화하세요.
- [x] 주기적으로 만료된 토큰 정보를 레지스트리에서 삭제하세요.