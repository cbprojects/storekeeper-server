FROM adoptopenjdk/openjdk11
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ARG JAR_FILE=Storekeeper.jar
ADD target/${JAR_FILE} $HOME/${JAR_FILE}

ENV CONTEXT_PATH=/

ENTRYPOINT ["java", "-jar", "$HOME/${JAR_FILE}", "--server.servlet.context-path=${CONTEXT_PATH}"]
