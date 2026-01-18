# ----- 0. Global Build Arguments -----
ARG PROJECT_NAME="discodeit"
ARG PROJECT_VERSION="2.2-M11"

# ----- 1. Build Stage -----
# Java 17과 Gradle을 포함한 이미지를 'builder'라는 별칭으로 사용
FROM eclipse-temurin:17-jdk AS builder

# 프로젝트 정보 환경 변수 설정
ENV JVM_OPTS=""

# 작업 디렉토리를 설정
WORKDIR /app

# 빌드 컨텍스트(현재 디렉터리)를 이미지 내부 /app으로 복사
COPY . .

# Gradle Wrapper를 실행 가능하게 하고, bootJar 태스크로 빌드 수행
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

# ----- 2. Run Stage -----
FROM amazoncorretto:17
WORKDIR /app

# 최상단 ARG의 값을 이 스테이지로 가져옴.
ARG PROJECT_NAME
ARG PROJECT_VERSION

# 실행 환경 변수 재정의
ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}

# 빌드 환경에서 생성된 JAR 파일 이름을 환경 변수를 사용하여 추론하고 복사
ARG JAR_FILE=build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar
COPY --from=builder /app/build/libs/${PROJECT_NAME}-*.jar /app.jar

# 애플리케이션 실행 포트
EXPOSE 80

# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java"]
CMD ["-Xmx384m", "-jar", "/app.jar"]