package com.sprint.mission.discodeit.service;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.Auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.Role;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final DiscodeitUserDetailsService userDetailsService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        return updateRoleInternal(request);
    }
    @Transactional
    public UserDto updateRoleInternal(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Role newRole = request.newRole();
        user.updateRole(newRole);

        sessionManager.invalidateSessionsByUserId(userId);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public JwtInformation refreshToken(String refreshToken) {

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken) ||
                !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String userName = jwtTokenProvider.getClaims(refreshToken).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        if(!(userDetails instanceof DiscodeitUserDetails discodeitUserDetails)){
            throw new AuthException(ErrorCode.INVALID_USER_DETAILS);
        }
        try{
            String newAccess = jwtTokenProvider.createAccessToken(discodeitUserDetails);
            String newRefresh = jwtTokenProvider.createRefreshToken(discodeitUserDetails);

            UserDto userDto = discodeitUserDetails.getUserDto();

            JwtInformation newInfo = new JwtInformation(userDto, newAccess, newRefresh);
            jwtRegistry.rotateJwtInformation(refreshToken, newInfo);

            return newInfo;
        } catch (JOSEException e){
            log.error("Failed to generate new tokens for user: {}", userName, e);
            throw new AuthException(ErrorCode.INVALID_AUTH);
        }

    }


}