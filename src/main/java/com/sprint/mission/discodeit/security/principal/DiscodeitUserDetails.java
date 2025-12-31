package com.sprint.mission.discodeit.security.principal;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.enums.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "userId")
public class DiscodeitUserDetails implements UserDetails {

    private final UUID userId; // @EqualsAndHashCode 사용을 위해 중복되지만 필드로 사용.
    private final UserResponseDto userResponseDto;
    private final String password;

    @Override // 계정 잠금 여부
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 잠금 여부
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 비밀번호 만료 여부
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 활성화 여부
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleWithPrefix = "ROLE_" + userResponseDto.role().name();
        return List.of(new SimpleGrantedAuthority(roleWithPrefix));
    }

    @Override
    public String getUsername() {
        return userResponseDto.username();
    }
}
