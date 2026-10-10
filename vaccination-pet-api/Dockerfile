# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline

COPY src/ src/

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S spring \
    && adduser -S spring -G spring

COPY --from=build \
    --chown=spring:spring \
    /workspace/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]