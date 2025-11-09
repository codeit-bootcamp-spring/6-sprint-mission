package com.sprint.mission.discodeit.userservice;

import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql("/test-user-data.sql")
public class UserApiTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("유저 생성 성공 검증")
    void userCreatApi_success() throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("테스트")
                .email("test@test.com")
                .password("000000")
                .build();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userCreateRequest", request);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            // 파일 이름을 서버에 알려주기 위해 getFilename()을 오버라이드
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        body.add("profile", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity(
                "/api/users",
                requestEntity, // HttpEntity를 전달
                UserDto.class  // 기대하는 응답 본문 타입
        );


        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserDto userDto = responseEntity.getBody(); // 변수명 수정 (UserDto -> userDto)
        assertThat(userDto).isNotNull();

    }

    @Test
    @DisplayName("유저 업데이트 성공 검증")
    void userUpdatesApi_success() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("테스트1")
                .newEmail("test1@test.com")
                .newPassword("111111")
                .build();

        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userUpdateRequest", request);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            // 파일 이름을 서버에 알려주기 위해 getFilename()을 오버라이드
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        body.add("profile", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDto> responseEntity = restTemplate.exchange(
                "/api/users/{userId}",
                HttpMethod.PATCH,
                requestEntity,
                UserDto.class,
                UUID.fromString("33ac0646-e2a0-4f51-ac8a-1d945f0b84a1")
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDto userDto = responseEntity.getBody();
        assertThat(userDto).isNotNull();
    }

    @Test
    @DisplayName("유저 생성 잘못된 요청 실패 검증")
    void userCreatApi_fail() throws Exception {
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userCreateRequest", request);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            // 파일 이름을 서버에 알려주기 위해 getFilename()을 오버라이드
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        body.add("profile", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity(
                "/api/users",
                requestEntity, // HttpEntity를 전달
                UserDto.class  // 기대하는 응답 본문 타입
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    @DisplayName("업데이트 유저 없음 실패 검증")
    void userUpdatesApi_fail() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("테스트1")
                .newEmail("test1@test.com")
                .newPassword("111111")
                .build();

        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userUpdateRequest", request);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            // 파일 이름을 서버에 알려주기 위해 getFilename()을 오버라이드
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        body.add("profile", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDto> responseEntity = restTemplate.exchange(
                "/api/users/{userId}",
                HttpMethod.PATCH,
                requestEntity,
                UserDto.class,
                UUID.fromString("33ac0a46-e2a0-4f51-ac8a-1d945f0b84a1")
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


}
