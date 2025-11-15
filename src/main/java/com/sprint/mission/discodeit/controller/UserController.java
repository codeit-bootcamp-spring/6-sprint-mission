package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestPart("userCreateRequest") UserCreateRequestDto request,
            @RequestPart(value = "profile", required = false) MultipartFile image
    ) {
        BinaryContentCreateRequestDto profileRequest = null;
        if (image != null && !image.isEmpty()) {
            profileRequest = resolveProfileRequest(image);
        }

        UserResponseDto user = userService.create(request, profileRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequestDto request,
            @RequestPart(value = "profile", required = false) MultipartFile image
    ) {
        BinaryContentCreateRequestDto profileRequest = null;
        if (image != null && !image.isEmpty()) {
            profileRequest = resolveProfileRequest(image);
        }
        UserResponseDto user = userService.update(id, request, profileRequest);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(user);
    }

    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusResponseDto> updateUserStatusByUserId(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(userStatusService.updateByUserId(userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    private BinaryContentCreateRequestDto resolveProfileRequest(MultipartFile profileFile) {
        try {
            return new BinaryContentCreateRequestDto(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

