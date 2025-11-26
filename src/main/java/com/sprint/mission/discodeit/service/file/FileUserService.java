package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserService implements Serializable, UserService {
    private static final long serialVersionUID = 1L;

    private Map<UUID, User> data;
    private FileMessageService fileMessageService = new FileMessageService();
    private FileUserChannelService fileUserChannelService = new FileUserChannelService();


    // 생성자 - user.ser 파일에서 데이터 로드
    public FileUserService() { data = loadData();}

    // *저장로직* 유저 생성 (+전체 유저 관리 Map에 추가 + saveData)
    @Override
    public User createUser(String name, String status, String email){
        User newUser = new User(name, status, email);
        data.put(newUser.getId(), newUser);
        saveData();
        return newUser;
    }


    // *저장로직* 유저 아이디로 특정 유저 조회
    @Override
    public User findById(UUID userId){
        return data.get(userId); // 존재할 시 user 객체 참조값 반환
    }
    // *저장로직* 전체 유저 조회
    @Override
    public List<User> findAll(){
        return data.values().stream().toList(); // 아무 유저도 존재하지 않을 시 빈 List 반환
    }

    // *비즈니스로직* 특정 채널의 모든 유저 조회
    @Override
    public List<User> findAllUsersByChannelId(UUID channelId){
        List<UUID> userIds = fileUserChannelService.findUserListOfChannelId(channelId);
        return userIds.stream()
                .map(this::findById).collect(Collectors.toList());
        // 아무 유저도 존재하지 않을 시 빈 List 반환
    }

    // *비즈니스로직* 유저 이름 수정
    @Override
    public boolean updateName(User user, String updatedName){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        // 존재할 시
        user.setName(updatedName); // 유저 이름 변경
        saveData();
        fileMessageService.modifyAuthorName(user.getId(), updatedName); // 유저가 작성한 메시지들 유저명 필드 변경
        return true;
    }
    // *저장로직* 유저 상태 수정
    @Override
    public boolean updateStatus(User user, String updatedStatus){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        user.setStatus(updatedStatus); // 존재할 시 update & return true
        saveData();
        return true;
    }
    // *저장로직* 유저 이메일 수정
    @Override
    public boolean updateEmail(User user, String updatedEmail){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        user.setEmail(updatedEmail); // 존재할 시 update & return true
        saveData();
        return true;
    }

    // *비즈니스로직* 유저 삭제
    @Override
    public boolean deleteUser(User user){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        //유저 존재 시
        fileMessageService.anonymizeAuthorName(user.getId()); // 해당 유저가 작성한 메시지 작성자명 (알 수 없음)으로 변경
        fileUserChannelService.removeAllOfUser(user.getId()); // 해당 유저의 유저-채널 관계 삭제
        data.remove(user.getId()); // 유저 삭제 후 return true
        saveData();
        return true;
    }


    // 데이터 로드
    public Map<UUID, User> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream
                (new FileInputStream("user.ser")))) {
            return (Map<UUID, User>) ois.readObject(); // 읽어온 역직렬화된 맵 덮어쓰기
        } catch (FileNotFoundException e) { // 읽어올 파일 존재X (처음 실행되는 경우)
            Map<UUID, User> newData = new HashMap<>(); // 새 맵 생성해서 반환
            return newData;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Error] 유저 데이터 로딩 실패", e);
        }
    }

    // 데이터 덮어쓰기 (저장)
    public void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream("user.ser")))){
            oos.writeObject(data); // 기존 파일 덮어쓰기
        } catch (IOException e) {
            throw new RuntimeException("[Error] 유저 데이터 저장 실패", e);
        }
    }

    // 유저 존재 여부 확인
    public boolean notExist(User user) {
        return !data.containsKey(user.getId());
    }
}
