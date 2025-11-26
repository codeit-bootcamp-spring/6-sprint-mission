package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.UserChannelService;
import com.sprint.mission.discodeit.service.jcf.JFCChannelService;
import com.sprint.mission.discodeit.service.jcf.JFCMessageService;
import com.sprint.mission.discodeit.service.jcf.JFCUserService;

import java.util.List;

public class JFCTest {
    private static final JFCUserService userService = JFCUserService.getInstance();
    private static final JFCMessageService messageService = JFCMessageService.getInstance();
    private static final JFCChannelService channelService = JFCChannelService.getInstance();

    private static final UserChannelService userChannelService = UserChannelService.getInstance();

    public static void main(String[] args) {


        User user1 = userService.createUser("짱구", "자는 중", "aaa@gmail.com");
        User user2 = userService.createUser("맹구", "식사 중", "bbb@gmail.com");
        User user3 = userService.createUser("유리", "공부 중", "ccc@gmial.com");
        User user4 = userService.createUser("철수", "쉬는 중", "ddd@gmial.com");
        User user5 = userService.createUser("훈이", "씻는 중", "eee@gmial.com");

        Channel channel1 = channelService.createChannel("떡잎 마을 방범대", "떡잎 마을을 지키는 모임");
        Channel channel2 = channelService.createChannel("붉은 장미 삼총사", "정의로운 모임");

        printAllChannels(); // 모든 채널 출력
        printAllUsers(); // 모든 유저 출력

        joinChannel(user1, channel1);
        joinChannel(user2, channel1);
        joinChannel(user2, channel2); // user2(맹구)의 채널 중복 입장
        joinChannel(user4, channel2);

        printUsersInChannel(channel1); // channel1(떡잎 마을 방범대) 유저 출력
        printChannelsOfUser(user2); // user2(맹구)가 속한 채널 출력

        sendMessage(user1, channel1, "출동!");
        sendMessage(user2, channel1, "발사!");
        sendMessage(user4, channel1, "출동!"); // 속하지 않은 채널에 메시지 전송

        printMessagesInChannel(channel1);
        printMessagesInChannel(channel2); // 메시지 0개인 채널 메시지 조회 시도

        updateChannelInfo(channel1, "떡잎 마을 파괴!");
        printChannel(channel1); // 채널 정보 수정 후 채널 출력

        updateChannelName(channel1, "떡잎 마을 파괴단");
        printMessagesInChannel(channel1); // 채널명 변경 후 메시지 조회

        deleteUser(user1);
        printMessagesInChannel(channel1); // user1(짱구) 삭제 후 메시지 출력

        printAllUsers();

        deleteChannel(channel1);
        printMessagesOfUser(user2); // 채널 삭제 후 해당 채널에서 채팅한 유저의 메시지 출력

    }


    // -----------------------------------------------
    // 채널 입퇴장
    private static void joinChannel(User user, Channel channel){
        if(userService.notExist(user) || channelService.notExist(channel)){
            System.out.println("[Error] 존재하지 않는 유저 혹은 채널입니다.\n");
        } else{
            if(!userChannelService.put(user.getId(), channel.getId())){
                System.out.println("[Error] 이미 입장한 채널입니다.\n");
            }else {
                System.out.println("[Info] " + user.getName() + " 님이 " + channel.getName()
                        + "에 입장했습니다.\n");
            }
        }
    }
    private static void leaveChannel(User user, Channel channel){
        if(userService.notExist(user) || channelService.notExist(channel)){
            System.out.println("[Error] 존재하지 않는 유저 혹은 채널입니다.\n");
        } else {
            if (!userChannelService.remove(user.getId(), channel.getId())) {
                System.out.println("[Error] 입장하지 않은 채널입니다.\n");
            } else {
                System.out.println("[Info] " + user.getName() + " 님이 " + channel.getName()
                        + "에서 퇴장했습니다.\n");
            }
        }
    }


    // 메시지 보내기
    private static void sendMessage(User user, Channel channel, String content){
        if(!userChannelService.isExist(user.getId(), channel.getId())){
            System.out.println("[Error] 입장하지 않은 채널에는 메시지를 보낼 수 없습니다.\n");
        } else {
            messageService.sendMessage(user.getId(), channel.getId(),
                    user.getName(), channel.getName(), content);
            System.out.println(user.getName() + " : " + content);
        }
    }
    // 메시지 출력
    private static void printMessage(Message message){
        System.out.println(message);
    }
    private static void printMessagesInChannel(Channel channel){
        System.out.println("-----채널 " + channel.getName() + "의 메시지 내역-----");
        messageService.findMessagesByChannelId(channel.getId()).stream()
                .forEach(JFCTest::printMessage);
        System.out.println();
    }
    private static void printMessagesOfUser(User user){
        System.out.println("-----유저 " + user.getName() + "의 메시지 내역-----");
        messageService.findMessagesByUserId(user.getId()).stream()
                .forEach(JFCTest::printMessage);
        System.out.println();
    }

    // 유저 정보 출력
    private static void printUser(User user){
        System.out.println(user);
    }
    private static void printAllUsers(){
        List<User> allUsers  = userService.findAll();
        System.out.println("----- 모든 유저 " + allUsers.size() + "명 -----");
        allUsers.stream().forEach(JFCTest::printUser);
        System.out.println();
    }
    private static void printUsersInChannel(Channel channel){
        List<User> usersInChannel = userService.findAllUsersByChannelId(channel.getId());
        System.out.println("---- 채널 " + channel.getName() + "의 유저 " +
                usersInChannel.size() + "명 ----");
        usersInChannel.stream().forEach(JFCTest::printUser);
        System.out.println();
    }

    // 채널 정보 출력
    private static void printChannel(Channel channel){
        System.out.println(channel);
    }
    private static void printAllChannels(){
        List<Channel> allChannels = channelService.findAll();
        System.out.println("----- 모든 채널 " + allChannels.size() + "개 -----");
        allChannels.stream().forEach(JFCTest::printChannel);
        System.out.println();
    }
    private static void printChannelsOfUser(User user){
        List<Channel> channelsOfUser = channelService.findAllChannelsByUserId(user.getId());
        System.out.println("---- " + user.getName() + "님이 속한 채널 " +
                channelsOfUser.size() + "개 ----");
        channelsOfUser.stream().forEach(JFCTest::printChannel);
        System.out.println();
    }

    // 유저 수정
    private static void updateUserName(User user, String updatedName){
        if(userService.updateName(user, updatedName))
            printUpdateInfo("유저명: ", user.getName());
        else printNotFoundError("유저");
    }
    private static void updateUserStatus(User user, String updatedStatus){
        if(userService.updateStatus(user, updatedStatus))
            printUpdateInfo("유저 상태: ", user.getStatus());
        else printNotFoundError("유저");
    }
    private static void updateUserEmail(User user, String updatedEmail){
        if(userService.updateEmail(user, updatedEmail))
            printUpdateInfo("유저 이메일: ", user.getEmail());
        else printNotFoundError("유저");
    }
    // 메시지 수정
    private static void updateMessageContent(Message message, String updatedContent){
        if(messageService.updateContent(message, updatedContent))
            printUpdateInfo("메시지 내용: ", message.getContent());
        else printNotFoundError("메시지");
    }
    // 채널 수정
    private static void updateChannelName(Channel channel, String updatedName){
        if(channelService.updateName(channel, updatedName))
            printUpdateInfo("채널명", channel.getName());
        else printNotFoundError("채널");
    }
    private static void updateChannelInfo(Channel channel, String updatedInfo){
        if(channelService.updateInformation(channel, updatedInfo))
            printUpdateInfo("채널 정보", channel.getInformation());
        else printNotFoundError("채널");
    }


    // 유저 삭제
    private static void deleteUser(User user){
        System.out.println(" ---- 유저 " + user.getName() + " 삭제 ---- ");
        if(userService.deleteUser(user)) printSuccessInfo();
        else printNotFoundError("유저");
    }
    // 채널 삭제
    private static void deleteChannel(Channel channel){
        System.out.println(" ---- 채널 " + channel.getName() + " 삭제 ---- ");
        if(channelService.deleteChannel(channel)) printSuccessInfo();
        else printNotFoundError("채널");
    }
    // 메시지 삭제
    private static void deleteMessage(Message message){
        System.out.println(" ---- 메시지 삭제 ---- ");
        if(messageService.deleteMessage(message)) printSuccessInfo();
        else printNotFoundError("메시지");
    }


    private static void printNotFoundError(String target){
        System.out.println("[Error] 존재하지 않는 " + target + "입니다.\n");
    }
    private static void printUpdateInfo(String targetName, String target){
        System.out.println("[Info] " + targetName + ": " + target + "(으)로 수정되었습니다.\n");
    }
    private static void printSuccessInfo(){
        System.out.println("[Info] 삭제되었습니다.\n");
    }

}
