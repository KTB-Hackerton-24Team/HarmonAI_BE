# OpenJDK 21 이미지 사용
FROM openjdk:21-slim

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 JAR 파일 복사
COPY build/libs/HarmonAI-0.0.1-SNAPSHOT.jar app.jar

# application.properties 파일 복사 (필요한 경우)
COPY src/main/resources/application.properties config/application.properties

# 환경 변수 설정 (config 폴더에 파일이 없으면 주석 처리)
ENV SPRING_CONFIG_LOCATION=file:/app/config/application.properties

# JVM 옵션 설정
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 컨테이너에서 사용할 포트
EXPOSE 8080

# 실행 명령어 (JVM 옵션 포함)
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]