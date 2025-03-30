FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8008
ENTRYPOINT ["java","-jar","app.jar"] 