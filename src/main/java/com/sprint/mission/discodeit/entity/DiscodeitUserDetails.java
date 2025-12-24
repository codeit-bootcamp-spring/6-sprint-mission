package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDTO;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDTO.User user;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return user.getId().equals(that.user.getId());
  }

  @Override
  public int hashCode() {
    return user.getId().hashCode();
  }

}
