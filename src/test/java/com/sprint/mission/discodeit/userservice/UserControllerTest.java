package com.sprint.mission.discodeit.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    @DisplayName("유저 생성 성공 검증")
    public void create_success() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("테스트")
                .email("test@test.com")
                .password("000000")
                .build();

        //기대값 유저Dto에 있는 바이트파일Dto 생성
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        //기대값 유저Dto 생성
        UUID userId = UUID.randomUUID();
        String username = "test";
        String email = "test@test.com";
        UserDto userDto = UserFixture.createUserDto(
                userId,
                username,
                email,
                binaryContentDto,
                false
        );

        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        // 3. 'request' DTO를 JSON 파트로 변환
        // 💡 컨트롤러에서 받을 @RequestPart("request")의 이름
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "", // 파일 이름 (JSON 파트이므로 비워둡니다)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request) // DTO를 JSON 바이트로 변환
        );

        when(userService.create(any(MultipartFile.class), any(UserCreateRequest.class))).thenReturn(userDto);

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(multipartFile)
                .file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.profile.id").value(binaryContentDto.id().toString()))
                .andExpect(jsonPath("$.profile.fileName").value(binaryContentDto.fileName()))
                .andExpect(jsonPath("$.profile.size").value(binaryContentDto.size()))
                .andExpect(jsonPath("$.profile.contentType").value(binaryContentDto.contentType()))
                .andExpect(jsonPath("$.online").value(false));

    }

    @Test
    @DisplayName("유저 업데이트 성공 검증")
    public void update_success() throws Exception {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("테스트1")
                .newEmail("test1@test.com")
                .newPassword("111111")
                .build();

        //기대값 유저Dto에 있는 바이트파일Dto 생성
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        //기대값 유저Dto 생성
        UUID userId = UUID.randomUUID();
        String username = "test";
        String email = "test@test.com";
        UserDto userDto = UserFixture.createUserDto(
                userId,
                username,
                email,
                binaryContentDto,
                false
        );

        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        // 3. 'request' DTO를 JSON 파트로 변환
        // 💡 컨트롤러에서 받을 @RequestPart("request")의 이름
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "", // 파일 이름 (JSON 파트이므로 비워둡니다)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request) // DTO를 JSON 바이트로 변환
        );

        when(userService.update(any(MultipartFile.class),any(UUID.class), any(UserUpdateRequest.class))).thenReturn(userDto);

        // when & then
        mockMvc.perform(multipart(HttpMethod.PATCH,"/api/users/{userId}", userId)
                .file(multipartFile)
                .file(requestPart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.profile.id").value(binaryContentDto.id().toString()))
                .andExpect(jsonPath("$.profile.fileName").value(binaryContentDto.fileName()))
                .andExpect(jsonPath("$.profile.size").value(binaryContentDto.size()))
                .andExpect(jsonPath("$.profile.contentType").value(binaryContentDto.contentType()))
                .andExpect(jsonPath("$.online").value(false));

    }

    @Test
    @DisplayName("사용자 잘못된 요청 실패 검증")
    public void create_fail() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username(null)
                .email("test@test.com")
                .password("000000")
                .build();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "", // 파일 이름 (JSON 파트이므로 비워둡니다)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request) // DTO를 JSON 바이트로 변환
        );
        mockMvc.perform(multipart("/api/users")
                        .file(multipartFile)
                        .file(requestPart))
                .andExpect(status().isBadRequest());


    }
    @Test
    @DisplayName("업데이트 지원 하지 않는 요청 실패 검증")
    public void update_fail() throws Exception {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("테스트1")
                .newEmail("test1@test.com")
                .newPassword("111111")
                .build();

        //기대값 유저Dto에 있는 바이트파일Dto 생성
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        //기대값 유저Dto 생성
        UUID userId = UUID.randomUUID();
        String username = "test";
        String email = "test@test.com";
        UserDto userDto = UserFixture.createUserDto(
                userId,
                username,
                email,
                binaryContentDto,
                false
        );

        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        // 3. 'request' DTO를 JSON 파트로 변환
        // 💡 컨트롤러에서 받을 @RequestPart("request")의 이름
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "", // 파일 이름 (JSON 파트이므로 비워둡니다)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request) // DTO를 JSON 바이트로 변환
        );

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(multipartFile)
                        .file(requestPart))
                .andExpect(status().isMethodNotAllowed());


    }

}
