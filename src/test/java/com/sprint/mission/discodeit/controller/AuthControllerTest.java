package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthService authService;

  @Test
  @DisplayName("인증 사용자 정보 조회 성공 테스트")
  void me_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto loggedInUser = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false
    );
    DiscodeitUserDetails principal = new DiscodeitUserDetails(
        loggedInUser,
        "encoded",
        Role.USER.toString());
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            principal, "encoded", principal.getAuthorities());

    // When & Then
    mockMvc.perform(get("/api/auth/me")
            .with(authentication(auth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("권한 변경 성공 테스트")
  void changeRole_Success() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.CHANNEL_MANAGER);
    UserDto updatedUser = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false
    );

    given(authService.changeRole(any(RoleUpdateRequest.class))).willReturn(updatedUser);

    // When & Then
    mockMvc.perform(put("/api/auth/role")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }
} 
