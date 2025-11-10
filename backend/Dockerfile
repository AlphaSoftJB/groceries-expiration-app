FROM gradle:8.5.0-jdk17-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle clean bootJar -x test

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
