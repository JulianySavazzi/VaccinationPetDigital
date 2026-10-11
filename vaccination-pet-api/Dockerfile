# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy AS workspace

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline

COPY src/ src/


FROM workspace AS test

CMD ["./mvnw", "test"]


FROM workspace AS development

CMD ["./mvnw", "spring-boot:run"]


FROM workspace AS build

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --no-create-home spring

COPY --from=build \
    --chown=spring:spring \
    /workspace/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
