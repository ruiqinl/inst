FROM openjdk:8-jdk-alpine

COPY build/libs/*.jar /jars/app.jar

ENTRYPOINT java -jar /jars/app.jar

EXPOSE 80