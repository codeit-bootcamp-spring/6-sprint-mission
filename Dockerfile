# --- 1. BUILD STAGE: 애플리케이션 빌드 환경 ---
FROM amazoncorretto:17 AS build

WORKDIR /app
#프로젝트 파일을 컨테어너에 복사
COPY . .

#gradle wrapper에 실행 권한을 부여
RUN chmod +x ./gradlew

# gradle 빌드를 실행하여 실행 가능한 JAR 파일을 생성
RUN ./gradlew bootJar


# --- 2. RUNTIME STAGE: 애플리케이션 실행 환경  ---
FROM amazoncorretto:17

# 프로젝트 정보를 환경 변수로 설정 (실행할 JAR 파일명 추론에 활용)
ENV PROJECT_NAME="discodeit"
ENV PROJECT_VERSION="1.2-M8"

# JVM 옵션 환경 변수 설정 (기본값은 빈 문자열로 정의)
ENV JVM_OPTS=""

# JAR 파일을 build 스테이지에서 가져와서 최종 이미지의 루트 경로(/app.jar)에 복사
COPY --from=build /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

ENTRYPOINT java $JVM_OPTS -jar /app.jar