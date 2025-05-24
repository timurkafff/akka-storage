FROM eclipse-temurin:21

WORKDIR /app
COPY target/scala-3.3.4/akka-storage-assembly-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]