package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserStatusDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentService binaryContentService;

    @Override
    public User create(UserCreateDto userCreateDto) {
        String username = userCreateDto.username();
        String email = userCreateDto.email();
        String password = userCreateDto.password();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with username " + email+ " already exists");
        }
        User user = new User(username, email, password);
        return userRepository.save(user);
    }


    @Override
    public User find(UUID userId, UserStatusDto userStatusDto) {
        if (userStatusDto.isOnline()) {
            System.out.println("접속중");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(userId + " not found"));
    }

    @Override
    public List<User> findAll(UserStatusDto userStatusDto) {
        if (userStatusDto.isOnline()) {
            System.out.println("접속중");
        }
        return userRepository.findAll();
    }
    @Override
    public List<User> findAll(){
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("사용자 가 없습니다.");
        }
        return users;
    }


    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(userId + " not found");
        }
        userRepository.deleteById(userId);
        userStatusRepository.deleteByUserId(userId);
    }


}
