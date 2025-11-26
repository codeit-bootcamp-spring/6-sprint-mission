FROM gradle:8.14.3-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle
RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew bootjar

FROM amazoncorretto:17

ENV PROJECT_NAME="discodeit"
ENV PROJECT_VERSION="1.2-M8"
ENV JVM_OPTS=""

CMD ["echo","$PROJECT_NAME-$PROJECT_VERSION"]

WORKDIR /app


EXPOSE 80
EXPOSE 8081
COPY --from=builder /app/build/libs/$PROJECT_NAME-$PROJECT_VERSION.jar app.jar

ENTRYPOINT ["java", "-jar" , "app.jar"]
