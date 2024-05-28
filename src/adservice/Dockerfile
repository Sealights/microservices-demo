# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM eclipse-temurin:21@sha256:9d007c093e624bc410f113ee09f5209d3263e0bada7524e4de99eac9fbef06a5 as builder
ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME

RUN apt-get update && \
    apt-get install -y unzip && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY ["build.gradle", "gradlew", "./"]
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew downloadRepos

COPY . .
RUN chmod +x gradlew
RUN ./gradlew installDist

# Install agent
#RUN wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
COPY bash.sh /usr/local/bin/

# Make the script executable
RUN chmod +x /usr/local/bin/bash.sh

# Run the script with the specified version
RUN /usr/local/bin/bash.sh 4.0.2502

# Make build session id
RUN java -Dsl.enableUpgrade=false -Dsl.featuresData.enableLineCoverage=true -Dsl.featuresData.lineCoverageVersion=v2  -jar sl-build-scanner-4.0.2502.jar -scmProvider github -scm git -config -token ${SEALIGHTS_TOKEN} -appname "adservice" -branchname ${BRANCH} -buildname "${BUILD_NAME}" -pi "*hipstershop*"
RUN export SL_BUILD_SESSION_ID=`cat buildSessionId.txt`
# Scan the code
RUN java -Dsl.enableUpgrade=false -Dsl.featuresData.enableLineCoverage=true -Dsl.featuresData.lineCoverageVersion=v2 -Dsl.excludes=*Grpc*,*Demo* -Dsl.includes=*hipstershop*  -jar sl-build-scanner-4.0.2502.jar -scmProvider github -scm git -scan -token ${SEALIGHTS_TOKEN} -buildsessionidfile buildSessionId.txt -workspacepath "/app/build/install/hipstershop/lib" -fi "*.jar" -r

FROM eclipse-temurin:21-jre-alpine@sha256:b5a1250bb72d804d49471207e2ebb574caffc13ac76f99618fb5ea06af621056

RUN apk add --no-cache ca-certificates

# Download Stackdriver Profiler Java agent
RUN mkdir -p /opt/cprof && \
    wget -q -O- https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent_alpine.tar.gz \
    | tar xzv -C /opt/cprof && \
    rm -rf profiler_java_agent.tar.gz

WORKDIR /app
COPY --from=builder /app .

EXPOSE 9555
ENTRYPOINT ["/app/build/install/hipstershop/bin/AdService"]
