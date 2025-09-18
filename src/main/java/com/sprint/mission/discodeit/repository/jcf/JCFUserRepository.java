package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
@Repository
public class JCFUserRepository implements UserRepository {
    private final List<User> userDate;

    public  JCFUserRepository() {
        userDate = new ArrayList<User>();
    }

    @Override
    public User save(User user) {
        int idx = userDate.indexOf(user);
        if (idx >=0) {
            userDate.set(idx, user);
        }else {
            userDate.add(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userDate.stream()
                .filter(user-> user.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<User> findByEmail(String userEmail) {
        return userDate.stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(userDate);
    }

    @Override
    public boolean existsById(UUID id) {
        return userDate.stream()
                .anyMatch(user -> user.getId().equals(id));
    }

    public void deleteById(UUID id) {
        userDate.removeIf(user -> user.getId().equals(id));
=======
=======
>>>>>>> 박지훈

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final List<User> userDeat;

    public  JCFUserRepository() {
        userDeat = new ArrayList<User>();
    }

    public void createUser(User user) {
        userDeat.add(user);
    }

    public User findUserById(UUID id) {
        return userDeat.stream()
                .filter(user-> user.getUserId().equals(id))
                .findAny()
                .orElse(null);
    }

    public User findUserByEmail(String userEmail) {
        return userDeat.stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .findAny()
                .orElse(null);
    }

    public List<User> findAllUsers() {
        return userDeat;
    }

    public void updateUser(User user) {
        int idx = userDeat.indexOf(user);
        if (idx == -1) {
            throw new NullPointerException("해당 유저 없음");
        }
        userDeat.set(idx, user);
    }

    public void deleteUser(UUID id) {
        userDeat.remove(findUserById(id));
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
@Repository
public class JCFUserRepository implements UserRepository {
    private final List<User> userDate;

    public  JCFUserRepository() {
        userDate = new ArrayList<User>();
    }

    @Override
    public User save(User user) {
        int idx = userDate.indexOf(user);
        if (idx >=0) {
            userDate.set(idx, user);
        }else {
            userDate.add(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userDate.stream()
                .filter(user-> user.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<User> findByEmail(String userEmail) {
        return userDate.stream()
                .filter(user -> user.getEmail().equals(userEmail))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(userDate);
    }

    @Override
    public boolean existsById(UUID id) {
        return userDate.stream()
                .anyMatch(user -> user.getId().equals(id));
    }

    public void deleteById(UUID id) {
        userDate.removeIf(user -> user.getId().equals(id));
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
    }
}
