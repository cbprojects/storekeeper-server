FROM adoptopenjdk/openjdk11
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME

ARG JAR_FILE=Storekeeper.jar
ENV CONTEXT_PATH=/

COPY $HOME/${JAR_FILE} /app/${JAR_FILE}
ENTRYPOINT ["java", "-jar", "/app/Storekeeper.jar", "--server.servlet.context-path=${CONTEXT_PATH}"]