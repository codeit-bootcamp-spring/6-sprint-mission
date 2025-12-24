package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentDTO.BinaryContent;
import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserStatusDTO;
import com.sprint.mission.discodeit.dto.api.request.UserRequestDTO;
import com.sprint.mission.discodeit.dto.api.response.UserResponseDTO;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.userstatus.NoSuchUserStatusException;
import com.sprint.mission.discodeit.mapper.api.UserApiMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockitoBean
  private AuthService authService;
  @MockitoBean
  private UserService userService;
  @MockitoBean
  private UserStatusService userStatusService;
  @MockitoBean
  private UserApiMapper userApiMapper;

  private final UUID testUserId = UUID.randomUUID();
  private final String testUsername = "tester";
  private final String testEmail = "test@example.com";
  private final String testPassword = "Testpass123@";
  private final String testDescription = "Test user description";

  @Test
  @DisplayName("사용자 생성 - 성공")
  void createUser_Success() throws Exception {
    //given
    UserRequestDTO.UserCreateRequest createUserRequest = new UserRequestDTO.UserCreateRequest(
        testUserId.toString(),
        testEmail,
        testPassword,
        testDescription
    );

    UserDTO.User user = UserDTO.User.builder()
        .id(testUserId)
        .username(testUsername)
        .email(testEmail)
        .build();

    UserResponseDTO.FindUserResponse findUserResponse = UserResponseDTO.FindUserResponse.builder()
        .id(testUserId)
        .username(testUsername)
        .email(testEmail)
        .build();

    when(userService.createUser(any()))
        .thenReturn(user);
    when(userApiMapper.toFindUserResponse(any(UserDTO.User.class)))
        .thenReturn(findUserResponse);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .multipart("/api/users")
            .file(new MockMultipartFile("userCreateRequest", "",
                "application/json",
                objectMapper.writeValueAsString(createUserRequest).getBytes()))
            .with(request -> {
              request.setMethod("POST");
              return request;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        //.andExpect(jsonPath("$.id").value(testUserId))
        .andExpect(jsonPath("$.username").value(testUsername));

  }

  @Test
  @DisplayName("사용자 생성 - 실패 (중복된 이메일 또는 사용자 이름)")
  void createUser_Fail_DuplicateEmailOrUsername() throws Exception {

    //given
    UserRequestDTO.UserCreateRequest createUserRequest = new UserRequestDTO.UserCreateRequest(
        testUserId.toString(),
        testEmail,
        testPassword,
        testDescription
    );

    when(userService.createUser(any()))
        .thenThrow(new IllegalArgumentException("Email or username already exists"));

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .multipart("/api/users")
            .file(new MockMultipartFile("userCreateRequest", "",
                "application/json",
                objectMapper.writeValueAsString(createUserRequest).getBytes()))
            .with(request -> {
              request.setMethod("POST");
              return request;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email or username already exists"));

  }

  @Test
  @DisplayName("사용자 프로필 수정 - 성공")
  void updateUserProfile_Success() throws Exception {

    // given
    UserRequestDTO.UserUpdateRequest updateRequest = UserRequestDTO.UserUpdateRequest.builder()
        .username("updatedUsername")
        .email("updated@example.com")
        .currentPassword(testPassword)
        .newPassword("Newpass123@")
        .build();

    UserDTO.User updatedUser = UserDTO.User.builder()
        .id(testUserId)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .username("updatedUsername")
        .email("updated@example.com")
        .password(testPassword)
        .profileId(new BinaryContent())
        .isOnline(false)
        .build();

    UserResponseDTO.FindUserResponse response = UserResponseDTO.FindUserResponse.builder()
        .id(testUserId)
        .username("updatedUsername")
        .email("updated@example.com")
        .profile(null)
        .isOnline(false)
        .build();

    when(userService.updateUser(any(UserDTO.UpdateUserCommand.class)))
        .thenReturn(updatedUser);
    when(userApiMapper.toFindUserResponse(any(UserDTO.User.class)))
        .thenReturn(response);

    // when & then
    mockMvc.perform(MockMvcRequestBuilders
            .multipart("/api/users/{userId}", testUserId)
            .file(new MockMultipartFile("userUpdateRequest", "",
                "application/json",
                objectMapper.writeValueAsString(updateRequest).getBytes()))
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().is(204))
        .andExpect(jsonPath("$.username").value("updatedUsername"))
        .andExpect(jsonPath("$.email").value("updated@example.com"));

  }

  @Test
  @DisplayName("사용자 삭제 - 성공")
  void deleteUser_Success() throws Exception {

    // given
    doNothing().when(userService)
        .deleteUserById(testUserId);

    // when & then
    mockMvc.perform(MockMvcRequestBuilders
            .delete("/api/users/{userId}", testUserId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

  }

  @Test
  @DisplayName("사용자 상태 업데이트 - 성공")
  void updateUserStatus_Success() throws Exception {

    //given
    UserRequestDTO.UserStatusUpdateRequest statusUpdateRequest =
        new UserRequestDTO.UserStatusUpdateRequest(Instant.now());

    UserStatusDTO.UserStatus userStatus = UserStatusDTO.UserStatus.builder()
        .id(testUserId)
        .userId(testUserId)
        .lastActiveAt(statusUpdateRequest.newLastActiveAt())
        .build();

    UserResponseDTO.CheckUserOnlineResponse response = UserResponseDTO.CheckUserOnlineResponse.builder()
            .id(testUserId)
            .userId(testUserId)
            .lastOnlineAt(statusUpdateRequest.newLastActiveAt())
            .isOnline(true)
            .build();

    when(userStatusService.findUserStatusByUserId(testUserId))
        .thenReturn(userStatus);
    when(userStatusService.updateUserStatus(any(UserStatusDTO.UpdateUserStatusCommand.class)))
        .thenReturn(userStatus);
    when(userApiMapper.userStatusToCheckUserOnlineResponse(any(UserStatusDTO.UserStatus.class)))
        .thenReturn(response);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/api/users/{userId}/userStatus", testUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(statusUpdateRequest)))
        .andExpect(status().isOk());

  }

  @Test
  @DisplayName("사용자 온라인 상태 확인 - 성공")
  void checkUserOnlineStatus_Success() throws Exception {

    //given
    UserStatusDTO.UserStatus userStatus = UserStatusDTO.UserStatus.builder()
        .id(testUserId)
        .userId(testUserId)
        .lastActiveAt(Instant.now())
        .build();

    UserResponseDTO.CheckUserOnlineResponse response = UserResponseDTO.CheckUserOnlineResponse.builder()
        .id(testUserId)
        .userId(testUserId)
        .lastOnlineAt(userStatus.getLastActiveAt())
        .isOnline(true)
        .build();

    when(userStatusService.findUserStatusByUserId(testUserId))
        .thenReturn(userStatus);
    when(userApiMapper.userStatusToCheckUserOnlineResponse(any(UserStatusDTO.UserStatus.class)))
        .thenReturn(response);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .get("/api/users/{userId}/userStatus", testUserId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.isOnline").value(true));

  }

  @Test
  @DisplayName("사용자 온라인 상태 확인 - 실패 (존재하지 않는 사용자)")
  void checkUserOnlineStatus_Fail_UserNotFound() throws Exception {

    //given
    when(userStatusService.findUserStatusByUserId(testUserId))
        .thenThrow(new NoSuchUserStatusException());

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .get("/api/users/{userId}/userStatus", testUserId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ErrorCode.NO_SUCH_USER_STATUS.name()));

  }

}