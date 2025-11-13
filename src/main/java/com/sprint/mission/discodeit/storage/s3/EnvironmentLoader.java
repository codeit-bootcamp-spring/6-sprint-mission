package com.sprint.mission.discodeit.storage.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EnvironmentLoader {

    private static final String DEFAULT_ENV_PATH = "./.env";

    public static Properties loadAwsProperties() {
        Properties properties = new Properties();

        // 환경 변수 또는 시스템 속성으로 경로 지정 가능
        String envFilePath = System.getProperty("env.path", DEFAULT_ENV_PATH);
        Path envPath = Path.of(envFilePath);

        if (!Files.exists(envPath)) {
            throw new RuntimeException("'.env' 파일이 존재하지 않습니다: " + envFilePath);
        }

        try (FileInputStream input = new FileInputStream(envFilePath)) {
            properties.load(input);
            System.out.println("[환경 로딩] AWS 환경변수가 성공적으로 로드되었습니다: " + envFilePath);
        } catch (IOException ex) {
            System.err.println("[환경 로딩 오류] .env 파일을 읽는 중 문제가 발생했습니다.");
            System.err.println("파일 경로: " + envFilePath);
            System.err.println("오류 상세: " + ex.getMessage());
            throw new RuntimeException("환경변수 로딩 실패: .env 파일을 확인해주세요.", ex);
        }

        return properties;
    }
}