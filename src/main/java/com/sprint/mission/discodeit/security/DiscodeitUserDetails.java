package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.model.UserDto;
import com.sprint.mission.discodeit.common.Role;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@EqualsAndHashCode(of = "id")
public class DiscodeitUserDetails implements UserDetails {

  private final UUID id;

  private final UserDto userDto;

  private final String password;

  private final Role role;

  public DiscodeitUserDetails(UserDto userDto, String password, Role role) {
    this.id = userDto.id();
    this.userDto = userDto;
    this.password = password;
    this.role = role;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return userDto.username();
  }
}
