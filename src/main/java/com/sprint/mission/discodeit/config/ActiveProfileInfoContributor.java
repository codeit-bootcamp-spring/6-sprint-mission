package com.sprint.mission.discodeit.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActiveProfileInfoContributor implements InfoContributor {

    private final Environment environment;

    public ActiveProfileInfoContributor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> map = new HashMap<>();
        map.put("application", environment.getProperty("info.application.name"));
        map.put("application-version", environment.getProperty("info.application.java"));
        map.put("springboot-version", environment.getProperty("info.application.springboot"));
        map.put("jpa", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        map.put("spring.datasource.url", new String[]{
                environment.getProperty("spring.datasource.driver-class-name"),
                environment.getProperty("spring.datasource.url"),
        });
        map.put("storage", environment.getProperty("discodeit.repository"));
        map.put("multipart", new String[]{environment.getProperty("spring.servlet.multipart.file-size-threshold"),
                environment.getProperty("spring.servlet.multipart.max-request-size")});

        builder.withDetails(map);


    }
}
