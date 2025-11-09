package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private ChannelRepository channelRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage storage;

  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("첨부파일 없는 메시지 생성 테스트")
  void createMessage_noAttachment() {
    // given
    CreateMessageRequest request = CreateMessageRequest.builder().channelId(null).authorId(null)
        .content("Hello, World!").attachmentIds(Collections.emptyList()).build();

    given(channelRepository.findById(request.channelId())).willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(request.authorId())).willReturn(Optional.of(mock(User.class)));
    given(messageRepository.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Message createdMessage = messageService.create(request, null);

    // then
    assertThat(createdMessage.getContent()).isEqualTo("Hello, World!");
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("첨부파일 있는 메시지 생성 테스트")
  void createMessage_withAttachments() {
    // given
    CreateMessageRequest request = CreateMessageRequest.builder().channelId(null).authorId(null)
        .content("Hello, World!").attachmentIds(List.of(UUID.randomUUID())).build();

    List<MultipartFile> attachments = List.of(
        new MockMultipartFile("attachment", "attachment.txt", "text/plain", new byte[]{1, 2, 3, 4}));

    given(channelRepository.findById(request.channelId())).willReturn(Optional.of(mock(Channel.class)));
    given(userRepository.findById(request.authorId())).willReturn(Optional.of(mock(User.class)));
    given(binaryContentRepository.save(any(BinaryContent.class))).willAnswer(invocation -> invocation.getArgument(0));
    given(storage.put(nullable(UUID.class), any(byte[].class))).willAnswer(invocation -> invocation.getArgument(0));
    given(messageRepository.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Message createdMessage = messageService.create(request, attachments);
    BinaryContent createdAttachment = createdMessage.getAttachments().get(0);

    // then
    assertThat(createdMessage.getContent()).isEqualTo("Hello, World!");
    verify(messageRepository).save(any(Message.class));

    assertThat(createdAttachment.getFileName()).isEqualTo("attachment.txt");
    assertThat(createdAttachment.getContentType()).isEqualTo("text/plain");
    assertThat(createdAttachment.getSize()).isEqualTo(new byte[]{1, 2, 3, 4}.length);
    verify(binaryContentRepository).save(any(BinaryContent.class));
  }

  @Test
  @DisplayName("메시지 업데이트 테스트")
  void updateMessage() {
    // given
    UUID messageId = UUID.randomUUID();

    UpdateMessageRequest request = UpdateMessageRequest.builder().newContent("new Content").build();

    Message message = new Message("content", mock(Channel.class), mock(User.class), Collections.emptyList());
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageRepository.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));

    // when
    Message updatedMessage = messageService.update(messageId, request);

    // then
    assertThat(updatedMessage.getContent()).isEqualTo("new Content");
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 삭제 테스트")
  void deleteMessage() {
    // given
    UUID messageId = UUID.randomUUID();

    Message message = new Message("content", mock(Channel.class), mock(User.class), Collections.emptyList());
    ReflectionTestUtils.setField(message, "id", messageId);

    given(messageRepository.existsById(messageId)).willReturn(true);
    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

    // when
    messageService.delete(messageId);

    // then
    verify(messageRepository).deleteById(messageId);
  }

  @Test
  @DisplayName("채널 ID로 메시지 조회 테스트")
  void findMessagesByChannelId() {
    // given
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);

    Message message = new Message("content", mock(Channel.class), mock(User.class), Collections.emptyList());
    List<Message> messageList = List.of(message);
    Slice<Message> sliceImpl = new SliceImpl<>(messageList, pageable, false);

    given(messageRepository.findAllByChannel_IdAndCreatedAtLessThanEqual(channelId, cursor, pageable)).willReturn(
        sliceImpl);

    // when
    Slice<Message> foundMessage = messageService.findAllByChannelId(channelId, cursor, pageable);

    // then
    assertThat(foundMessage.getContent()).hasSize(1);
    assertThat(foundMessage.getContent().get(0)).isEqualTo(message);
    assertThat(foundMessage.hasNext()).isFalse();
    verify(messageRepository).findAllByChannel_IdAndCreatedAtLessThanEqual(channelId, cursor, pageable);
  }
}
