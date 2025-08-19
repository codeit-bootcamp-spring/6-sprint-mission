package com.sprint.mission;

import com.sprint.mission.discodeit.Exception.DuplicateNameException;
import com.sprint.mission.discodeit.Exception.NotFoundException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        ChannelService channelService = new ChannelService();
        UserService userService = new UserService();
        MessageService messageService = new MessageService();

        UUID uuid = UUID.randomUUID();

        System.out.println("초기 설정을 시작합니다.");
        System.out.print("아이디를 입력해주세요: ");
        Scanner sc = new Scanner(System.in);
        String userName = sc.nextLine();
        User user = new User(uuid, userName);
        userService.addUser(user);
        System.out.println("초기 설정이 완료되었습니다.");
        boolean flag = true;
        while(flag){
            System.out.println("===========메뉴==========");
            System.out.println("1. 이름 변경");
            System.out.println("2. 채널 조회");
            System.out.println("3. 작성한 메세지 조회");
            System.out.println("4. 채널 생성하기");
            System.out.println("5. 내 정보 보기");
            System.out.println("6. 프로그램 종료");
            System.out.print("선택: ");
            int firstMenu = sc.nextInt();
            sc.nextLine();
            switch (firstMenu){
                case 1:
                    try {
                        System.out.print("변경하실 이름을 작성해주세요: ");
                        String changeName = sc.nextLine();
                        userService.changeUserName(uuid, changeName);
                        break;
                    } catch (NotFoundException e) {
                        System.out.println("[NotFound]" + e.getMessage());
                        break;
                    }
                case 2:
                    try {
                        System.out.println("사용자가 생성한 채널을 조회합니다");
                        List<Channel> channels = channelService.getChannels();
                        if(channels.isEmpty()){
                            System.out.println("사용자가 생성한 채널이 없습니다.");
                            break;
                        }
                        for (int i = 0; i < channels.size(); i++) {
                            System.out.println((i+1) + "." + channels.get(i));
                        }
                        boolean flag4 = true;
                        while (flag4){
                            System.out.println("==========메뉴===========");
                            System.out.println("1. 채널 이름 변경");
                            System.out.println("2. 채널 메세지 전송");
                            System.out.println("3. 채널 메세지 조회");
                            System.out.println("4. 채널 삭제");
                            System.out.println("5. 채널 목록 보기");
                            System.out.println("6. 이전 메뉴로");
                            int channelMenu = sc.nextInt();
                            if( channelMenu < 1 || channelMenu > 5){
                                System.out.println("잘못된 번호입니다.");
                            }
                            sc.nextLine();
                            switch (channelMenu) {
                                case 1:
                                    try {
                                        System.out.println("변경하실 채널 번호를 입력해주세요: ");
                                        Integer channelNumber = getChannelNumber(channels, sc);
                                        if (channelNumber == null) break;
                                        System.out.print("변경 후 채널의 이름을 작성해주세요: ");
                                        String channelName = sc.nextLine();
                                        channelService.changeChannelName(channels.get(channelNumber - 1).getId(), channelName);
                                        break;
                                    } catch (NotFoundException e) {
                                        System.out.println("[NotFound]" + e.getMessage());
                                        break;
                                    }
                                case 2:
                                    try{
                                        System.out.println("메세지를 작성하실 채널을 선택해주세요: ");
                                        Integer channelNumber = getChannelNumber(channels, sc);
                                        if (channelNumber == null) break;
                                        System.out.print("메세지를 작성해주세요: ");
                                        String messageContext = sc.nextLine();
                                        Message message = new Message(UUID.randomUUID(), messageContext, user.getUserName(), channels.get(channelNumber - 1).getId());
                                        messageService.addMessage(message);
                                        channelService.addMessage(channels.get(channelNumber - 1).getId(), message);
                                        break;
                                    } catch (NotFoundException e) {
                                        System.out.println("[NotFound]" + e.getMessage());
                                        break;
                                    }
                                case 3:
                                    try {
                                        System.out.println("조회하실 채널의 번호를 입력해주세요");
                                        Integer channelNumber = getChannelNumber(channels, sc);
                                        if (channelNumber == null) break;
                                        boolean flag2 = true;
                                        while(flag2){
                                            List<Message> messages = messageService.getMessagesByChannelId(channels.get(channelNumber - 1).getId());
                                            for(int i = 0; i < messages.size(); i++){
                                                System.out.println((i+1) + "." + messages.get(i));
                                            }
                                            System.out.println("============메뉴==========");
                                            System.out.println("1. 메세지 수정");
                                            System.out.println("2. 메세지 삭제");
                                            System.out.println("3. 이전메뉴로");
                                            System.out.print("선택: ");
                                            int messageMenu = sc.nextInt();
                                            sc.nextLine();

                                            switch (messageMenu) {
                                                case 1:
                                                    try {
                                                        System.out.println("수정하실 메세지를 선택해주세요");
                                                        Integer choose = getMessageNumber(messages, sc);
                                                        if (choose == null) break;
                                                        System.out.print("변경하실 메세지 내용을 입력해주세요: ");
                                                        String messageContext = sc.nextLine();
                                                        messageService.changeMessage(messages.get(choose - 1).getId(), messageContext);
                                                        break;
                                                    } catch (NotFoundException e) {
                                                        System.out.println("[NotFound]" + e.getMessage());
                                                        break;
                                                    }
                                                case 2:
                                                    try{
                                                        System.out.println("삭제하실 메세지를 선택해주세요");
                                                        Integer choose = getMessageNumber(messages, sc);
                                                        if (choose == null) break;
                                                        messageService.deleteMessage(messages.get(choose - 1).getId());
                                                        break;
                                                    } catch (NotFoundException e) {
                                                        System.out.println("[NotFound]" + e.getMessage());
                                                        break;
                                                    }
                                                case 3:
                                                    flag2 = false;
                                                    break;
                                                default:
                                                    System.out.println("잘못된 입력입니다.");
                                                    break;
                                            }

                                        }
                                        break;
                                    } catch (NotFoundException e) {
                                        System.out.println("[NotFound]" + e.getMessage());
                                        break;
                                    }
                                case 4:
                                    try{
                                        System.out.println("삭제하실 채널의 번호를 입력해주세요");
                                        Integer channelNumber = getChannelNumber(channels, sc);
                                        if(channelNumber == null) break;
                                        channelService.deleteChannel(channels.get(channelNumber - 1).getId());
                                        break;
                                    } catch (NotFoundException e) {
                                        System.out.println("[NotFound]" + e.getMessage());
                                        break;
                                    }
                                case 5:
                                    for (int i = 0; i < channels.size(); i++) {
                                        System.out.println((i+1) + "." + channels.get(i));
                                    }
                                    break;
                                case 6:
                                    flag4 = false;
                                    break;
                                default:
                                    System.out.println("잘못된 입력입니다.");
                                    break;
                            }
                        }
                        break;
                    } catch (NotFoundException e) {
                        System.out.println("[NotFound]" + e.getMessage());
                        break;
                    }
                case 3:
                    try {
                        System.out.println("사용자가 작성한 메세지를 조회합니다.");
                        boolean flag3 = true;
                        while (flag3){
                            List<Message> messages = messageService.getAllMessages(user.getUserName());
                            for (int i = 0; i < messages.size(); i++) {
                                System.out.println((i + 1) + "." + messages.get(i));
                            }
                            System.out.println("=========메뉴=========");
                            System.out.println("1. 메세지 수정");
                            System.out.println("2. 메세지 삭제");
                            System.out.println("3. 이전 메뉴로");
                            int messageMenu = sc.nextInt();
                            sc.nextLine();
                            switch (messageMenu) {
                                case 1:
                                    try {
                                        System.out.println("수정하실 메세지의 번호를 입력하세요: ");
                                        Integer choose = getMessageNumber(messages, sc);
                                        if (choose == null) break;
                                        System.out.println("변경하실 메세지 내용을 작성해주세요: ");
                                        String messageContext = sc.nextLine();
                                        messageService.changeMessage(messages.get(choose - 1).getId(), messageContext);
                                        break;
                                    } catch (NotFoundException e) {
                                        System.out.println("[NotFound]" + e.getMessage());
                                        break;
                                    }
                                case 2:
                                    try{
                                        System.out.println("삭제하실 메세지의 번호를 입력하세요:");
                                        Integer choose = getMessageNumber(messages, sc);
                                        if (choose == null) break;
                                        messageService.deleteMessage(messages.get(choose - 1).getId());
                                        break;
                                    }  catch (NotFoundException e) {
                                        System.out.println("[NotFound]" + e.getMessage());
                                        break;
                                    }
                                case 3:
                                    flag3 = false;
                                    break;
                                default:
                                    System.out.println("잘못된 입력입니다.");
                                    break;
                            }
                        }
                        break;
                    } catch (NotFoundException e) {
                        System.out.println("[NotFound]" + e.getMessage());
                    }
                case 4:
                    try{
                        System.out.print("채널명을 작성해주세요: ");
                        String channelName = sc.nextLine();
                        Channel channel = new Channel(UUID.randomUUID(), channelName);
                        channelService.addChannel(channel);
                        userService.addChannel(uuid, channel);
                        break;
                    } catch (DuplicateNameException e){
                        System.out.println("[Duplicate]" + e.getMessage());
                        break;
                    } catch (NotFoundException e) {
                        System.out.println("[NotFound]" + e.getMessage());
                        break;
                    }
                case 5:
                    try {
                        User userInformation = userService.getUserInformation(uuid);
                        System.out.println("사용자 이름: " + userInformation.getUserName() + ", 생성일자: " + userInformation.getCreatedAt());
                        break;
                    }catch (NotFoundException e){
                        System.out.println("[NotFound]" + e.getMessage());
                    }
                case 6:
                    flag = false;
                    break;
                default:
                    System.out.println("잘못된 입력입니다.");
                    break;
            }

        }
    }

    private static Integer getChannelNumber(List<Channel> channels, Scanner sc) {
        for (int i = 0; i < channels.size(); i++) {
            System.out.println((i+1) + "." + channels.get(i));
        }
        System.out.print("선택: ");
        int channelNumber = sc.nextInt();
        if (channelNumber > channels.size()) {
            System.out.println("잘못된 번호입니다.");
            return null;
        }
        sc.nextLine();
        return channelNumber;
    }

    private static Integer getMessageNumber(List<Message> messages, Scanner sc) {
        System.out.print("선택: ");
        int channelNumber = sc.nextInt();
        if (channelNumber > messages.size()) {
            System.out.println("잘못된 번호입니다.");
            return null;
        }
        sc.nextLine();
        return channelNumber;
    }


}