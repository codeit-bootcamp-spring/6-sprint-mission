package com.sprint.mission.discodeit.service.file;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserChannelService implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<UserChannelRelationship> data;

    // 생성자
    public FileUserChannelService() { data = loadData(); }


    /*전부 저장 로직*/

    // 새 유저-채널 관계 추가
    public boolean put(UUID userId, UUID channelId){
        if(isExist(userId, channelId)){ // 이미 관계 존재
            return false;
        }
        data.add(new UserChannelRelationship(userId, channelId));
        saveData();
        return true;
    }

    // 유저-채널 단일 관계 삭제
    public boolean remove(UUID userId, UUID channelId){
        if(!isExist(userId, channelId)){ // 관계 존재 X
            return false;
        }
        data.removeIf(uc ->
                uc.getUserId().equals(userId) && uc.getChannelId().equals(channelId));
        saveData();
        return true;
    }
    // 특정 유저 모든 관계 삭제
    public void removeAllOfUser(UUID userId){
        data.removeIf(uc -> uc.getUserId().equals(userId));
        saveData();
    }
    // 특정 채널 모든 관계 삭제
    public void removeAllOfChannel(UUID channelId){
        data.removeIf(uc -> uc.getChannelId().equals(channelId));
        saveData();
    }


    // 유저 Id로 속한 채널 list 조회
    public List<UUID> findChannelListOfUserId(UUID userId){
        return data.stream().filter(uc -> uc.getUserId().equals(userId))
                .map(UserChannelRelationship::getChannelId)
                .collect(Collectors.toList());
    }

    // 채널 Id로 속한 유저 list 조회
    public List<UUID> findUserListOfChannelId(UUID channelId){
        return data.stream().filter(uc -> uc.getChannelId().equals(channelId))
                .map(UserChannelRelationship::getUserId)
                .collect(Collectors.toList());
    }


    // 데이터 로드
    public List<UserChannelRelationship> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream
                (new FileInputStream("userChannelRelationship.ser")))) {
            return (List<UserChannelRelationship>) ois.readObject(); // 읽어온 역직렬화된 맵 덮어쓰기
        } catch (FileNotFoundException e) { // 읽어올 파일 존재X (처음 실행되는 경우)
            List<UserChannelRelationship> newData = new ArrayList<>(); // 새 맵 생성해서 반환
            return newData;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Error] 유저-채널 관계 데이터 로딩 실패", e);
        }
    }

    // 데이터 덮어쓰기 (저장)
    public void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream("userChannelRelationship.ser")))){
            oos.writeObject(data); // 기존 파일 덮어쓰기
        } catch (IOException e) {
            throw new RuntimeException("[Error] 유저-채널 관계 데이터 저장 실패", e);
        }
    }

    // 유저-관계 존재 여부 확인
    public boolean isExist(UUID userId, UUID channelId){
        return data.stream().anyMatch(uc ->
                uc.getUserId().equals(userId) && uc.getChannelId().equals(channelId));
    }
}
