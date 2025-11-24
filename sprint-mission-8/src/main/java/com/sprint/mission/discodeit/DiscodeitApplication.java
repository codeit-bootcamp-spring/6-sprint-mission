package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication {


//  public static void main(String[] args) {
//    try {
//      AWSS3Test tester = new AWSS3Test();
//
//      // 1. 업로드용 임시 파일 하나 만들기
//      Path tempFile = Files.createTempFile("s3-test-", ".txt");
//      Files.writeString(tempFile, "Hello S3! This is test content.");
//
//      String s3Key = "test-folder/s3-test-file.txt";  // 버킷 안에서의 경로
//
//      // 업로드
//      tester.uploadTest(tempFile, s3Key);
//
//      // 다운로드 (현재 프로젝트 폴더 아래 download-test.txt 로)
//      Path downloadPath = Path.of("download-test.txt");
//      tester.downloadTest(s3Key, downloadPath);
//
//      // Presigned URL (5분짜리)
//      tester.presignedUrlTest(s3Key, Duration.ofMinutes(5));
//
//      System.out.println("모든 테스트 완료 🎉");
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//  }

  public static void main(String[] args) {
    SpringApplication.run(DiscodeitApplication.class, args);
  }
}
