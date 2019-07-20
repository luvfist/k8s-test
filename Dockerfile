FROM openjdk:8-slim
EXPOSE 8080
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
