package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.LoginDto;
import com.sprint.mission.discodeit.service.AuthService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginDto loginDto) {
        authService.userMatch(loginDto);
    }
}