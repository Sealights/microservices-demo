# Use a base image with Java installed
FROM maven:3.9.6-sapmachine-17 as builder

ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME=1.1
ARG SERVICE_NAME=sealightsservice
ENV OS_ARCH linux-amd64
ARG AGENT_URL=https://agents.sealights.co/sealights-java/sealights-java-latest.zip

RUN apt-get update && apt-get install -y wget && apt-get install -y zip

WORKDIR /sealights
RUN wget -nv ${AGENT_URL}
# Use jar to extract the contents of instead of installing unzip
RUN unzip -oq sealights-java-latest


WORKDIR /app
COPY . /app
RUN mvn clean package
RUN java -jar /sealights/sl-build-scanner.jar -config -token ${SEALIGHTS_TOKEN} -appname "sealightsservice" -branchname ${BRANCH} -buildname ${BUILD_NAME} -pi "sealights.notes.*" 
RUN java -jar /sealights/sl-build-scanner.jar -scan -token ${SEALIGHTS_TOKEN} -buildsessionidfile buildSessionId.txt -workspacepath "./target" -fi "*.jar"
# Expose the port that your Spring Boot application listens on

FROM openjdk:17-alpine as runtime
COPY --from=builder /sealights/sl-test-listener.jar /sealights/sl-test-listener.jar
WORKDIR /app
COPY --from=builder app/target/sealights-0.0.1-SNAPSHOT.jar /sealights/sealights-0.0.1-SNAPSHOT.jar 
EXPOSE 5732

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "target/sealights-0.0.1-SNAPSHOT.jar"]