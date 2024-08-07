FROM alpine:3.17.0

ARG GITHUB_TOKEN
ARG VERSION

# Install curl and bash
RUN apk add --no-cache curl bash

# Copy the bash script into the image
COPY bash.sh /scripts/bash.sh

# Make sure the script is executable
RUN chmod +x /scripts/bash.sh

# Download the Java agent: bootstrapper and SL:CD agent
RUN bash /scripts/bash.sh $VERSION -o sl-cd-agent.jar

RUN mv java-agent-bootstrapper-${VERSION}.jar sl-cd-agent.jar

RUN ls

# Copy the SL CD agent to the desired location
