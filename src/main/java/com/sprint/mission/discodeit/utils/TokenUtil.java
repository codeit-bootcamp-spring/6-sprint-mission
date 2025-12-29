package com.sprint.mission.discodeit.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

  public void setHttpOnlyCookie(String name, String value, HttpServletResponse response, int maxAge) {

    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);

  }

  public void deleteCookie(String name, HttpServletResponse response) {

    Cookie cookie = new Cookie(name, null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);

  }

}
