package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.user.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-statuses")
public class UserStatusController {

    private final UserStatusService userStatusService;


    public UserStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    @PostMapping
    public UserStatus createStatus(@RequestBody UserStatusCreateDto dto) {  //요청본문 json데이터를 UserStatusCreateDto객체로 변환
        return userStatusService.create(dto);  //상태생성 반환
    }

    @GetMapping("/{id}")
    public UserStatus findStatusById(@PathVariable UUID id) {  //URL경로 {id}값을 UUID타입 id로 받음
        return userStatusService.find(id);  //id조회 반환
    }

    @GetMapping
    public List<UserStatus> findAllStatuses() {
        return userStatusService.findAll();
    }

    @PutMapping("/{id}")
    public UserStatus updateStatus(@PathVariable UUID id, @RequestBody UserStatusUpdateDto dto) {  //URL의 ID, 본문의 데이터를 받아 상태를 업데이트
        return userStatusService.update(id, dto);
    }

    @PutMapping("/user/{userId}")
    public UserStatus updateStatusByUserId(@PathVariable UUID userId, @RequestBody UserStatusUpdateDto dto) {
        return userStatusService.updateByUserId(userId, dto);
    }

    @GetMapping("/user/{userId}/is-online")
    public boolean isUserOnline(@PathVariable UUID userId, @RequestParam long minutesToConsiderOnline) {
        return userStatusService.isOnlineByUserId(userId, minutesToConsiderOnline);
    }

    @DeleteMapping("/{id}")
    public void deleteStatus(@PathVariable UUID id) {
        userStatusService.delete(id);
    }
}