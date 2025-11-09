package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {

  // todo - MockitoBean과 @Autowired를 어떤 상황에 붙여줘야하는지 공부하기
  // todo - given에서 any()를 쓸지, 명확히 타입을 지정해줄지 혹은 mock 객체를 만들어줄지

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  // @EnableJpaAuditing 사용 시 필요
  // https://tlatmsrud.tistory.com/140
  @MockitoBean
  private JpaMetamodelMappingContext jpaMappingContext;

  @MockitoBean
  private UserMapper userMapper;

  // todo - userStatusMapper 없으면 에러남
  @MockitoBean
  private UserStatusMapper userStatusMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("유저 생성을 성공하고 201 상태 코드를 반환한다.")
  void createUser_returnCreatedStatus() throws Exception {
    // given
    CreateUserRequest request = CreateUserRequest.builder()
        .username("test")
        .email("test@gmail.com")
        .password("password")
        .build();

    User user = new User("test", "test@gmail.com", "password");
    UUID userId = UUID.randomUUID();
    ReflectionTestUtils.setField(user, "id", userId);

    UserDto response = UserDto.builder()
        .id(user.getId())
        .username("test")
        .email("test@gmail.com")
        .profile(null)
        .online(Boolean.TRUE)
        .build();

    // todo - multipart 파일 생성
    MockMultipartFile profileFile = new MockMultipartFile(
        "profile",
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "dummy".getBytes()
    );
    // todo - json part 생성
    MockMultipartFile jsonPart = new MockMultipartFile(
        // @RequestPart("userCreateRequest") 와 동일하게 맞춰줘야함
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        // todo - objectMapper 공부하기
        objectMapper.writeValueAsBytes(request)
    );

    given(userService.create(any(CreateUserRequest.class), any()))
        .willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(response);

    // when & then
    // todo - json과 multipart 같이 보내는 방법 공부하기
    mockMvc.perform(multipart("/api/users")
            .file(profileFile)
            .file(jsonPart) // JSON도 multipart에 넣음
            // todo - VALUE 있는 것과 없는 것 차이 공부하기
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("test@gmail.com"))
        .andExpect(jsonPath("$.online").value(true));
  }
}
