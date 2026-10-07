FROM eclipse-temurin:8-jdk-alpine

WORKDIR /apps
COPY target/stockquote-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]