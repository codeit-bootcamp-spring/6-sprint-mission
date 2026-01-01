package com.sprint.mission.discodeit.service.basic;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtClaimNames;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateRole(RoleUpdateRequest request) {
        UserDto updatedUserDto = updateRoleInternal(request);
        jwtRegistry.invalidateJwtInformationByUserId(request.userId());
        return updatedUserDto;
    }

    @Transactional
    @Override
    public UserDto updateRoleInternal(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        Role newRole = request.newRole();
        user.updateRole(newRole);

        return userMapper.toDto(user);
    }

    @Override
    public JwtDto refreshAccessToken(String refreshToken) {
        if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new JwtException(ErrorCode.JWT_TOKEN_INVALID);
        }
        JWTClaimsSet jwtClaimsSet = jwtTokenProvider.getClaims(refreshToken);
        String username = jwtClaimsSet.getSubject();
        String role = (String) jwtClaimsSet.getClaim(JwtClaimNames.role.name());

        User foundUser = userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withUsername(username));
        UserDto foundUserDto = userMapper.toDto(foundUser);

        String newAccessToken = jwtTokenProvider.createAccessToken(username, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username, role);

        JwtInformation jwtInformation = JwtInformation.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userDto(foundUserDto)
                .build();
        jwtRegistry.rotateJwtInformation(refreshToken, jwtInformation);

        return new JwtDto(foundUserDto, newAccessToken, newRefreshToken);
    }
}
