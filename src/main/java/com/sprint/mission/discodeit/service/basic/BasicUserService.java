package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
<<<<<<< HEAD
import com.sprint.mission.discodeit.DTO.User.CreateUserDTO;
import com.sprint.mission.discodeit.DTO.User.FindUserDTO;
import com.sprint.mission.discodeit.DTO.User.UpdateUserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService{
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(CreateUserDTO createUserDTO){
        List<User> users = userRepository.findAll();
        boolean nameIsDuplicate = users.stream()
                .anyMatch(user -> user.getUsername().equals(createUserDTO.userName()));

        if(nameIsDuplicate){
            throw new DuplicateRequestException("Username already exists");
        }

        boolean emailIsDuplicate = users.stream()
                .anyMatch(user -> user.getEmail().equals(createUserDTO.userName()));

        if(emailIsDuplicate){
            throw new DuplicateRequestException("Email already exists");
        }

        User user = userRepository.save(new User(createUserDTO));
        userStatusRepository.save(new UserStatus(user.getId()));
        return user;
    }

    @Override
    public FindUserDTO find(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()->new NullPointerException("User not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(()->new NullPointerException("UserStatus not found"));
        return new FindUserDTO(user,userStatus);
    }

    @Override
    public FindUserDTO findEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException("User not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(()->new NullPointerException("UserStatus not found"));
        return new FindUserDTO(user,userStatus);
    }

    @Override
    public List<FindUserDTO> findAll() {
        List<User> users = userRepository.findAll();
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        Map<UUID, UserStatus> userStatusMap = userStatuses.stream()
                .collect(Collectors.toMap(UserStatus::getUserId, userStatus->userStatus));

        return users.stream()
                .map(user -> new FindUserDTO(user,userStatusMap.get(user.getId())))
                .toList();
    }

    @Override
    public void update(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(updateUserDTO.id()).orElseThrow(()-> new NullPointerException("user not found"));
        user.update(updateUserDTO);
        userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findByUserId(id).orElseThrow(()-> new NullPointerException("UserStatus not found"));
        User user = userRepository.findById(id).orElseThrow(()-> new NullPointerException("User not found"));

        binaryContentRepository.deleteById(user.getProfileId());
        userStatusRepository.deleteById(userStatus.getId());
        userRepository.deleteById(id);
    }
=======
=======
>>>>>>> 박지훈
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class BasicUserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(String username, int age, String email){
        User user =new User(username,age,email);
        userRepository.createUser(user);
        return user;
    }
<<<<<<< HEAD
>>>>>>> 박지훈
=======
=======
import com.sprint.mission.discodeit.DTO.User.CreateUserDTO;
import com.sprint.mission.discodeit.DTO.User.FindUserDTO;
import com.sprint.mission.discodeit.DTO.User.UpdateUserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService{
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(CreateUserDTO createUserDTO){
        List<User> users = userRepository.findAll();
        boolean nameIsDuplicate = users.stream()
                .anyMatch(user -> user.getUsername().equals(createUserDTO.userName()));

        if(nameIsDuplicate){
            throw new DuplicateRequestException("Username already exists");
        }

        boolean emailIsDuplicate = users.stream()
                .anyMatch(user -> user.getEmail().equals(createUserDTO.userName()));

        if(emailIsDuplicate){
            throw new DuplicateRequestException("Email already exists");
        }

        User user = userRepository.save(new User(createUserDTO));
        userStatusRepository.save(new UserStatus(user.getId()));
        return user;
    }

    @Override
    public FindUserDTO find(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()->new NullPointerException("User not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(()->new NullPointerException("UserStatus not found"));
        return new FindUserDTO(user,userStatus);
    }

    @Override
    public FindUserDTO findEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException("User not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(()->new NullPointerException("UserStatus not found"));
        return new FindUserDTO(user,userStatus);
    }

    @Override
    public List<FindUserDTO> findAll() {
        List<User> users = userRepository.findAll();
        List<UserStatus> userStatuses = userStatusRepository.findAll();
        Map<UUID, UserStatus> userStatusMap = userStatuses.stream()
                .collect(Collectors.toMap(UserStatus::getUserId, userStatus->userStatus));

        return users.stream()
                .map(user -> new FindUserDTO(user,userStatusMap.get(user.getId())))
                .toList();
    }

    @Override
    public void update(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(updateUserDTO.id()).orElseThrow(()-> new NullPointerException("user not found"));
        user.update(updateUserDTO);
        userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findByUserId(id).orElseThrow(()-> new NullPointerException("UserStatus not found"));
        User user = userRepository.findById(id).orElseThrow(()-> new NullPointerException("User not found"));

        binaryContentRepository.deleteById(user.getProfileId());
        userStatusRepository.deleteById(userStatus.getId());
        userRepository.deleteById(id);
    }
>>>>>>> ff6aee37135da2c11de96095adcd9502ced596ab
>>>>>>> 박지훈
}
