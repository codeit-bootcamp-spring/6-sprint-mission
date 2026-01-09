package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public class MdcAndSecurityContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

        // 현재 스레드의 SecurityContext 캡처
        SecurityContext originalContext = SecurityContextHolder.getContext();

        // 깊은 복사
        SecurityContext contextCopy = createContextCopy(originalContext);
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        return () -> {
            SecurityContext previousContext = SecurityContextHolder.getContext();
            try {
                SecurityContextHolder.setContext(contextCopy);
                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }
                runnable.run();

            } finally {
                MDC.clear();
                SecurityContextHolder.setContext(previousContext);
            }
        };

    }

    private SecurityContext createContextCopy(SecurityContext original) {

        if (original == null || original.getAuthentication() == null) {
            return SecurityContextHolder.createEmptyContext();
        }

        SecurityContext copy = SecurityContextHolder.createEmptyContext();
        copy.setAuthentication(original.getAuthentication());

        return copy;
    }
}
