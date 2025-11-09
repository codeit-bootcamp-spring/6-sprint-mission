package com.sprint.mission.discodeit.userservice;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.support.BinaryContentFixture;
import com.sprint.mission.discodeit.support.UserFixture;
import com.sprint.mission.discodeit.support.UserStatusFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDeleteTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @InjectMocks
    private BasicUserService basicUserService;

    @Test
    @DisplayName("유저 삭제 성공 검증")
    void deleteUser_success() {
        //given
        UUID userId = UUID.randomUUID();
        UUID userStatusId = UUID.randomUUID();

        BinaryContent binaryContent = new BinaryContent("테스트.txt",300L,"txt");
        BinaryContentFixture.setBinaryContentId(binaryContent,UUID.randomUUID());
        User user = UserFixture.createUser(binaryContent);
        UserStatus userStatus = new UserStatus(user);
        UserFixture.setUserId(user, userId);
        UserFixture.setStatus(user, userStatus);
        UserStatusFixture.setUserStatusId(userStatus, userStatusId);

        //유저레포 반환값 설정
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        //유저레포 반환값 설정
        when(userStatusRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(userStatus));

        //when
        basicUserService.delete(userId);

        //then
        verify(userRepository,times(1)).findById(any(UUID.class));
        verify(userRepository,times(1)).deleteById(any(UUID.class));

        verify(userStatusRepository,times(1)).findByUserId(any(UUID.class));
        verify(userStatusRepository,times(1)).deleteById(any(UUID.class));

        verify(binaryContentRepository,times(1)).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("유저 상태없음 실패 검증")
    void deleteUser_fail_1() {
        UUID userId = UUID.randomUUID();

        DiscodeitException exception = assertThrows(UserStatusNotFoundException.class, () -> {
            basicUserService.delete(userId);
        });
        System.out.println(exception.getErrorCode());

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_STATUS_NOT_FOUND);

    }

    @Test
    @DisplayName("유저 없음 실패 검증2")
    void deleteUser_fail_2() {
        UUID userId = UUID.randomUUID();
        User user = UserFixture.createUser(new BinaryContent());
        UserFixture.setUserId(user, userId);
        UserStatus userStatus = new UserStatus(user);
        when(userStatusRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(userStatus));

        DiscodeitException exception = assertThrows(UserNotFoundException.class, () -> {
            basicUserService.delete(userId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

    }
}
