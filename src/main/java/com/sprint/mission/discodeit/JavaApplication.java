package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.message.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.user.LoginDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.user.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.ChannelType.PUBLIC;

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("--- 테스트 시작 ---");

        //리포지토리 인스턴스 생성
        UserRepository userRepository = new JCFUserRepository();
        UserStatusRepository userStatusRepository = new JCFUserStatusRepository();
        BinaryContentRepository binaryContentRepository = new JCFBinaryContentRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        ReadStatusRepository readStatusRepository = new JCFReadStatusRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        //서비스 인스턴스 생성 및 의존성 주입
        AuthService authService = new BasicAuthService(userRepository);
        UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        UserStatusService userStatusService = new BasicUserStatusService(userRepository, userStatusRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository, readStatusRepository, messageRepository);
        BinaryContentService binaryContentService = new BasicBinaryContentService(binaryContentRepository);
        ReadStatusService readStatusService = new BasicReadStatusService(readStatusRepository, userRepository, channelRepository);


        //사용자 생성
        UserCreateDto user1Dto = new UserCreateDto("김코딩", "user1@example.com", "1234");
        UserCreateDto user2Dto = new UserCreateDto("이코딩", "user2@example.com", "5678");
        User user1 = userService.create(user1Dto);
        User user2 = userService.create(user2Dto);
        System.out.println("사용자 생성 완료: " + user1.getUsername() + ", " + user2.getUsername());

        //로그인 테스트
        LoginDto loginDto = new LoginDto("김코딩", "1234");
        try {
            authService.userMatch(loginDto);
            System.out.println("로그인 성공: " + loginDto.username());
        } catch (IllegalArgumentException e) {
            System.out.println("로그인 실패: " + e.getMessage());
        }

        //유저 상태 생성
        UserStatusCreateDto statusCreateDto = new UserStatusCreateDto(user1.getId(), true);
        userStatusService.create(statusCreateDto);
        System.out.println(user1.getUsername() + " 의 상태 생성 완료");

        //유저 상태 업데이트
        UserStatusUpdateDto statusUpdateDto = new UserStatusUpdateDto(false);
        userStatusService.updateByUserId(user1.getId(), statusUpdateDto);
        System.out.println(user1.getUsername() + " 의 상태를 false로 업데이트");

        //유저 온라인 상태 확인
        boolean isOnline = userStatusService.isOnlineByUserId(user1.getId(), 5);
        System.out.println(user1.getUsername() + " 의 온라인 상태 확인 (5분 이내 활동): " + isOnline);

        //프로필 등록, 삭제
        binaryContentService.registerProfile(user1.getId(), "profile.jpg");
        System.out.println(user1.getUsername() + " 의 프로필 사진 업로드 완료");
        binaryContentService.deleteProfile(user1.getId());
        System.out.println(user1.getUsername() + " 의 프로필 사진 삭제 완료");

        //공개 채널 생성
        ChannelCreatePublicDto publicChannelDto = new ChannelCreatePublicDto(PUBLIC, "공개채널", "모두를 위한 채널");
        Channel publicChannel = channelService.create(publicChannelDto);
        System.out.println("공개 채널 생성 완료");

        //비공개 채널 생성
        List<UUID> participantIds = List.of(user1.getId(), user2.getId());
        ChannelCreatePrivateDto privateChannelDto = new ChannelCreatePrivateDto(PRIVATE, "비공개채널", "비공개 입니다.", participantIds);
        Channel privateChannel = channelService.create(privateChannelDto);
        System.out.println("비공개 채널 생성 완료");

        //메시지 생성
        Message msg1 = messageService.create("안녕하세요", publicChannel.getId(), user1.getId());
        Message msg2 = messageService.create("반갑습니다", privateChannel.getId(), user2.getId());
        System.out.println("메시지 2개 생성 완료");

        //첨부파일 포함 메시지
        BinaryContentDto attachmentDto = new BinaryContentDto(user1.getId(), "attach.jpeg", "첨부파일",new byte[200000]);
        CreateMessageRequest requestAttachment = new CreateMessageRequest(publicChannel.getId(), user1.getId(), "첨부파일 있는 메세지", List.of(attachmentDto));
        Message attachment = messageService.create(requestAttachment);
        System.out.println(user1.getUsername() + " 의 첨부파일이 포함된 메시지 전송 완료");

        //유저1 공개 채널 입장
        ReadStatusCreateDto user1PublicReadStatusDto = new ReadStatusCreateDto(user1.getId(), publicChannel.getId());
        ReadStatus user1ReadStatus = readStatusService.create(user1PublicReadStatusDto);
        System.out.println(user1.getUsername() + "이 공개채널 에 입장, 최신 메세지 읽음");

        //유저1이 공개 채널에서 비공개 채널로 이동(공개채널 에서 삭제) 해서 ReadStatus 생성
        readStatusService.delete(user1ReadStatus.getId(), user1ReadStatus.getChannelId());
        System.out.println(user1.getUsername() + "이 공개 채널을 떠났습니다.");
        ReadStatusCreateDto user1PrivateReadStatusDto = new ReadStatusCreateDto(user1.getId(), privateChannel.getId());
        ReadStatus user1PrivateReadStatus = readStatusService.create(user1PrivateReadStatusDto);
        System.out.println(user1.getUsername() + "이 비공개 채널로 이동했습니다.");

        //유저2 비공개 채널 입장
        ReadStatusCreateDto user2ReadStatusDto = new ReadStatusCreateDto(user2.getId(), privateChannel.getId());
        ReadStatus user2ReadStatus = readStatusService.create(user2ReadStatusDto);
        System.out.println(user2.getUsername() + "이 비공개채널 에 입장, 최신 메시지 읽음");

        //유저1 메시지를 읽음 (lastReadAt 업데이트)
        Instant readTimestamp = Instant.now();
        userStatusService.updateByUserId(user1.getId(), new UserStatusUpdateDto(true));
        System.out.println(user1.getUsername() + " 최신 메시지 읽음 (" + readTimestamp + ")");

        //사용자 삭제
        userService.delete(user1.getId());
        userService.delete(user2.getId());
        System.out.println("사용자 '" + user1.getUsername() + "' 삭제 완료");
        System.out.println("사용자 '" + user2.getUsername() + "' 삭제 완료");

        //삭제 확인
        userService.findAll();


        System.out.println("--- 테스트 종료 ---");
    }

}