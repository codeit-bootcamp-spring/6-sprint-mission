package com.sprint.mission.discodeit.Main;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.File;
import java.util.List;


public class TestJavaApplication {

    public User testUserSenderTempRun() {
        UserService userService = new FileUserService();      //File*Service 테스트
        //UserService userService = new JCFUserService();

        User user = userService.create("이호건");
        System.out.println("유저 생성: " + user);
        User modifiedUser = userService.modify(user.getCommon().getId(),"이호건 이름 수정");       // 지역메소드 modify 안에만 user 이름을 수정하기에 반환 필요
        System.out.println("유저 이름 수정: " + userService.read(modifiedUser.getName()));
        userService.delete(modifiedUser.getCommon().getId());
        System.out.println("이호건 유저 삭제 후 유저 조회: " + userService.allRead());

        User user1 = userService.create("첫번째 유저");
        User user2 = userService.create("두번째 유저");
        System.out.println("더미 유저 생성 후 모든 유저 조회: " + userService.allRead());
        userService.delete(user1.getCommon().getId());
        userService.delete(user2.getCommon().getId());

        return user;
    }

    public User testUserRecieverTempRun() {
        UserService userService = new FileUserService();      //File*Service 테스트
        //UserService userService = new JCFUserService();

        User user = userService.create("코드잇");
        System.out.println("유저 생성: " + user);
        User modifiedUser = userService.modify(user.getCommon().getId(), "코드잇 이름 수정");
        System.out.println("유저 이름 수정: " + userService.read(modifiedUser.getName()));
        userService.delete(modifiedUser.getCommon().getId());
        System.out.println("코드잇 유저 삭제 후 유저 조회: " + userService.allRead());

        return user;
    }

    public Channel testChannelTempRun() {
        ChannelService channelService = new FileChannelService();      //File*Service 테스트
        //ChannelService channelService = new JCFChannelService();

        Channel channel = channelService.create("백엔드 6기");
        System.out.println("채널 생성: " + channel);
        Channel modifiedChannel = channelService.modify(channel.getCommon().getId(), "백엔드 6기 채널명 수정");
        System.out.println("채널명 수정: " + channelService.read(modifiedChannel.getName()));
        channelService.delete(modifiedChannel.getCommon().getId());
        System.out.println("삭제 후 채널명 조회: " + channelService.allRead());

        return channel;
    }

    public void testMessageTempRun() {
        MessageService messageService = new FileMessageService();      //File*Service 테스트
        //MessageService messageService = new JCFMessageService();
        TestJavaApplication testJavaApplication = new TestJavaApplication();

        Message message = messageService.create(testJavaApplication.testUserSenderTempRun(), testJavaApplication.testUserRecieverTempRun(), "안녕", testJavaApplication.testChannelTempRun());
        System.out.println("메시지 생성: " + message);
        Message modifiedMessage = messageService.modify(message.getCommon().getId(),"안녕하세요");
        System.out.println("메시지 내용 수정: " + messageService.read(modifiedMessage.getSender()));
        messageService.delete(message.getCommon().getId());
        System.out.println("삭제 후 메시지 조회: " + messageService.allRead());

    }

    public static void main(String[] args) {
        new TestJavaApplication().testMessageTempRun();
    }
}
