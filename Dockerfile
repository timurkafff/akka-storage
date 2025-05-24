FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

WORKDIR /app
COPY . .

RUN mkdir -p /app/src/main/resources
COPY src/main/resources/logback.xml /app/src/main/resources/

EXPOSE 8080

# Запуск через sbt
CMD ["sbt", "run"] 