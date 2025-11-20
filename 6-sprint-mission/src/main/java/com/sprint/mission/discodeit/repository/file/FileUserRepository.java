package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {

    Path directory = Paths.get(System.getProperty("user.dir"), "UserData");

    @Override
    public User find(UUID id) {
        return findall()
                .stream()
                .filter(u->u.getCommon().getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public User save(User user) {
        FileInitSaveLoad.init(directory);

        Path filePath = directory.resolve(user.getCommon().getId() +".ser");
        FileInitSaveLoad.<User>save(filePath, user);

        return user;
    }

    @Override
    public List<User> findall() {
        return FileInitSaveLoad.<User>load(directory);
    }

    @Override
    public void delete(UUID id) {
        User user = findall()
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
