FROM alpine:3.17.0

ARG GITHUB_TOKEN
ARG VERSION
# Install curl
RUN apk add --no-cache curl
# Download the Java agent bootstrapper and SL CD agent
RUN bash jenkins/scripts/bash.sh $VERSION  -o sl-cd-agent.jar

RUN mv java-agent-bootstrapper-ftv-${VERSION}.jar sl-cd-agent.jar

RUN ls
# Copy the SL CD agent to the desired location
