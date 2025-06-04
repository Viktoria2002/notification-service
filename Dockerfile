FROM gradle:7.4-jdk17 AS builder
WORKDIR /app
COPY . /app/.
RUN gradle --no-daemon build --exclude-task test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/
EXPOSE 9897
ENTRYPOINT ["java", "-jar", "/app/notification-service-0.0.1-SNAPSHOT.jar"]