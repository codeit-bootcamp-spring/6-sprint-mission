package com.sprint.mission.discodeit.channelservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChannelController.class)
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ChannelService channelService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("공개 채널 생성 성공 검증")
    public void createPublic_success() throws Exception {

        PublicChannelCreateRequest request = new PublicChannelCreateRequest("테스트", "테스트중");

        UUID channelId = UUID.randomUUID();
        Channel channel = ChannelFixture.publicCreateChannel("테스트", "테스트중");
        ChannelFixture.setChannelId(channel, channelId);
        ChannelDto channelDto = ChannelFixture.createChannelDto(channel, new ArrayList<>());

        when(channelService.createPublic(any(PublicChannelCreateRequest.class))).thenReturn(channelDto);

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelDto.id().toString()))
                .andExpect(jsonPath("$.name").value(channelDto.name()))
                .andExpect(jsonPath("$.description").value(channelDto.description()))
                .andExpect(jsonPath("$.type").value(channelDto.type().toString()))
                .andExpect(jsonPath("$.participants").isEmpty())
                .andExpect(jsonPath("$.lastMessageAt").value(channelDto.lastMessageAt().toString()));

    }

    @Test
    @DisplayName("비공개 채널 생성 성공 검증")
    public void createCPrivate_success() throws Exception {
        //비공개 채널 생성 요청값 설정
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(userIds)
                .build();
        //비공개 채널 설정
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel();
        ChannelFixture.setChannelId(channel,channelId);

        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userIds.get(0));

        UserDto userDto = UserFixture.createUserDto(
                userIds.get(0),
                "user",
                "test@test",
                null,
                false
        );

        ChannelDto channelDto = ChannelFixture.createChannelDto(channel, List.of(userDto));

        when(channelService.createPrivate(any(PrivateChannelCreateRequest.class))).thenReturn(channelDto);


        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelDto.id().toString()))
                .andExpect(jsonPath("$.name").value(channelDto.name()))
                .andExpect(jsonPath("$.description").value(channelDto.description()))
                .andExpect(jsonPath("$.type").value(channelDto.type().toString()))
                .andExpect(jsonPath("$.participants[0].id").value(channelDto.participants().get(0).id().toString()))
                .andExpect(jsonPath("$.participants[0].username").value(channelDto.participants().get(0).username()))
                .andExpect(jsonPath("$.participants[0].email").value(channelDto.participants().get(0).email()))
                .andExpect(jsonPath("$.participants[0].profile").isEmpty())
                .andExpect(jsonPath("$.participants[0].online").value(channelDto.participants().get(0).online()))
                .andExpect(jsonPath("$.lastMessageAt").value(channelDto.lastMessageAt().toString()));
    }

    @Test
    @DisplayName("공개 채널 생성 잘못된 요청 실패 검증")
    public void create_fail() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(null, null);

        UUID channelId = UUID.randomUUID();
        Channel channel = ChannelFixture.publicCreateChannel(null, null);
        ChannelFixture.setChannelId(channel, channelId);
        ChannelDto channelDto = ChannelFixture.createChannelDto(channel, new ArrayList<>());

        when(channelService.createPublic(any(PublicChannelCreateRequest.class))).thenReturn(channelDto);

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비공개 체널 생성 잘못된 요청 실패 검증")
    public void createCPrivate_fail() throws Exception {
        //비공개 채널 생성 요청값 설정
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = PrivateChannelCreateRequest.builder()
                .participantIds(null)
                .build();
        //비공개 채널 설정
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel();
        ChannelFixture.setChannelId(channel,channelId);

        User user = UserFixture.createUser(null);
        UserFixture.setUserId(user, userIds.get(0));

        UserDto userDto = UserFixture.createUserDto(
                userIds.get(0),
                "user",
                "test@test",
                null,
                false
        );

        ChannelDto channelDto = ChannelFixture.createChannelDto(channel, List.of(userDto));

        when(channelService.createPrivate(any(PrivateChannelCreateRequest.class))).thenReturn(channelDto);


        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
