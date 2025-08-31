package com.sprint.mission.discodeit.service.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.repository.UserRepositoryInterface;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserRepository implements UserRepositoryInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private Map<UUID, User> data;

    // 생성자 - user.ser 파일에서 데이터 로드
    public FileUserRepository() { data = loadData();}

    // 유저 생성 (+전체 유저 관리 Map에 추가 + saveData)
    public User createUser(String name, String status, String email){
        User newUser = new User(name, status, email);
        data.put(newUser.getId(), newUser);
        saveData();
        return newUser;
    }


    // 유저 아이디로 특정 유저 조회
    public User findById(UUID userId){
        if(!data.containsKey(userId)){ // 존재하지 않을 시 null 반환
            return null;
        }
        return data.get(userId); // 존재할 시 user 객체 참조값 반환
    }
    // 전체 유저 조회
    public List<User> findAll(){
        return data.values().stream().toList(); // 아무 유저도 존재하지 않을 시 빈 List 반환
    }

    public void updateName(User user, String updatedName){
        user.setName(updatedName);
        saveData();
    }

    // 유저 상태 수정
    public void updateStatus(User user, String updatedStatus){
        user.setStatus(updatedStatus);
        saveData();
    }
    // 유저 이메일 수정
    public void updateEmail(User user, String updatedEmail){
        user.setEmail(updatedEmail);
        saveData();
    }

    @Override
    public void deleteUser(User user) {
        data.remove(user.getId());
        saveData();
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
