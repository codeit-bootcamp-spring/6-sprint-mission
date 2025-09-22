package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    static User setupUser(UserService userService) {
        UserCreateDto userCreateDto = new UserCreateDto("woody_user", "woody@example.com", "password123");
        User user = userService.create(userCreateDto);
        System.out.println("사용자 생성: " + user.getUsername());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        ChannelCreatePublicDto dto = new ChannelCreatePublicDto(ChannelType.PUBLIC, "공개", "공개 채널입니다.");
        Channel channel = channelService.create(dto);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);

    }

}
