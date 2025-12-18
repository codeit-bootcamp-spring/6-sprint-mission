package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDTO;
import com.sprint.mission.discodeit.dto.api.request.ChannelRequestDTO;
import com.sprint.mission.discodeit.dto.api.response.ChannelResponseDTO;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.NoSuchChannelException;
import com.sprint.mission.discodeit.mapper.api.ChannelApiMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockitoBean
  private ChannelService channelService;
  @MockitoBean
  private ChannelApiMapper channelApiMapper;

  private UUID testChannelId = UUID.randomUUID();
  private String testChannelName = "testChannel";
  private String testDescription = "This is a test channel";
  private List<UUID> testParticipants = List.of(UUID.randomUUID(), UUID.randomUUID());

  @Test
  @DisplayName("공개 채널 생성 - 성공")

  void createPublicChannel_Success() throws Exception {

    //given
    ChannelRequestDTO.PublicChannelCreateRequest request = new ChannelRequestDTO.PublicChannelCreateRequest(testChannelName, testDescription);

    ChannelDTO.Channel channel = ChannelDTO.Channel.builder()
        .id(testChannelId)
        .type(ChannelType.PUBLIC)
        .name(testChannelName)
        .description(testDescription)
        .participants(new ArrayList<>())
        .build();

    ChannelResponseDTO.FindChannelResponse response = ChannelResponseDTO.FindChannelResponse.builder()
        .id(testChannelId)
        .type(ChannelType.PUBLIC)
        .name(testChannelName)
        .description(testDescription)
        .participants(new ArrayList<>())
        .build();

    when(channelService.createChannel(any(ChannelDTO.CreatePublicChannelCommand.class)))
        .thenReturn(channel);
    when(channelApiMapper.toFindChannelResponse(any(ChannelDTO.Channel.class)))
        .thenReturn(response);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(testChannelId.toString()))
        .andExpect(jsonPath("$.name").value(testChannelName));

  }

  @Test
  @DisplayName("비공개 채널 생성 - 성공")
  void createPrivateChannel_Success() throws Exception {

    //given
    ChannelRequestDTO.PrivateChannelCreateRequest request =
        new ChannelRequestDTO.PrivateChannelCreateRequest(testParticipants);

    ChannelDTO.Channel channel = ChannelDTO.Channel.builder()
        .id(testChannelId)
        .type(ChannelType.PRIVATE)
        .name("DM")
        .description("DM")
        .participants(new ArrayList<>())
        .build();

    ChannelResponseDTO.FindChannelResponse response = ChannelResponseDTO.FindChannelResponse.builder()
        .id(testChannelId)
        .type(ChannelType.PRIVATE)
        .name("DM")
        .description("DM")
        .participants(new ArrayList<>())
        .build();

    when(channelService.createPrivateChannel(any(ChannelDTO.CreatePrivateChannelCommand.class)))
        .thenReturn(channel);
    when(channelApiMapper.toFindChannelResponse(any(ChannelDTO.Channel.class)))
        .thenReturn(response);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(testChannelId.toString()))
        .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.toString()));

  }

  @Test
  @DisplayName("채널 정보 수정 - 성공")
  void updateChannel_Success() throws Exception {

    //given
    ChannelRequestDTO.ChannelUpdateRequest request =
        new ChannelRequestDTO.ChannelUpdateRequest("updated-name", "updated-description");

    ChannelDTO.Channel updatedChannel = ChannelDTO.Channel.builder()
        .id(testChannelId)
        .type(ChannelType.PUBLIC)
        .name("updated-name")
        .description("updated-description")
        .participants(new ArrayList<>())
        .build();

    ChannelResponseDTO.FindChannelResponse response = ChannelResponseDTO.FindChannelResponse.builder()
        .id(testChannelId)
        .type(ChannelType.PUBLIC)
        .name("updated-name")
        .description("updated-description")
        .participants(new ArrayList<>())
        .build();

    when(channelService.updateChannel(any(ChannelDTO.UpdateChannelCommand.class)))
        .thenReturn(updatedChannel);
    when(channelApiMapper.toFindChannelResponse(any(ChannelDTO.Channel.class)))
        .thenReturn(response);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/api/channels/{channelId}", testChannelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testChannelId.toString()))
        .andExpect(jsonPath("$.name").value("updated-name"));

  }

  @Test
  @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
  void updateChannel_Fail_EmptyName() throws Exception {

    //given
    ChannelRequestDTO.ChannelUpdateRequest request =
        new ChannelRequestDTO.ChannelUpdateRequest("updated-name", "updated-description");

    when(channelService.updateChannel(any(ChannelDTO.UpdateChannelCommand.class)))
        .thenThrow(new NoSuchChannelException());

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .patch("/api/channels/{channelId}", testChannelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());

  }

  @Test
  @DisplayName("채널 삭제 - 성공")
  void deleteChannel_Success() throws Exception {

    //given
    doNothing().when(channelService).deleteChannelById(testChannelId);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .delete("/api/channels/{channelId}", testChannelId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

  }

  @Test
  @DisplayName("채널 조회 - 성공")
  void getChannel_Success() throws Exception {

    //given
    UUID userId = UUID.randomUUID();

    ChannelDTO.Channel channel = ChannelDTO.Channel.builder()
        .id(testChannelId)
        .type(ChannelType.PUBLIC)
        .name(testChannelName)
        .description(testDescription)
        .participants(new ArrayList<>())
        .build();

    ChannelResponseDTO.FindChannelResponse response = ChannelResponseDTO.FindChannelResponse.builder()
        .id(testChannelId)
        .type(ChannelType.PUBLIC)
        .name(testChannelName)
        .description(testDescription)
        .participants(new ArrayList<>())
        .build();

    when(channelService.findChannelsByUserId(userId))
        .thenReturn(List.of(channel));
    when(channelApiMapper.toFindChannelResponse(channel))
        .thenReturn(response);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders
            .get("/api/channels")
            .param("userId", String.valueOf(userId))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

}