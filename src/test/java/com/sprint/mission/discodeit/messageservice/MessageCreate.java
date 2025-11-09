package com.sprint.mission.discodeit.messageservice;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.support.BinaryContentFixture;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class MessageCreate {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Test
    @DisplayName("messageCreate:성공 겁증")
    public void messageCreate_success() {
        // given (준비)
        //요청값 설정
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        List<MultipartFile>  files = List.of(new MockMultipartFile("file", new byte[32]));
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(channelId,userId,"안녕");

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
        Channel channel = ChannelFixture.publicCreateChannel("메시지 테스트","메시지 테스트");
        Message message = MessageFixture.createMessage(user,channel,"테스트",new ArrayList<>());
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();
        MessageDto messageDto = MessageFixture.createMessageDto(message,userDto,channel,List.of(binaryContentDto));

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(channelRepository.findById(any(UUID.class))).thenReturn(Optional.of(channel));
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);



        //when
        MessageDto response = basicMessageService.create(files,messageCreateRequest);
        //then
        assertThat(response).isEqualTo(messageDto);

        verify(userRepository,times(1)).findById(any(UUID.class));
        verify(channelRepository,times(1)).findById(any(UUID.class));
        verify(binaryContentStorage,times(1)).put(isNull(), any(byte[].class));
        verify(binaryContentRepository,times(1)).save(any(BinaryContent.class));
        verify(messageRepository,times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("messageCreate:유저 없음 실패 검증")
    public void messageCreate_fail() {
        // given (준비)
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        List<MultipartFile>  files = List.of(new MockMultipartFile("file", new byte[32]));
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(channelId,userId,"안녕");
        //when
        DiscodeitException exception = assertThrows(UserNotFoundException.class, ()->{
           basicMessageService.create(files,messageCreateRequest);
        });
        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

}
