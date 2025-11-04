package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final UserMapper userMapper;
  private final UserStatusMapper userStatusMapper;

  @GetMapping
  public ResponseEntity<List<UserDto>> readUser() {
    List<User> userList = userService.findAll();
    return ResponseEntity.ok(userMapper.toDtoList(userList));

  }

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserDto> createUser(
      // @RequestPart의 경우 js 파일에서 맞는 변수명을 찾아 넣어야함
      // 멀티파일과 dto를 함께 쓸 경우 @RequestPart 사용, postman 테스트할때는 form-data로  { "request" : { "username" : "test" , ... } }
      @Valid @RequestPart("userCreateRequest") CreateUserRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    User user = userService.create(request, Optional.ofNullable(profile));
    return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
  }

  @PatchMapping(value = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
      MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UpdateUserRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    User user = userService.update(userId, request,
        Optional.ofNullable(profile));
    return ResponseEntity.ok(userMapper.toDto(user));
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteUser(
      @PathVariable UUID userId
  ) {
    userService.delete(userId);
    return ResponseEntity.ok(userId + " 삭제되었습니다");
  }

  @PatchMapping(value = "/{userId}/userStatus", consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserStatusDto> stateUser(
      @PathVariable UUID userId,
      @Valid @RequestBody UpdateUserStatusRequest request
  ) {
    UserStatus updated = userStatusService.update(userId, request);
    return ResponseEntity.ok(userStatusMapper.toDto(updated));
  }
}
