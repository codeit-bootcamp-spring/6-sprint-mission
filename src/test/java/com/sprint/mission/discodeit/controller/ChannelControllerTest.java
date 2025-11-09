package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@ActiveProfiles("test")
class ChannelControllerTest {

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private ChannelMapper channelMapper;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMappingContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("공개 채널 생성 성공하고 201 상태 코드를 반환한다.")
  void createPublicChannel_returnCreatedStatus() throws Exception {
    // given
    CreatePublicChannelRequest request = CreatePublicChannelRequest.builder()
        .name("public channel")
        .description("public channel description")
        .build();

    UUID channelId = UUID.randomUUID();

    ChannelDto response = ChannelDto.builder()
        .id(channelId)
        .type(ChannelType.PUBLIC)
        .name("public channel")
        .description("public channel description")
        .participants(Collections.emptyList())
        .lastMessageAt(Instant.now())
        .build();

    Channel channel = new Channel(ChannelType.PUBLIC, "public channel", "public channel description");
    ReflectionTestUtils.setField(channel, "id", channelId);

    given(channelService.createPublic(any(CreatePublicChannelRequest.class))).willReturn(channel);
    given(channelMapper.toDto(any(Channel.class))).willReturn(response);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.toString()))
        .andExpect(jsonPath("$.name").value("public channel"))
        .andExpect(jsonPath("$.description").value("public channel description"));
  }
}
