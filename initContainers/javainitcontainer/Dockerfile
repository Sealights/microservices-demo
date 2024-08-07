FROM alpine:3.17.0

ARG GITHUB_TOKEN
ARG VERSION

# Install curl and bash
RUN apk add --no-cache curl bash

# Download the Java agent: bootstrapper and SL:CD agent
RUN bash bash.sh $VERSION

RUN mv java-agent-bootstrapper-ftv-${VERSION}.jar sl-cd-agent.jar

RUN ls

# Copy the SL CD agent to the desired location
