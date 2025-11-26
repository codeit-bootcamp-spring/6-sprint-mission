FROM amazoncorretto:17

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew && \
./gradlew clean build -x test

  # 5. 프로젝트 정보 / JVM 옵션 환경 변수 설정
ENV PROJECT_NAME=discodeit \
PROJECT_VERSION=1.2-M8 \
JVM_OPTS=""

EXPOSE 80

CMD ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]