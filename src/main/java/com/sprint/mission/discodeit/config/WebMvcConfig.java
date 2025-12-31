package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 설정 클래스
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public MDCLoggingInterceptor mdcLoggingInterceptor() {
        return new MDCLoggingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mdcLoggingInterceptor())
                .addPathPatterns("/**"); // 모든 경로에 적용
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // "/" 경로로 들어오는 모든 요청을 "index.html"로 포워딩합니다.
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}