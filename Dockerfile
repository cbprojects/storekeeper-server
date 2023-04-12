FROM maven as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn -U clean install

FROM adoptopenjdk/openjdk11
ARG JAR_FILE=target/*.jar
ENV CONTEXT_PATH=/

COPY --from=build /usr/app/${JAR_FILE} /app/Storekeeper.jar
ENTRYPOINT ["java", "-jar", "/app/Storekeeper.jar", "--server.servlet.context-path=${CONTEXT_PATH}"]