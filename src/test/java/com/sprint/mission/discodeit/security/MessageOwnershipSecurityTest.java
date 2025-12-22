package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MessageOwnershipSecurityTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("메시지 수정은 작성자만 가능")
    void updateMessage_DeniedForNonAuthor() {
        Message message = setupMessage();
        authenticateAs(createUser("other", "other@example.com"));

        assertThatThrownBy(() -> messageService.update(
            message.getId(), new MessageUpdateRequest("변경된 내용")))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("메시지 삭제는 작성자만 가능")
    void deleteMessage_DeniedForNonAuthor() {
        Message message = setupMessage();
        authenticateAs(createUser("other2", "other2@example.com"));

        assertThatThrownBy(() -> messageService.delete(message.getId()))
            .isInstanceOf(AccessDeniedException.class);
    }

    private Message setupMessage() {
        User author = createUser("author", "author@example.com");
        Channel channel = channelRepository.save(new Channel(
            ChannelType.PUBLIC, "test-channel", "test-description"));
        Message message = new Message("original", channel, author, List.of());
        return messageRepository.save(message);
    }

    private User createUser(String username, String email) {
        User user = new User(username, email, "encoded", null, Role.USER);
        return userRepository.save(user);
    }

    private void authenticateAs(User user) {
        DiscodeitUserDetails principal = new DiscodeitUserDetails(
            userMapper.toDto(user),
            user.getPassword(),
            user.getRole().toString());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                principal,
                user.getPassword(),
                principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
