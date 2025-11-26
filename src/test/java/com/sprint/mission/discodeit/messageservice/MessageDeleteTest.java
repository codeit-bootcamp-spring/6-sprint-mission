package com.sprint.mission.discodeit.messageservice;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.support.BinaryContentFixture;
import com.sprint.mission.discodeit.support.MessageFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageDeleteTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @InjectMocks
    private BasicMessageService basicMessageService;

    @Test
    @DisplayName("messageDelete:성공 검증")
    public void messageDelete_success() {
        // given (준비)
        UUID messageId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("파일")
                .size(10L)
                .contentType("txt")
                .build();
        BinaryContentFixture.setBinaryContentId(binaryContent,UUID.randomUUID());
        Message message = MessageFixture.createMessage(new User(),new Channel(),"테스트",List.of(binaryContent));
        MessageFixture.setMessageId(message,messageId);

        when(messageRepository.findById(any(UUID.class))).thenReturn(Optional.of(message));

        //when
        basicMessageService.delete(messageId);

        //then
        verify(binaryContentRepository, times(1)).deleteById(any(UUID.class));
        verify(messageRepository, times(1)).findById(any(UUID.class));
        verify(messageRepository, times(1)).deleteById(any(UUID.class));

    }

    @Test
    @DisplayName("messageDelete:메시지 없음 실패 검증")
    public void messageDelete_fail() {
        // given (준비)
        UUID messageId = UUID.randomUUID();
        //when
        DiscodeitException exception = assertThrows(DiscodeitException.class, () -> {
            basicMessageService.delete(messageId);
        });
        //then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MESSAGE_NOT_FOUND);
    }
}
