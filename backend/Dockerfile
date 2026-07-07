FROM eclipse-temurin:24-jdk-alpine AS build

ENV MAVEN_OPTS=""

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
