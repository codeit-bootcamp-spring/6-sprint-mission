package com.sprint.mission.discodeit.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

  public void setHttpOnlyCookie(String name, String value, HttpServletResponse response) {

    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
    response.addCookie(cookie);

  }

}
