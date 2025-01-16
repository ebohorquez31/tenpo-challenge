FROM eclipse-temurin:21-jdk-alpine
RUN mkdir /app
COPY applications/app-service/build/libs/*.jar /app/app-service.jar
ENTRYPOINT ["java","-jar","/app/app-service.jar"] 