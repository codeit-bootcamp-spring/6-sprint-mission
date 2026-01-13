package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.Role;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = "notification", key = "#userId", allEntries = true)
    public void create(UUID userId, String title, String content) {
        Notification notification = Notification.builder()
                .receiverId(userId)
                .title(title)
                .content(content)
                .build();
        notificationRepository.save(notification);
    }
    // 요구사항중 S3 비동기 실패 시 관리자한테 알람 전송을 해야함
    // 문제 1: 여기서 문제 관리자 한명 한테 보내야하나 아니면 관리자 전부한테 보내야하나?
    // 문제 2: 기존에 create 함수를 호출 해야하난 아니면 그냥 함수 추가 하나?
    // 위문제는 관리자가 한명이면 관리자 전용 함수 하나 만들고 관리자 db에서 관리자 아이디 들고왔어
    // create 호출하면 끝 <--- 일단 관리자한명으로 가정
    // 관리자가 다수면 create를 호출 해서 처리하면 db작업이 많아저 db서버에 부하를 주게됨
    // 그리고 관리자 전용 알람 전송 될때 마다 관리자 캐시 갱신 해야함
    @Override
    @Transactional
    public void createToAdmins(String title, String content) {
        User admins = userRepository.findAllByRole(Role.ADMIN)
                .orElseThrow(UserNotFoundException::new);
        create(admins.getId(),title,content);
    }

    @Override
    @Cacheable(value = "notification", key = "#userId" )
    @Transactional(readOnly = true)
    public List<NotificationDto> list(UUID userId) {
        return notificationRepository.findAllByReceiverId(userId);
    }

    @Override
    @CacheEvict(value = "notification", key = "#userId", allEntries = true)
    @Transactional
    public void delete(UUID notificationId,UUID userId) {
        notificationRepository.deleteById(notificationId);
    }
}
