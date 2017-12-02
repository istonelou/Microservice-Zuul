FROM java:8
COPY /target/api-gateway-0.0.1-SNAPSHOT.jar /api-gateway-0.0.1-SNAPSHOT.jar
COPY gateway.jks /
EXPOSE 443
ENTRYPOINT java -jar /api-gateway-0.0.1-SNAPSHOT.jar