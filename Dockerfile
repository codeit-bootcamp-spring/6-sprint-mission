#build stage
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew build -x test

#runtime stage
FROM amazoncorretto:17-alpine

WORKDIR /app

ENV JVM_OPTS=""
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV SERVER_PORT=80

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar /app/app.jar

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]