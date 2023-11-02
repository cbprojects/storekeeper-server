FROM adoptopenjdk/openjdk11
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ARG JAR_FILE=Storekeeper.jar
ADD Storekeeper.jar /usr/app/Storekeeper.jar

ENV CONTEXT_PATH=/

ENTRYPOINT ["java", "-jar", "/usr/app/Storekeeper.jar", "--server.servlet.context-path=${CONTEXT_PATH}"]
