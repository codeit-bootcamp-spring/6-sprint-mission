package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFManagerService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.Scanner;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // User ID는 UUID를 사용



public class JavaApplication {
    private static UserRepository userRepository;
    private static MessageRepository messageRepository;
    private static ChannelRepository channelRepository;
        //[ ] 등록
        //[ ] 조회(단건, 다건)
        //[ ] 수정
        //[ ] 수정된 데이터 조회
        //[ ] 삭제
        //[ ] 조회를 통해 삭제되었는지 확인

        // 데이터 영속성 관리를 담당하는 객체
    private static JCFManagerService persistenceManager = new JCFManagerService();
    private static Scanner sc = new Scanner(System.in);


    public static void main(String[] args) {
        // 애플리케이션 시작 시 기존 데이터(서비스 인스턴스)를 파일에서 로드하거나 새로 생성
        loadServices();

        boolean running = true; // 애플리케이션 실행 상태를 제어하는 플래그
        while (running) { // 프로그램이 종료될 때까지 반복
            printMainMenu(); // 메인 메뉴 출력
            int choice = getUserChoice(); // 사용자로부터 메뉴 선택 입력 받기

            switch (choice) {
                case 1: // 사용자 관리 메뉴 진입
                    manageUsers();
                    break;
                case 2: // 채널 관리 메뉴 진입
                    manageChannels();
                    break;
                case 3: // 메시지 관리 메뉴 진입
                    manageMessages();
                    break;
                case 4: // 현재 데이터를 파일에 즉시 저장
                    saveServices();
                    break;
                case 5: // 애플리케이션 종료
                    saveServices(); // 종료 직전에 변경된 데이터 최종 저장
                    System.out.println("프로그램을 종료합니다. 안녕히 계세요!");
                    running = false; // 루프 종료하여 프로그램 종료
                    break;
                default: // 잘못된 입력 처리
                    System.out.println("잘못된 선택입니다. 1에서 5 사이의 번호를 입력해주세요.");
                    break;
            }
            System.out.println("\n----------------------------------------\n"); // 메뉴 구분선
        }
        sc.close(); // 프로그램 종료, Scanner 자원 해제
    }

    // --- 서비스 인스턴스 로드 및 저장 메서드 ---


    private static void loadServices() {
        System.out.println("===== 애플리케이션 시작: 데이터 로드 =====");
        // 각 서비스 구현체 객체를 파일에서 로드 시도. 파일이 없거나 로드 실패 시 새로운 인스턴스 생성.
        UserRepository UserRepository = persistenceManager.loadUserRepository();
        if (userRepository == null) {
            userRepository = new FileUserService();
            System.out.println("새로운 사용자 서비스 인스턴스 생성.");
        }

        channelRepository = persistenceManager.loadChannelRepository();
        if (channelRepository == null) {
            channelRepository = new FileChannelService();
            System.out.println("새로운 채널 서비스 인스턴스 생성.");
        }

        messageRepository = persistenceManager.loadMessageRepository();
        if (messageRepository == null) {
            messageRepository = new FileMessageService();
            System.out.println("새로운 메시지 서비스 인스턴스 생성.");
        }
        System.out.println("=========================================\n");
    }


    private static void saveServices() {
        System.out.println("===== 현재 데이터 저장 시도 중 =====");
        persistenceManager.saveAllServices(userRepository, channelRepository, messageRepository);
        System.out.println("===================================\n");
    }



    private static void printMainMenu() {
        System.out.println("====== 채팅 앱 메인 메뉴 ======");
        System.out.println("1. 사용자 (User) 관리");
        System.out.println("2. 채널 (Channel) 관리");
        System.out.println("3. 메시지 (Message) 관리");
        System.out.println("4. 데이터 저장 (Save Now)"); // 현재 상태를 즉시 파일에 저장하는 메뉴
        System.out.println("5. 종료");
        System.out.print("메뉴를 선택하세요: ");
    }


    private static int getUserChoice() {
        while (!sc.hasNextInt()) { // 정수가 입력될 때까지 반복
            System.out.println("숫자를 입력해주세요.");
            sc.next(); // 잘못된 입력(토큰) 소비하여 무한 루프 방지
            System.out.print("메뉴를 다시 선택하세요: ");
        }
        int choice = sc.nextInt(); // 정수 입력 받기
        sc.nextLine(); // 개행 문자(\n) 소비
        return choice;
    }

    // --- 사용자 (User) 관리 메뉴 및 기능 ---


    private static void manageUsers() {
        boolean inUserMenu = true;
        while (inUserMenu) {
            System.out.println("\n--- 사용자 (User) 관리 ---");
            System.out.println("1. 등록");
            System.out.println("2. 전체 조회");
            System.out.println("3. 단건 조회 (ID로)");
            System.out.println("4. 수정");
            System.out.println("5. 삭제");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print("작업을 선택하세요: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1: // 사용자 등록 (Create)
                    System.out.print("사용할 사용자 ID (UUID 형식으로 입력): ");
                    String userIdInput = sc.nextLine();
                    System.out.print("사용자 이름 입력: ");
                    String name = sc.nextLine();
                    System.out.print("메시지 입력: ");
                    String message = sc.nextLine();
                    if (name.isEmpty() || message.isEmpty()) { // 필수 정보 누락 시 경고
                        System.out.println("이름 과 메세지가 비어있습니다. 다시 시도해주세요.");
                        break;
                    }
                    try {
                        UUID newUserId = UUID.fromString(userIdInput); // 입력된 문자열을 UUID로 변환
                        userRepository.createUser(new User(newUserId, name)); // 생성된 ID를 바로 User 객체에 전달
                    } catch (IllegalArgumentException e) {
                        System.out.println("입력된 사용자 ID가 올바른 UUID 형식이 아닙니다.");
                    }
                    break;
                case 2: // 모든 사용자 조회 (Read All)
                    List<User> users = userRepository.readAllUsers();
                    if (users.isEmpty()) {
                        System.out.println("등록된 사용자가 없습니다.");
                    } else {
                        System.out.println("\n--- 사용자 목록 ---");
                        users.forEach(System.out::println);
                    }
                    break;
                case 3: // ID로 사용자 단건 조회 (Read One by ID)
                    System.out.print("조회할 사용자 ID 입력 (UUID 전체 입력): ");
                    String userIdStr = sc.nextLine();
                    try {
                        UUID userId = UUID.fromString(userIdStr); // 입력 문자열을 UUID로 변환
                        Optional<User> userOpt = userRepository.readUser(userId);
                        userOpt.ifPresentOrElse( // Optional에 값이 있으면 출력, 없으면 사용자에게 알림
                                user -> System.out.println("조회된 사용자: " + user),
                                () -> System.out.println("ID " + userIdStr + "의 사용자를 찾을 수 없습니다.")
                        );
                    } catch (IllegalArgumentException e) { // UUID 변환 실패 시
                        System.out.println("유효하지 않은 UUID 형식입니다.");
                    }
                    break;
                case 4: // 사용자 정보 수정 (Update)
                    System.out.print("수정할 사용자 ID 입력 (UUID 전체 입력): ");
                    String updateUserIdStr = sc.nextLine();
                    try {
                        UUID updateUserId = UUID.fromString(updateUserIdStr);
                        Optional<User> userToUpdateOpt = userRepository.readUser(updateUserId);
                        userToUpdateOpt.ifPresentOrElse(user -> { // 해당 사용자가 존재할 경우 수정 진행
                            System.out.print("새 이름 입력 (현재: " + user.getName() + ", 변경 없으면 Enter): ");
                            String newName = sc.nextLine();

                            if (!newName.isEmpty()) user.setName(newName); // 입력이 있을 경우에만 변경
                            userRepository.updateUser(user); // 서비스 계층에서 업데이트 로직 수행
                        }, () -> System.out.println("ID " + updateUserIdStr + "의 사용자를 찾을 수 없습니다."));
                    } catch (IllegalArgumentException e) {
                        System.out.println("유효하지 않은 UUID 형식입니다.");
                    }
                    break;
                case 5: // 사용자 삭제 (Delete)
                    System.out.print("삭제할 사용자 ID 입력 (UUID 전체 입력): ");
                    String deleteUserIdStr = sc.nextLine();
                    try {
                        UUID deleteUserId = UUID.fromString(deleteUserIdStr);
                        userRepository.deleteUser(deleteUserId);
                        System.out.println("\n--- 삭제 후 사용자 목록 확인 ---");
                        userRepository.readAllUsers().forEach(System.out::println); // 삭제 후 변경된 목록 확인
                    } catch (IllegalArgumentException e) {
                        System.out.println("유효하지 않은 UUID 형식입니다.");
                    }
                    break;
                case 6: // 메인 메뉴로 돌아가기
                    inUserMenu = false;
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    break;
                default:
                    System.out.println("잘못된 선택입니다.");
                    break;
            }
        }
    }

    // --- 채널 (Channel) 관리 메뉴 및 기능 ---


    private static void manageChannels() {
        boolean inChannelMenu = true;
        while (inChannelMenu) {
            System.out.println("\n--- 채널 (Channel) 관리 ---");
            System.out.println("1. 등록");
            System.out.println("2. 전체 조회");
            System.out.println("3. 단건 조회 (ID로)");
            System.out.println("4. 수정");
            System.out.println("5. 삭제");
            System.out.println("6. 메인 메뉴로 돌아가기");
            System.out.print("➡작업을 선택하세요: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1: // 채널 등록 (Create)
                    System.out.print("채널 이름 입력: ");
                    String channelName = sc.nextLine();
                    if (channelName.isEmpty()) {
                        System.out.println("⚠채널 이름이 비어있습니다. 다시 시도해주세요.");
                        break;
                    }
                    channelRepository.createChannel(new Channel(channelName)); // ID는 서비스에서 자동 부여
                    break;
                case 2: // 모든 채널 조회 (Read All)
                    List<Channel> channels = channelRepository.readAllChannels();
                    if (channels.isEmpty()) {
                        System.out.println("ℹ등록된 채널이 없습니다.");
                    } else {
                        System.out.println("\n--- 채널 목록 ---");
                        channels.forEach(System.out::println);
                    }
                    break;
                case 3: // ID로 채널 단건 조회 (Read One by ID)
                    System.out.print("조회할 채널 ID 입력: ");
                    long channelId = getUserChoice();
                    Optional<Channel> channelOpt = channelRepository.readChannel(channelId);
                    channelOpt.ifPresentOrElse(
                            channel -> System.out.println("조회된 채널: " + channel),
                            () -> System.out.println("ID " + channelId + "의 채널을 찾을 수 없습니다.")
                    );
                    break;
                case 4: // 채널 정보 수정 (Update)
                    System.out.print("수정할 채널 ID 입력: ");
                    long updateChannelId = getUserChoice();
                    Optional<Channel> channelToUpdateOpt = channelRepository.readChannel(updateChannelId);
                    channelToUpdateOpt.ifPresentOrElse(channel -> {
                        System.out.print("새 채널 이름 입력 (현재: " + channel.getName() + ", 변경 없으면 Enter): ");
                        String newChannelName = sc.nextLine();
                        if (!newChannelName.isEmpty()) channel.setName(newChannelName);
                        channelRepository.updateChannel(channel);
                    }, () -> System.out.println("ID " + updateChannelId + "의 채널을 찾을 수 없습니다."));
                    break;
                case 5: // 채널 삭제 (Delete)
                    System.out.print("삭제할 채널 ID 입력: ");
                    long deleteChannelId = getUserChoice();
                    channelRepository.deleteChannel(deleteChannelId);
                    System.out.println("\n--- 삭제 후 채널 목록 확인 ---");
                    channelRepository.readAllChannels().forEach(System.out::println);
                    break;
                case 6: // 메인 메뉴로 돌아가기
                    inChannelMenu = false;
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    break;
                default:
                    System.out.println("잘못된 선택입니다.");
                    break;
            }
        }
    }

    // --- 메시지 (Message) 관리 메뉴 및 기능 ---


    private static void manageMessages() {
        boolean inMessageMenu = true;
        while (inMessageMenu) {
            System.out.println("\n--- 메시지 (Message) 관리 ---");
            System.out.println("1. 등록");
            System.out.println("2. 전체 조회");
            System.out.println("3. 단건 조회 (ID로)");
            System.out.println("4. 채널별 메시지 조회");
            System.out.println("5. 사용자별 메시지 조회");
            System.out.println("6. 수정");
            System.out.println("7. 삭제");
            System.out.println("8. 메인 메뉴로 돌아가기");
            System.out.print("작업을 선택하세요: ");

            int choice = getUserChoice();
            switch (choice) {
                case 1: // 메시지 등록 (Create)
                    System.out.print("메시지를 보낼 사용자(User) ID 입력 (UUID 전체 입력): ");
                    String senderIdStr = sc.nextLine();
                    System.out.print("메시지를 보낼 채널(Channel) ID 입력: ");
                    long targetChannelId = getUserChoice();
                    System.out.print("메시지 내용 입력: ");
                    String content = sc.nextLine();

                    try {
                        UUID senderId = UUID.fromString(senderIdStr); // UUID 문자열을 UUID 객체로 변환

                        // 메시지 생성을 위한 사용자 및 채널 유효성 검사 (실제로 존재하는지 확인)
                        Optional<User> senderOpt = userRepository.readUser(senderId);
                        Optional<Channel> channelOpt = channelRepository.readChannel(targetChannelId);

                        if (senderOpt.isEmpty()) {
                            System.out.println("보낸 사용자 ID " + senderIdStr + "를 찾을 수 없습니다. 메시지를 등록할 수 없습니다.");
                            break;
                        }
                        if (channelOpt.isEmpty()) {
                            System.out.println("채널 ID " + targetChannelId + "를 찾을 수 없습니다. 메시지를 등록할 수 없습니다.");
                            break;
                        }
                        if (content.isEmpty()) {
                            System.out.println("메시지 내용이 비어있습니다. 다시 시도해주세요.");
                            break;
                        }
                        // 모든 조건 충족 시 메시지 등록
                        messageRepository.createMessage(new Message(senderId, targetChannelId, content)); // ID는 서비스에서 자동 부여
                    } catch (IllegalArgumentException e) {
                        System.out.println("유효하지 않은 사용자 ID (UUID) 형식입니다.");
                    }
                    break;
                case 2: // 모든 메시지 조회 (Read All)
                    List<Message> messages = messageRepository.readAllMessages();
                    if (messages.isEmpty()) {
                        System.out.println("등록된 메시지가 없습니다.");
                    } else {
                        System.out.println("\n--- 메시지 목록 ---");
                        messages.forEach(System.out::println);
                    }
                    break;
                case 3: // ID로 메시지 단건 조회 (Read One by ID)
                    System.out.print("조회할 메시지 ID 입력: ");
                    long messageId = getUserChoice();
                    Optional<Message> messageOpt = messageRepository.readMessage(messageId);
                    messageOpt.ifPresentOrElse(
                            message -> System.out.println("조회된 메시지: " + message),
                            () -> System.out.println("ID " + messageId + "의 메시지를 찾을 수 없습니다.")
                    );
                    break;
                case 4: // 채널 ID로 메시지 조회
                    System.out.print("메시지를 조회할 채널 ID 입력: ");
                    long searchChannelId = getUserChoice();
                    List<Message> channelMessages = messageRepository.readMessagesByChannelId(searchChannelId);
                    if (channelMessages.isEmpty()) {
                        System.out.println("채널 ID " + searchChannelId + "에는 메시지가 없습니다.");
                    } else {
                        System.out.println("\n--- 채널 ID " + searchChannelId + "의 메시지 목록 ---");
                        channelMessages.forEach(System.out::println);
                    }
                    break;
                case 5: // 사용자 ID로 메시지 조회
                    System.out.print("메시지를 조회할 사용자 ID 입력 (UUID 전체 입력): ");
                    String searchSenderIdStr = sc.nextLine();
                    try {
                        UUID searchSenderId = UUID.fromString(searchSenderIdStr);
                        List<Message> senderMessages = messageRepository.readMessagesBySenderUserId(searchSenderId);
                        if (senderMessages.isEmpty()) {
                            System.out.println("사용자 ID " + searchSenderIdStr + "가 보낸 메시지가 없습니다.");
                        } else {
                            System.out.println("\n--- 사용자 ID " + searchSenderIdStr + "가 보낸 메시지 목록 ---");
                            senderMessages.forEach(System.out::println);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("유효하지 않은 사용자 ID (UUID) 형식입니다.");
                    }
                    break;
                case 6: // 메시지 내용 수정 (Update)
                    System.out.print("수정할 메시지 ID 입력: ");
                    long updateMessageId = getUserChoice();
                    Optional<Message> messageToUpdateOpt = messageRepository.readMessage(updateMessageId);
                    messageToUpdateOpt.ifPresentOrElse(message -> {
                        System.out.print("새 메시지 내용 입력 (현재: '" + message.getContent() + "', 변경 없으면 Enter): ");
                        String newContent = sc.nextLine();
                        if (!newContent.isEmpty()) {
                            message.setContent(newContent);
                        }
                        messageRepository.updateMessage(message);
                    }, () -> System.out.println("ID " + updateMessageId + "의 메시지를 찾을 수 없습니다."));
                    break;
                case 7: // 메시지 삭제 (Delete)
                    System.out.print("삭제할 메시지 ID 입력: ");
                    long deleteMessageId = getUserChoice();
                    messageRepository.deleteMessage(deleteMessageId);
                    System.out.println("\n--- 삭제 후 메시지 목록 확인 ---");
                    messageRepository.readAllMessages().forEach(System.out::println);
                    break;
                case 8: // 메인 메뉴로 돌아가기
                    inMessageMenu = false;
                    System.out.println("메인 메뉴로 돌아갑니다.");
                    break;
                default:
                    System.out.println("잘못된 선택입니다.");
                    break;
            }
        }
    }
}