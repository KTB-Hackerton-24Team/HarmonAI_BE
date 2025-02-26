FROM azul/zulu-openjdk:21

WORKDIR /app

COPY build/libs/HarmonAI-0.0.1-SNAPSHOT.jar HarmonAI.jar

CMD ["java", "-jar", "HarmonAI.jar"]