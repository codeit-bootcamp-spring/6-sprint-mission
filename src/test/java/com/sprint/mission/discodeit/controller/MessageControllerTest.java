package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.Collections;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
class MessageControllerTest {

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private MessageMapper messageMapper;

  @MockitoBean
  private PageResponseMapper<MessageDto> pageResponseMapper;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMappingContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("메시지 생성 성공하고 201 상태 코드를 반환한다.")
  void createMessage_returnCreatedStatus() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    CreateMessageRequest request = CreateMessageRequest.builder()
        .channelId(channelId)
        .authorId(authorId)
        .content("Hello, World!")
        .attachmentIds(Collections.emptyList())
        .build();

    MessageDto response = MessageDto.builder()
        .id(UUID.randomUUID())
        .channelId(channelId)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .attachments(Collections.emptyList())
        .content("Hello, World!")
        .build();

    MockMultipartFile attachment = new MockMultipartFile(
        "attachment",
        "attachment.png",
        MediaType.IMAGE_PNG_VALUE,
        "dummy".getBytes()
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    Message message = new Message("Hello, World!", mock(Channel.class), mock(User.class), null);

    given(messageService.create(any(CreateMessageRequest.class), any()))
        .willReturn(message);
    given(messageMapper.toDto(any(Message.class))).willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(attachment)
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello, World!"));
  }
}
