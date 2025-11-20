package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    Path directory = Paths.get(System.getProperty("user.dir"), "UserData");

    @Override
    public User create(String name) {
        FileInitSaveLoad.init(directory);

        User user = new User(name);

        Path filePath = directory.resolve(user.getCommon().getId() +".ser");
        FileInitSaveLoad.<User>save(filePath, user);

        return user;
    }

    @Override
    public User read(String name) {
        return allRead()
                .stream()
                .filter(u -> u.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<User> allRead() {
        return FileInitSaveLoad.<User>load(directory);
    }

    @Override
    public User modify(UUID id,String name) {
        User user = allRead()
                .stream()
                .filter(u -> u.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
        if(user!=null){
            user.setName(name);
            Path filePath = directory.resolve(user.getCommon().getId()+".ser");         // 수정된 이름의 객체가 더 생길수있기에 파일명을 id.ser로 해야함
            FileInitSaveLoad.<User>save(filePath, user);        // 덮어쓰기
            return user;
        } else{
            System.out.println("해당 유저 없음");
            return null;
        }
    }

    @Override
    public void delete(UUID id) {
        User user = allRead()
                .stream()
                .filter(u -> u.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
        File file = new File("Userdata/"+user.getCommon().getId()+".ser");
        if(file.delete()){
            //System.out.println("---delete---");
        }
    }

}
