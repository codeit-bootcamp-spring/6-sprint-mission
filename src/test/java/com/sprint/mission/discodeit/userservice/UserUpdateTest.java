package com.sprint.mission.discodeit.userservice;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserDuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.UserDuplicateNameException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.support.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserUpdateTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicUserService basicUserService;

    @Test
    @DisplayName("성공 검증")
    void updateUser_success() {
        //given
        //기존 업데이트 전 유저 설정
        BinaryContent binaryContent = new BinaryContent("테스트.txt",300L,"txt");

        User user = UserFixture.createUser(binaryContent);

        //업데이트 요청 값 설정
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("테스트1")
                .newEmail("test1@test.com")
                .newPassword("111111")
                .build();

        //multipartFile 설정
        MultipartFile multipartFile = new MockMultipartFile(
                "테스트파일1",
                "파일1.txt",
                "txt",
                new byte[300]
        );

        //업데이트로 값 변경된 유저Dto값 설정
        //유저Dto에 있는 바이트파일Dto값 설정
        BinaryContentDto updateBinaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일1.txt")
                .size(310L)
                .contentType("txt")
                .build();

        //유저Dto 설정
        UUID userId = UUID.randomUUID();
        String username = "테스트1";
        String email = "test1@test.com";
        UserDto updateUserDto = UserFixture.createUserDto(
                userId,
                username,
                email,
                updateBinaryContentDto,
                true
        );

        //유저레포 반횐값 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //증복 검증 반횐값 설정
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        //유저 멥퍼 반횐값 설정
        when(userMapper.toDto(any(User.class))).thenReturn(updateUserDto);

        //when
        UserDto updateResponse = basicUserService.update(multipartFile,userId,request);

        //then
        //값 검증
        assertThat(updateResponse).isEqualTo(updateUserDto);
        assertThat(updateResponse.profile()).isEqualTo(updateBinaryContentDto);

        //행동 검증
        verify(userRepository,times(1)).findById(any(UUID.class));
        verify(userRepository,times(1)).existsByUsername(any(String.class));
        verify(userRepository,times(1)).existsByEmail(any(String.class));
        verify(userRepository,times(1)).save(any(User.class));
        verify(userMapper,times(1)).toDto(any(User.class));

        verify(binaryContentStorage,times(1)).put(isNull(), any(byte[].class));
        verify(binaryContentRepository,times(1)).save(any(BinaryContent.class));


    }

    @Test
    @DisplayName("실패 검증")
    void updateUser_fail() {
        //given
        User user = UserFixture.createUser(null);

        //업데이트 요청 값 설정
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("테스트1")
                .newEmail("test1@test.com")
                .newPassword("111111")
                .build();

        //multipartFile 설정
        MultipartFile multipartFile = new MockMultipartFile(
                "테스트파일1",
                "파일1.txt",
                "txt",
                new byte[300]
        );

        //증복 검증 반횐값 설정
        //이메일 중복 false -> true
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        //이름 중복 false -> true
        //when(userRepository.existsByUsername(anyString())).thenReturn(true);

        DiscodeitException exception = assertThrows(UserDuplicateEmailException.class, () -> {
            basicUserService.update(multipartFile,user.getId(),request);
        });
        /*
        DiscodeitException exception = assertThrows(UserDuplicateNameException.class, () -> {
            basicUserService.update(multipartFile,user.getId(),request);
        });
        */

        //이메일 중복
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DUPLICATE_EMAIL);
        //이름 중복
        //assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DUPLICATE_NAVE);
    }
}
