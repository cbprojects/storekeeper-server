FROM adoptopenjdk/openjdk11
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ARG JAR_FILE=storekeeper-server.jar
ADD storekeeper/storekeeper-server.jar /usr/app/storekeeper-server.jar

EXPOSE 8443
ENTRYPOINT ["java", "-jar", "/usr/app/storekeeper-server.jar"]