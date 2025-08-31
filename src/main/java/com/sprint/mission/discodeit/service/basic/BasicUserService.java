package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.repository.UserChannelRepositoryInterface;
import com.sprint.mission.discodeit.service.repository.UserRepositoryInterface;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private UserRepositoryInterface userRepository;
    private UserChannelRepositoryInterface userChannelRepository;

    private MessageService messageService;

    // 생성자
    public BasicUserService(UserRepositoryInterface userRepository,
                            UserChannelRepositoryInterface userChannelRepository,
                            MessageService messageService) {
        this.userRepository = userRepository;
        this.userChannelRepository = userChannelRepository;
        this.messageService = messageService;
    }

 @Override
    public User createUser(String name, String status, String email) {
        return userRepository.createUser(name, status, email);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllUsersByChannelId(UUID channelId) {
        List<UUID> userIds = userChannelRepository.findUserListOfChannelId(channelId);
        return userIds.stream().map(this::findById).toList();
    }

    @Override
    public boolean updateName(User user, String updatedName) {
        if(findById(user.getId()) == null) return false;
        //존재할 시
        userRepository.updateName(user, updatedName);
        messageService.modifyAuthorName(user.getId(), updatedName);
        return true;
    }

    @Override
    public boolean updateStatus(User user, String updatedStatus) {
        if(findById(user.getId()) == null) return false;
        userRepository.updateStatus(user, updatedStatus);
        return true;
    }

    @Override
    public boolean updateEmail(User user, String updatedEmail) {
        if(findById(user.getId()) == null) return false;
        userRepository.updateEmail(user, updatedEmail);
        return true;
    }

    @Override
    public boolean deleteUser(User user) {
        if(findById(user.getId()) == null) return false;
        // 유저 존재 시
        messageService.anonymizeAuthorName(user.getId());
        userChannelRepository.removeAllOfUser(user.getId());
        userRepository.deleteUser(user);
        return true;
    }

    @Override
    public boolean notExist(User user) {
        return userRepository.notExist(user);
    }
}
