FROM openjdk:11-jdk-oracle
EXPOSE 8888
VOLUME /tmp
COPY target/coder-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
# docker build -t ideaworks/coder-backend:v1.0 .
