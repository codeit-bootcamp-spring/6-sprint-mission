FROM amazoncorretto:17

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""\
    SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

# Copy only files required for dependency resolution first to leverage Docker layer caching
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

RUN chmod +x gradlew

# Copy the rest of the source code
COPY src ./src
COPY api-docs*.json ./
COPY HELP.md README.md ./

RUN ./gradlew bootJar --no-daemon

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
