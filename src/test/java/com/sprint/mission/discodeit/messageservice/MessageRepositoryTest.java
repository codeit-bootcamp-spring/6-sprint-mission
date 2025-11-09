package com.sprint.mission.discodeit.messageservice;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.support.ChannelFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setup() {
        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("파일")
                .size(10L)
                .contentType("txt")
                .build();
        BinaryContent binaryContent2 = BinaryContent.builder()
                .fileName("파일")
                .size(10L)
                .contentType("txt")
                .build();
        User user = UserFixture.createUser(binaryContent);


        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .build();
        UserFixture.setStatus(user, userStatus);

        Channel channel = ChannelFixture.publicCreateChannel("asd", "das");


        Message message = Message.builder()
                .author(user)
                .channel(channel)
                .attachment(List.of(binaryContent))
                .build();

        Message message2 = Message.builder()
                .author(user)
                .channel(channel)
                .attachment(List.of(binaryContent2))
                .build();


        binaryContentRepository.saveAll(List.of(binaryContent,binaryContent2));
        userRepository.save(user);
        userStatusRepository.save(userStatus);
        channelRepository.save(channel);
        messageRepository.save(message);
        messageRepository.save(message2);
    }

    @Test
    @DisplayName("성공 검증")
    public void findByChannelIdOrderByCreatedAtDesc_success() {

        // given
        Channel channel = channelRepository.findAll().get(0);
        Pageable pageable = PageRequest.of(0, 1);

        //when
        Slice<Message> messageSlice = messageRepository.findByChannelIdOrderByCreatedAtDesc(channel.getId(), pageable);

        //then
        assertThat(messageSlice).isNotEmpty();


    }

    @Test
    @DisplayName("성공 검증")
    public void findByCourseIdAndIdLessThanOrderByIdDesc_success() {
        // given
        Channel channel = channelRepository.findAll().get(0);

        List<Message> message = messageRepository.findAll();

        Pageable pageable = PageRequest.of(0, 1);

        //when
        Slice<Message> messageSlice = messageRepository.findByCourseIdAndIdLessThanOrderByIdDesc(channel.getId(), message.get(0).getCreatedAt(), pageable);

        //then
        assertThat(messageSlice).isNotEmpty();

    }

    @Test
    @DisplayName("")
    public void findByChannelIdOrderByCreatedAtDesc_fail() {
    }

    @Test
    @DisplayName("")
    public void findByCourseIdAndIdLessThanOrderByIdDesc_fail() {
    }

}
