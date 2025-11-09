package com.sprint.mission.discodeit.config;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request , ServletResponse response, FilterChain chain)throws IOException, ServletException {
        try {
            MDC.put("requestId", UUID.randomUUID().toString());
            chain.doFilter(request,response);
        }finally {
            MDC.clear();
        }
    }
}
