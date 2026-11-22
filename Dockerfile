FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B verify

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S app && adduser -S app -G app && apk add --no-cache curl
WORKDIR /app
COPY --from=build --chown=app:app /build/target/storekeeper-server.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
