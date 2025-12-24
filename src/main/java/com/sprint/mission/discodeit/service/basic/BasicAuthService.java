package com.sprint.mission.discodeit.service.basic;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService {

  // todo 유저 상태 확인
  private final SessionRegistry sessionRegistry;


}
