package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable UUID id, @RequestParam(required = false) UserStatusDto status) {
        return userService.find(id, status);
    }

    @GetMapping
    public List<User> findAllUsers(@RequestParam(required = false) UserStatusDto status) {
        if (status != null) {  //선택적 파라미터(isOnline)가 존재하면
            return userService.findAll(status);  //해당 상태조회
        }
        return userService.findAll();  //선택적 파라미터가 없으면 전체조회
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable UUID id,
                           @RequestParam String newUsername,
                           @RequestParam String newEmail,
                           @RequestParam String newPassword) {
        return userService.update(id, newUsername, newEmail, newPassword);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }
}
