package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * S3 클라이언트 생성과 관련된 설정
 */
@Configuration
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class S3Config {

    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    // S3Client가 생성된 후에 자동으로 생성됨
    @Bean
    public S3Presigner s3Presigner() {
        // Presigner도 독립적으로 생성하는 것이 좋습니다.
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
}