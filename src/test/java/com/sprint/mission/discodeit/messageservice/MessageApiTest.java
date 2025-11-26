package com.sprint.mission.discodeit.messageservice;

import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
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
@Sql("/test-channel-data.sql")
@Sql("/test-message-data.sql")
public class MessageApiTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;


    @Test
    @DisplayName("메시지 생성 성공 검증")
    void messageCreateApi_success() throws Exception {
        UUID userId = UUID.fromString("33ac0646-e2a0-4f51-ac8a-1d945f0b84a1");
        UUID channelId = UUID.fromString("34ca8846-e2a0-4f51-ac8a-1d946f0b34a2");
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(channelId, userId, "안녕");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("messageCreateRequest", messageCreateRequest);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            // 파일 이름을 서버에 알려주기 위해 getFilename()을 오버라이드
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        body.add("attachments", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<MessageDto> responseEntity = restTemplate.postForEntity(
                "/api/messages",
                requestEntity, // HttpEntity를 전달
                MessageDto.class  // 기대하는 응답 본문 타입
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        MessageDto messageDto = responseEntity.getBody();
        assertThat(messageDto).isNotNull();

    }

    @Test
    @DisplayName("메시지 업데이트 성공 검증")
    void messageUpdateApi_success() throws Exception {
        UUID messageId = UUID.fromString("33ac0633-e2e9-4f51-ac8a-1d911f0b84a1");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));

        MessageUpdateRequest requestBody = MessageUpdateRequest.builder()
                .newContent("수정 테스트")
                .build();

        HttpEntity<MessageUpdateRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<MessageDto> createResponse = restTemplate.exchange(
                "/api/messages/{messageId}",
                HttpMethod.PATCH,
                requestEntity,
                MessageDto.class,
                messageId
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        MessageDto messageDto = createResponse.getBody();
        assertThat(messageDto).isNotNull();
    }

    @Test
    @DisplayName("메세지 유저,채널 아이디 미입력 잘못된 요청 실패 검증")
    @Sql("/test-delete-all.sql")
    void messageCreateApi_fail() throws Exception {
        UUID userId = null;
        UUID channelId = null;
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(channelId, userId, "안녕");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "파일.txt",
                "txt",
                new byte[300]
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("messageCreateRequest", messageCreateRequest);

        ByteArrayResource fileResource = new ByteArrayResource(multipartFile.getBytes()) {
            // 파일 이름을 서버에 알려주기 위해 getFilename()을 오버라이드
            @Override
            public String getFilename() {
                return multipartFile.getOriginalFilename();
            }
        };
        body.add("attachments", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<MessageDto> responseEntity = restTemplate.postForEntity(
                "/api/messages",
                requestEntity, // HttpEntity를 전달
                MessageDto.class  // 기대하는 응답 본문 타입
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
    @Test
    @DisplayName("메시지 내용 미입력 잘못된 요청 실패 검증")
    @Sql("/test-delete-all.sql")
    void messageUpdateApi_fail() throws Exception {
        UUID messageId = UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));

        MessageUpdateRequest requestBody = MessageUpdateRequest.builder()
                .newContent(null)
                .build();

        HttpEntity<MessageUpdateRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<MessageDto> createResponse = restTemplate.exchange(
                "/api/messages/{messageId}",
                HttpMethod.PATCH,
                requestEntity,
                MessageDto.class,
                messageId
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
