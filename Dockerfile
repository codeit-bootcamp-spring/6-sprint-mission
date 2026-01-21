# --- Build Stage ---
FROM gradle:8.14.3-jdk17 AS builder

WORKDIR /app
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# --- Runtime Stage ---
FROM amazoncorretto:17-alpine

# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=2.2-M11
ENV JVM_OPTS=""

# 빌드 스테이지에서 생성된 JAR 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
