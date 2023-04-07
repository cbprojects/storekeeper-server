FROM tomcat
USER local

EXPOSE 8080
COPY ./run.sh run.sh
COPY target/Storekeeper.war /usr/local/tomcat/webapps
CMD ["catalina.sh", "run"]
RUN ls