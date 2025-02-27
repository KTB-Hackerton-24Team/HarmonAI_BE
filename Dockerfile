# Gradle 빌드를 위한 JDK 21 이미지 사용
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle bootJar

# 런타임을 위한 JDK 21 이미지
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
