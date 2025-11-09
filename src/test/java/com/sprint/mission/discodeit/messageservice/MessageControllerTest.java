package com.sprint.mission.discodeit.messageservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("messageCreate 성공 검증")
    public void messageCreate_success() throws Exception {
        // given (준비)
        //요청값 설정
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(channelId, userId, "안녕");

        //메시지 유저 설정
        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userId);
        UserDto userDto = UserFixture.createUserDto(
                userId,
                user.getUsername(),
                user.getEmail(),
                null,
                false);

        //메시지 채널 설정
        Channel channel = ChannelFixture.publicCreateChannel("메시지 테스트", "메시지 테스트");
        ChannelFixture.setChannelId(channel, channelId);
        Message message = MessageFixture.createMessage(user, channel, "테스트", new ArrayList<>());
        MessageFixture.setMessageId(message, messageId);
        MessageFixture.setMessageCreatedAt(message, Instant.now());
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        MessageDto messageDto = MessageFixture.createMessageDto(message, userDto, channel, List.of(binaryContentDto));


        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "attachments",
                "파일.txt",
                "txt",
                new byte[300]
        );

        // 3. 'request' DTO를 JSON 파트로 변환
        // 💡 컨트롤러에서 받을 @RequestPart("request")의 이름
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "", // 파일 이름 (JSON 파트이므로 비워둡니다)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(messageCreateRequest) // DTO를 JSON 바이트로 변환
        );

        when(messageService.create(anyList(), any(MessageCreateRequest.class))).thenReturn(messageDto);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(multipartFile)
                        .file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageDto.id().toString()))
                .andExpect(jsonPath("$.createdAt").value(messageDto.createdAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(messageDto.updatedAt().toString()))
                .andExpect(jsonPath("$.content").value(messageDto.content()))
                .andExpect(jsonPath("$.channelId").value(messageDto.channelId().toString()))
                .andExpect(jsonPath("$.author.id").value(messageDto.author().id().toString()))
                .andExpect(jsonPath("$.author.username").value(messageDto.author().username()))
                .andExpect(jsonPath("$.author.email").value(messageDto.author().email()))
                .andExpect(jsonPath("$.author.profile").isEmpty())
                .andExpect(jsonPath("$.author.online").value(messageDto.author().online()))
                .andExpect(jsonPath("$.attachments[0].id").value(messageDto.attachments().get(0).id().toString()))
                .andExpect(jsonPath("$.attachments[0].fileName").value(messageDto.attachments().get(0).fileName()))
                .andExpect(jsonPath("$.attachments[0].size").value(messageDto.attachments().get(0).size()))
                .andExpect(jsonPath("$.attachments[0].contentType").value(messageDto.attachments().get(0).contentType()));

    }

    @Test
    @DisplayName("messageUpdate 성공 검증")
    public void messageUpdate_success() throws Exception {
        // given (준비)
        //요청값 설정
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest newContent = MessageUpdateRequest.builder()
                .newContent("변경")
                .build();

        //메시지 유저 설정
        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userId);
        UserDto userDto = UserFixture.createUserDto(
                userId,
                user.getUsername(),
                user.getEmail(),
                null,
                false);

        //메시지 채널 설정
        Channel channel = ChannelFixture.publicCreateChannel("메시지 테스트", "메시지 테스트");
        ChannelFixture.setChannelId(channel, channelId);
        Message message = MessageFixture.createMessage(user, channel, "테스트", new ArrayList<>());
        MessageFixture.setMessageId(message, messageId);
        MessageFixture.setMessageCreatedAt(message, Instant.now());
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        MessageDto messageDto = MessageFixture.createMessageDto(message, userDto, channel, List.of(binaryContentDto));


        when(messageService.update(any(UUID.class),any(String.class))).thenReturn(messageDto);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newContent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageDto.id().toString()))
                .andExpect(jsonPath("$.createdAt").value(messageDto.createdAt().toString()))
                .andExpect(jsonPath("$.updatedAt").value(messageDto.updatedAt().toString()))
                .andExpect(jsonPath("$.content").value(messageDto.content()))
                .andExpect(jsonPath("$.channelId").value(messageDto.channelId().toString()))
                .andExpect(jsonPath("$.author.id").value(messageDto.author().id().toString()))
                .andExpect(jsonPath("$.author.username").value(messageDto.author().username()))
                .andExpect(jsonPath("$.author.email").value(messageDto.author().email()))
                .andExpect(jsonPath("$.author.profile").isEmpty())
                .andExpect(jsonPath("$.author.online").value(messageDto.author().online()))
                .andExpect(jsonPath("$.attachments[0].id").value(messageDto.attachments().get(0).id().toString()))
                .andExpect(jsonPath("$.attachments[0].fileName").value(messageDto.attachments().get(0).fileName()))
                .andExpect(jsonPath("$.attachments[0].size").value(messageDto.attachments().get(0).size()))
                .andExpect(jsonPath("$.attachments[0].contentType").value(messageDto.attachments().get(0).contentType()));
    }

    @Test
    @DisplayName("메시지 잘못된 요청 실패 검증")
    public void messageCreate_fail() throws Exception {
        // given (준비)
        //요청값 설정
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageCreateRequest Request = new MessageCreateRequest(channelId, userId, null);

        //메시지 유저 설정
        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userId);
        UserDto userDto = UserFixture.createUserDto(
                userId,
                user.getUsername(),
                user.getEmail(),
                null,
                false);

        //메시지 채널 설정
        Channel channel = ChannelFixture.publicCreateChannel("메시지 테스트", "메시지 테스트");
        ChannelFixture.setChannelId(channel, channelId);
        Message message = MessageFixture.createMessage(user, channel, "테스트", new ArrayList<>());
        MessageFixture.setMessageId(message, messageId);
        MessageFixture.setMessageCreatedAt(message, Instant.now());
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        MessageDto messageDto = MessageFixture.createMessageDto(message, userDto, channel, List.of(binaryContentDto));


        //임시 multipartFile 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "attachments",
                "파일.txt",
                "txt",
                new byte[300]
        );

        // 3. 'request' DTO를 JSON 파트로 변환
        // 💡 컨트롤러에서 받을 @RequestPart("request")의 이름
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "", // 파일 이름 (JSON 파트이므로 비워둡니다)
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(Request) // DTO를 JSON 바이트로 변환
        );

        when(messageService.create(anyList(), any(MessageCreateRequest.class))).thenReturn(messageDto);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(multipartFile)
                .file(requestPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메시지 잘못된 요청 실패 검증")
    public void messageUpdate_fail() throws Exception {
        // given (준비)
        //요청값 설정
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest newContent = MessageUpdateRequest.builder()
                .newContent(null)
                .build();

        //메시지 유저 설정
        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userId);
        UserDto userDto = UserFixture.createUserDto(
                userId,
                user.getUsername(),
                user.getEmail(),
                null,
                false);

        //메시지 채널 설정
        Channel channel = ChannelFixture.publicCreateChannel("메시지 테스트", "메시지 테스트");
        ChannelFixture.setChannelId(channel, channelId);
        Message message = MessageFixture.createMessage(user, channel, "테스트", new ArrayList<>());
        MessageFixture.setMessageId(message, messageId);
        MessageFixture.setMessageCreatedAt(message, Instant.now());
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        MessageDto messageDto = MessageFixture.createMessageDto(message, userDto, channel, List.of(binaryContentDto));


        when(messageService.update(any(UUID.class),any(String.class))).thenReturn(messageDto);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newContent)))
                .andExpect(status().isBadRequest());
    }

}
