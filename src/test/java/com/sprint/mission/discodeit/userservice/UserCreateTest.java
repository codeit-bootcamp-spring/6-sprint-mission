package com.sprint.mission.discodeit.userservice;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.user.UserDuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.UserDuplicateNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCreateTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

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
    void createUser_success() {
        // given (준비)
        //요청값 생성
        UserCreateRequest request = UserCreateRequest.builder()
                .username("테스트")
                .email("test@test.com")
                .password("000000")
                .build();

        //기대값 유저Dto에 있는 바이트파일Dto 생성
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        //기대값 유저Dto 생성
        UUID userId = UUID.randomUUID();
        String username = "test";
        String email = "test@test.com";
        UserDto userDto = UserFixture.createUserDto(
                userId,
                username,
                email,
                binaryContentDto,
                false
                );

        //임시 multipartFile 생성
        MultipartFile multipartFile = new MockMultipartFile(
                "테스트파일",
                "파일.txt",
                "txt",
                new byte[300]
        );



        //증복 검증 반횐값 설정
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        //유저 멥퍼 반횐값 설정
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        //when
        UserDto response = basicUserService.create(multipartFile, request);

        //then
        //값 검증
        assertThat(response).isEqualTo(userDto);
        assertThat(response.profile()).isEqualTo(binaryContentDto);

        //행위 검증
        verify(userRepository, times(1)).existsByUsername(request.username());
        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(binaryContentStorage, times(1)).put(isNull(), any(byte[].class));
        verify(binaryContentRepository, times(1)).save(any(BinaryContent.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userStatusRepository, times(1)).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("중복 실패 검증")
    void createUser_fail() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("테스트")
                .email("test@test.com")
                .password("000000")
                .build();

        //기대값 유저Dto에 있는 바이트파일Dto 생성
        BinaryContentDto binaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName("파일.txt")
                .size(300L)
                .contentType("txt")
                .build();

        //기대값 유저Dto 생성
        UUID userId = UUID.randomUUID();
        String username = "test";
        String email = "test@test.com";
        UserDto userDto = UserFixture.createUserDto(
                userId,
                username,
                email,
                binaryContentDto,
                false
        );

        //임시 multipartFile 생성
        MultipartFile multipartFile = new MockMultipartFile(
                "테스트파일",
                "파일.txt",
                "txt",
                new byte[300]
        );
        //증복 검증 반횐값 설정
        //이메일 중복 false -> true
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        //이름 중복 false -> true
        //when(userRepository.existsByUsername(anyString())).thenReturn(false);


        //when
        DiscodeitException exception = assertThrows(UserDuplicateEmailException.class, () -> {
            basicUserService.create(multipartFile, request);
        });

        /*
        DiscodeitException exception = assertThrows(UserDuplicateNameException.class, () -> {
            basicUserService.create(multipartFile, request);
        });
        */

        //이메일 중복
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DUPLICATE_EMAIL);
        //이름 중복
        //assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DUPLICATE_NAVE);

    }
}
