# Use a base image with Java installed
FROM eclipse-temurin:17 as builder

ARG SEALIGHTS_TOKEN=eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctYWM3YjVlMTctZTA1MC00NmRiLTg4N2UtZTBkYzI5Mzk5OTQ1LDE3MTU4NTA2ODE5ODAiLCJzdWJqZWN0IjoiU2VhTGlnaHRzQGFnZW50IiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50IiwieC1zbC1zZXJ2ZXIiOiJodHRwczovL2Rldi1pbnRlZ3JhdGlvbi5kZXYuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE3MTU4NTA2ODF9.Gh8uVE10U5rLyfanyNAp4Zj0O60oUlhDdMgdsIVOYCHr_X1XWNbx8pujA4B47RC0DDeJJcx2LxTHL2kgFiLcvdz5FN6dqs0UWXP0T-iugf7AxF0Q7g0kkUzzinjnUuAC4_V86Or4vB82Llu7pXgKYWn9T2LC0PHpu668Z5m0az7SF9J6v6PqnSlpB1razI7x3wG9R-u0nRR8eCYHLAH1tJfqn4dcgHqWPZSRDyUZA9wFBiDM5srK1Q6cYQ0kR1kfmRpg3yM9meWd5wH-GzgwOhMnYxrJPPk5LmF4a5ifaM2eZSf3b_iF6gCPcXWoaUomQ_oxhZEubz3RED-WG6XmcckJ5u40tYnNo3kNNPYCS1G5UhqAP9yDUeyjb4kYveXRHwwAoHDYqGxf84o_jrifSvv_DfRDeQk_NwT4tJGnUL-jr5Mgn4VMSq95mkzAU3deDfAN1NO-3olb0NxfdiLq770ie4rwyTgyaSbPi2NtFff-0AKCs2mcd0bWQZY-ATXE8phr1XHjQw5yAz69GAXH2zOVEd6ytYSSv9399o0gCzEugV9CrIfbuvX2GAmqpL5L3wE6WlW-pvJBQq4AwWFBamJVrXBzbS0VEdSkyOWLJgLVwd7Re1qQZXTYpbufkPAePFynTLO9ATiU-JWRpWDudQWYCJfUPsLHVPfc_xvdMME
ARG BRANCH=main
ARG BUILD_NAME=1.2
ARG SERVICE_NAME=sealightsservice
ENV OS_ARCH linux-amd64

RUN apt-get update && apt-get install -y wget zip

WORKDIR /sealights

COPY bash.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/bash.sh
RUN /usr/local/bin/bash.sh 4.0.2502
RUN ls

WORKDIR /app
COPY . /app
RUN ./mvnw clean package

# Run the Sealights build scanner configuration
RUN java -Dsl.enableUpgrade=false -Dsl.featuresData.enableLineCoverage=true -Dsl.featuresData.lineCoverageVersion=v2 -jar /sealights/sl-build-scanner-4.0.2502.jar -config -token ${SEALIGHTS_TOKEN} -appname "${SERVICE_NAME}" -branchname "${BRANCH}" -buildname "${BUILD_NAME}" -pi "sealights.notes.*" -pe "sealights.notes.sealights"

# Scan the build
RUN java -Dsl.enableUpgrade=false -Dsl.featuresData.enableLineCoverage=true -Dsl.featuresData.lineCoverageVersion=v2 -Dsl.log.toConsole=true -Dsl.log.level=info -Dsl.log.enabled=true -jar /sealights/sl-build-scanner-4.0.2502.jar -scan -token ${SEALIGHTS_TOKEN} -buildsessionidfile buildSessionId.txt -workspacepath "./target" -fi "*.jar"

FROM openjdk:17-alpine as runtime
COPY --from=builder /sealights/sl-test-listener-4.0.2502.jar /sealights/sl-test-listener-4.0.2502.jar
COPY --from=builder /app/buildSessionId.txt /sealights/buildSessionId.txt
WORKDIR /app
COPY --from=builder /app/target/sealights-0.0.1-SNAPSHOT.jar /app/sealights-0.0.1-SNAPSHOT.jar
EXPOSE 5732

CMD ["java", "-jar", "/app/sealights-0.0.1-SNAPSHOT.jar"]
