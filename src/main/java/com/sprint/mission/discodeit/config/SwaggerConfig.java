package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Sprint_Mission_5 API 문서")
            .version("2.6.0")
            .description("Sprint_Mission_5 의 Swagger API 문서입니다.")
        )
        .servers(
            Arrays.asList(
                new Server().url("http://localhost:8080").description("로컬 서버")
            )
        )
        .tags(
            Arrays.asList(
                new Tag().name("Channel").description("Channel API"),
                new Tag().name("ReadStatus").description("Message 읽음 상태 API"),
                new Tag().name("Message").description("Message API"),
                new Tag().name("User").description("User API"),
                new Tag().name("UserStatus").description("UserStatus API"),
                new Tag().name("BinaryContent").description("첨부 파일 API"),
                new Tag().name("Auth").description("인증 API")
            )
        );
  }
}