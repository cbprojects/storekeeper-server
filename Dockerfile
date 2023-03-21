FROM adoptopenjdk/openjdk11
ARG JAR_FILE=target/*.jar
ENV CONTEXT_PATH=/
EXPOSE 8080

COPY ./run.sh run.sh
COPY ${JAR_FILE} storekeeper.jar
RUN chmod +x ./run.sh
ENTRYPOINT ["./run.sh"]
