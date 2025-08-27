package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.io.*; // 입출력 관련 클래스들

public class JCFManagerService {

    // 각 서비스 구현체를 저장할 파일명 정의
    private static final String USERS_FILE = "users_data.dat";
    private static final String CHANNELS_FILE = "channels_data.dat";
    private static final String MESSAGES_FILE = "messages_data.dat";


    public void saveObject(Object obj, String fileName) {
        // try-with-resources: 스트림을 자동으로 닫기.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(obj); // 객체를 바이트 스트림으로 변환하여 파일에 쓰기
            System.out.println("DataPersistence: '" + fileName + "' 저장 완료.");
        } catch (IOException e) {
            System.err.println("DataPersistence: '" + fileName + "' 저장 실패: " + e.getMessage());
            e.printStackTrace(); // 개발 중 오류 추적을 위해 스택 트레이스 출력
        }
    }


    public Object loadObject(String fileName) {
        File file = new File(fileName);
        // 파일이 없거나 비어 있으면 null 반환 (새로 시작해야 함을 의미)
        if (!file.exists() || file.length() == 0) {
            if (!file.exists()) {
                System.out.println("DataPersistence: '" + fileName + "' 파일 없음. 새로운 데이터로 시작합니다.");
            } else { // file.length() == 0 인 경우 (빈 파일)
                System.out.println("DataPersistence: '" + fileName + "' 파일 내용이 비어 있습니다. 새로운 데이터로 시작합니다.");
            }
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = ois.readObject(); // 파일에서 바이트 스트림을 읽어 객체로 변환 (역직렬화)
            System.out.println("DataPersistence: '" + fileName + "' 불러오기 완료.");
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            // IOException: 파일 읽기 오류, ClassNotFoundException: 직렬화된 클래스를 찾을 수 없는 경우
            System.err.println("DataPersistence: '" + fileName + "' 불러오기 실패: " + e.getMessage());
            System.err.println("원인: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return null; // 오류 발생 시 null 반환 (새로운 인스턴스 생성을 유도)
        }
    }


    public void saveAllServices(UserRepository userRepository, ChannelRepository channelRepository,
                                MessageRepository messageRepository) {
        saveObject(userRepository, USERS_FILE);
        saveObject(channelRepository, CHANNELS_FILE);
        saveObject(messageRepository, MESSAGES_FILE);
    }


    public UserRepository loadUserRepository() {
        Object obj = loadObject(USERS_FILE);
        // 불러온 객체가 FileUserService 인스턴스인 경우에만 UserRepository 타입으로 캐스팅하여 반환
        return (obj instanceof FileUserService) ? (UserRepository) obj : null;
    }


    public ChannelRepository loadChannelRepository() {
        Object obj = loadObject(CHANNELS_FILE);
        return (obj instanceof FileChannelService) ? (ChannelRepository) obj : null;
    }


    public MessageRepository loadMessageRepository() {
        Object obj = loadObject(MESSAGES_FILE);
        return (obj instanceof FileMessageService) ? (MessageRepository) obj : null;
    }
}
