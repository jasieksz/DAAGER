FROM openjdk:8-jdk-alpine

VOLUME /dependencies

COPY age3-core-0.3-SNAPSHOT-all.jar ./age3-all.jar
COPY start.sh ./

CMD ["/bin/sh", "start.sh"]
