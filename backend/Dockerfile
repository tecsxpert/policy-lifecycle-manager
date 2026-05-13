FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar app.jar

RUN mkdir -p /app/uploads/files

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]