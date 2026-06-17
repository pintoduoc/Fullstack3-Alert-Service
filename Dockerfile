FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/alert-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]