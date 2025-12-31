## 요구사항

### 기본 요구사항
1. Spring Security 환경설정

- [x] 프로젝트에 Spring Security 의존성을 추가하세요.
- [x] Security 설정 클래스를 생성하세요.
- [x] SecurityFilterChain Bean을 선언하세요.
- [x] 개발 환경에서 Spring Security 모듈의 로깅 레벨을 trace로 설정하세요.

2. CSRF 보호 설정하기

- [x] CsrfTokenRepository 구현체를 CookieCsrfTokenRepository로 설정하세요.
- [x] CsrfTokenRequestHandler 컴포넌트를 대체하세요.
- [x] CSRF 토큰을 발급하는 API를 구현하세요.

3. 회원가입

- [x] 회원가입 API 스펙은 유지합니다. 
- [x] 회원가입 시 비밀번호는 PasswordEncoder를 통해 해시로 저장하세요. 

4. 인증 - 로그인

- [x] formLogin 을 기본값으로 활성화하고, 추가된 필터를 확인해보세요.
- [x] 로그인을 처리할 url을  /api/auth/login로 설정하세요.
- [x] UserDetailsService 컴포넌트를 대체하세요.
- [x] UserDetails 컴포넌트를 대체하세요.
- [x] AuthenticationSuccessHandler 컴포넌트를 대체하세요.
- [x] AuthenticiationFailureHandler 컴포넌트를 대체하세요.
- [x] 이제 로그인 처리는 SecurityFilterChain에서 모두 처리되기 때문에 기존에 구현했던 로그인 관련 코드는 제거하세요.

5. 인증 - 세션을 활용한 현재 사용자 정보 조회

- [x] 세션ID를 통해 사용자의 기본 정보(UserDto)를 가져올 수 있도록 API를 정의하세요. 

6. 인증 - 로그아웃

- [x] Spring Security의 logout 흐름은 그대로 유지하면서 필요한 부분만 대체합니다.
- [x] 이번 미션에서는 2가지 요소를 대체합니다.
- [x] 로그아웃을 처리할 url을  /api/auth/logout로 설정하세요.
- [x] LogoutSuccessHandler 컴포넌트를 대체하세요.

7. 인가 - 권한 정의

- [x] 다음과 같이 권한을 정의하세요.
- [x] 데이터베이스 스키마를 변경하세요.
- [x] 회원 가입 시 모든 사용자는 USER 권한을 기본 권한으로 설정하세요.
- [x] 사용자 권한을 수정하는 API를 구현하세요.
- [x] 애플리케이션 실행 시 ADMIN 권한을 가진 어드민 계정이 초기화되도록 구현하세요.
- [x] DiscodietUserDetails.getAuthorities를 수정하세요.

8. 인가 - 권한 적용

- [x] authorizeHttpRequests를 활성화하고, 모든 요청을 인증하도록 설정하세요.
- [x] 다음의 요청은 인증하지 않도록 설정하세요.
- [x] Method Security를 활성화하세요.
- [x] Service의 메소드 별로 아래의 조건에 맞게 권한을 수정하세요.
- [x] 적절한 권한이 없는 경우 403 응답을 반환하세요.
- [x] RoleHierarchy를 활용해 권한의 계층 구조를 정의하세요.

### 심화 요구사항

1. 세션 관리 고도화

- [x] 동일한 계정으로 동시 로그인할 수 없도록 설정하세요.
- [x] 권한이 변경된 사용자가 로그인 상태라면 세션을 무효화하세요.
- [ ] UserStatus 엔티티 대신 SessionRegistry를 활용해 사용자의 로그인 여부를 판단하도록 리팩토링하세요.

2. 로그인 고도화 - RememberMe

- [x] 로그인 요청 파라미터(remember-me)가 true인 경우 세션이 무효화되어도 자동으로 다시 로그인되도록 하세요.

3. 권한 적용 고도화

- [x] SpEL을 활용해 Method Security 기반 리소스 보호 정책을 강화해보세요. 