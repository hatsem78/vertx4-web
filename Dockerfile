# Docker example using farjar
# docker build -t example/vertx4-start .
# docker run -t -i -p 8888:8888 example/vertx4-start

# https://hub.docker.com/_/adoptopenjdk
# https://github.com/GoogleContainerTools/jib?tab=readme-ov-file
# ./gradlew jibDockerbuild
# docker run -t -i -p 8888:8888 example/vertx4-start:latest

FROM amazoncorretto:21

#ENV FAT_JAR vertx4-start-1.0.0-SNAPSHOT-fat.jar
ENV FAT_JAR=starter-1.0.0-SNAPSHOT-fat.jar
ENV APP_HOME=/usr/app/

COPY build/libs/$FAT_JAR $APP_HOME

EXPOSE 8888

WORKDIR $APP_HOME
ENTRYPOINT [ "/bin/bash", "-c" ]
CMD ["exec java -jar $FAT_JAR"]