package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class JFCUserService {
    private static final JFCUserService instance = new JFCUserService();
    private final Map<UUID, User> data; // 전체 유저 관리 Map 필드
    private final UserChannelService userChannelService = UserChannelService.getInstance();
    private final JFCMessageService messageService = JFCMessageService.getInstance();

    // 생성자 - 싱글톤 패턴 (User data의 유일성 보장)
    private JFCUserService(){
        data = new HashMap<>(); // 유저 관리 Map 초기화
    }
    // GetInstance
    public static JFCUserService getInstance(){
        return instance;
    }


    // 유저 생성 (+전체 유저 관리 Map에 유저 추가)
    public User createUser(String name, String status, String email){
        User newUser = new User(name, status, email);
        data.put(newUser.getId(), newUser);
        return newUser; // 생성한 유저 객체 참조값 반환
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
    // 특정 채널의 모든 유저 조회
    public List<User> findAllUsersByChannelId(UUID channelId){
        List<UUID> userIds = userChannelService.findUserListOfChannelId(channelId);
        return userIds.stream()
                .map(this::findById).collect(Collectors.toList());
        // 아무 유저도 존재하지 않을 시 빈 List 반환
    }


    // 유저 이름 수정
    public boolean updateName(User user, String updatedName){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        // 존재할 시
        user.setName(updatedName); // 유저 이름 변경
        messageService.modifyAuthorName(user.getId(), updatedName); // 유저가 작성한 메시지들 유저명 필드 변경
        return true;
    }
    // 유저 상태 수정
    public boolean updateStatus(User user, String updatedStatus){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        user.setStatus(updatedStatus); // 존재할 시 update & return true
        return true;
    }
    // 유저 이메일 수정
    public boolean updateEmail(User user, String updatedEmail){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        user.setEmail(updatedEmail); // 존재할 시 update & return true
        return true;
    }


    //유저 삭제
    public boolean deleteUser(User user){
        if(notExist(user)){ return false; } // 유저 존재하지 않을 시 return false
        //유저 존재 시
        messageService.anonymizeAuthorName(user.getId()); // 해당 유저가 작성한 메시지 작성자명 (알 수 없음)으로 변경
        userChannelService.removeAllOfUser(user.getId()); // 해당 유저의 유저-채널 관계 삭제
        data.remove(user.getId()); // 유저 삭제 후 return true
        return true;
    }



    // 유저 존재 여부 확인
    public boolean notExist(User user){
        return !data.containsKey(user.getId());
    }

}
