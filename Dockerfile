FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
# Toma el .jar generado por Maven en la carpeta target
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]