FROM java:8
COPY /target/gateway-0.0.1-SNAPSHOT.jar /gateway-0.0.1-SNAPSHOT.jar
COPY gateway.jks /
EXPOSE 443
ENTRYPOINT java -jar /gateway-0.0.1-SNAPSHOT.jar