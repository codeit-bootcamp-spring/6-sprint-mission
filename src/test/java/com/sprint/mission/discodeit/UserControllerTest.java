package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doNothing;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStatusService userStatusService;

    @Test
    @DisplayName("POST /api/users - 회원 등록 성공")
    void 사용자_생성_성공() throws Exception {

        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto(
                "alice@example.com",
                "alice",
                "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(requestDto)
        );

        UserResponseDto responseDto = new UserResponseDto(
                UUID.randomUUID(),
                "alice@example.com",
                "alice",
                true
        );

        when(userService.create(any(UserCreateRequestDto.class), any()))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("POST /api/users - 회원 등록 실패 (필수값 누락)")
    void 사용자_생성_실패() throws Exception {

        // given - username 없음
        UserCreateRequestDto invalidRequest = new UserCreateRequestDto("alice@example.com", null, "password123");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest", "", "application/json",
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/users/{userId}/userStatus - 유저 상태 업데이트 성공")
    void 사용자_상태_수정_성공() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequestDto updateDto = new UserStatusUpdateRequestDto(Instant.now());
        UserStatusResponseDto responseDto = new UserStatusResponseDto(userId, Instant.now());

        // when
        when(userStatusService.updateByUserId(eq(userId), any(UserStatusUpdateRequestDto.class)))
                .thenReturn(responseDto);

        // then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - 사용자 삭제 성공")
    void 사용자_삭제_성공() throws Exception {

        // given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}
