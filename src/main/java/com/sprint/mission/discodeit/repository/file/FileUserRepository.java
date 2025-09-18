package com.sprint.mission.discodeit.repository.file;

<<<<<<< HEAD
<<<<<<< HEAD
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
=======
=======
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
public class FileUserRepository implements UserRepository {
    private static final Path directory = Paths.get("./src/main/resources/UserDate");
    private static final FileEdit instance = new  FileEdit();

<<<<<<< HEAD
<<<<<<< HEAD
    private Path filePaths(UUID id) {
        return directory.resolve( id + ".ser");
=======
    private Path filePaths(User user) {
        return directory.resolve(user.getUserId().toString() + ".ser");
>>>>>>> 박지훈
=======
    private Path filePaths(User user) {
        return directory.resolve(user.getUserId().toString() + ".ser");
=======
    private Path filePaths(UUID id) {
        return directory.resolve( id + ".ser");
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    }

    public FileUserRepository() {
        instance.init(directory);
    }

<<<<<<< HEAD
<<<<<<< HEAD
    @Override
    public User save(User user) {
        instance.save(directory,user.getId(),user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {return instance.load(directory,id);}

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> user = instance.loadAll(directory);
        return user.stream()
                .filter(user1 -> user1.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return instance.loadAll(directory);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        boolean isDelete = instance.delete(directory,id);
        if(!isDelete){
            throw new NullPointerException(" 유저 삭제 실패");
=======
=======
>>>>>>> 박지훈
    public void createUser(User user) {
        instance.save(filePaths(user),user);
    }

    public User findUserById(UUID id) {

        List<User> userList = instance.load(directory);
        return userList.stream()
                .filter(user -> user.getUserId().equals(id))
                .findAny()
                .orElse(null);
    }

    public User findUserByEmail(String userEmail) {
        List<User> userList = instance.load(directory);
        return userList.stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .findAny()
                .orElse(null);

    }

    public List<User> findAllUsers() {
        return instance.load(directory);
    }

    public void updateUser(User user) {
        instance.save(filePaths(user),user);
    }

    public void deleteUser(UUID id) {
        User user = findUserById(id);
        boolean isDelete = instance.delete(filePaths(user));
        if(!isDelete){
            throw new NullPointerException(user.getEmail()+" 유저 삭제 실패");
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
    @Override
    public User save(User user) {
        instance.save(directory,user.getId(),user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {return instance.load(directory,id);}

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> user = instance.loadAll(directory);
        return user.stream()
                .filter(user1 -> user1.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return instance.loadAll(directory);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        boolean isDelete = instance.delete(directory,id);
        if(!isDelete){
            throw new NullPointerException(" 유저 삭제 실패");
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
        }
    }
}
