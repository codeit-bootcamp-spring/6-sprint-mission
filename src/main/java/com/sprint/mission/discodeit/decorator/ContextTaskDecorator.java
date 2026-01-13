package com.sprint.mission.discodeit.decorator;

//import org.slf4j.MDC;
//import org.springframework.core.task.TaskDecorator;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component("defaultDecorator")
//public class ContextTaskDecorator implements TaskDecorator {
//    @Override
//    public Runnable decorate(Runnable runnable) {
//        Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        return () -> {
//            try {
//                if (mdcContextMap != null) {
//                    MDC.setContextMap(mdcContextMap);
//                }
//
//                if (authentication != null) {
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//
//                runnable.run();
//            } finally {
//                MDC.clear();
//                SecurityContextHolder.clearContext();
//            }
//        };
//    }
//}
