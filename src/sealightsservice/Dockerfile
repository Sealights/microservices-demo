# Use a base image with Java installed
FROM eclipse-temurin:17 as builder

ARG SEALIGHTS_TOKEN
ARG BRANCH=main
ARG BUILD_NAME=1.2
ARG SERVICE_NAME=sealightsservice
ENV OS_ARCH linux-amd64

RUN apt-get update && apt-get install -y wget && apt-get install -y zip

WORKDIR /sealights
RUN wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
# Use jar to extract the contents of instead of installing unzip
RUN unzip -oq sealights-java-latest.zip

WORKDIR /app
COPY . /app
RUN ./mvnw clean package
RUN java -jar /sealights/sl-build-scanner.jar -config -token ${SEALIGHTS_TOKEN} -appname "sealightsservice" -branchname ${BRANCH} -buildname ${BUILD_NAME} -pi "sealights.notes.*" -pe "sealights.notes.sealights"
RUN java -jar /sealights/sl-build-scanner.jar -scan -token ${SEALIGHTS_TOKEN} -buildsessionidfile buildSessionId.txt -workspacepath "./target" -fi "*.jar"
# Expose the port that your Spring Boot application listens on

FROM openjdk:17-alpine as runtime
WORKDIR /app
COPY --from=builder /app/buildSessionId.txt /app/buildSessionId.txt
COPY --from=builder app/target/sealights-0.0.1-SNAPSHOT.jar /app/sealights-0.0.1-SNAPSHOT.jar 
EXPOSE 5732

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "/app/sealights-0.0.1-SNAPSHOT.jar"]