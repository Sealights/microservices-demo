FROM alpine:3.17.0

ARG GITHUB_TOKEN
ARG VERSION
# Install curl
RUN apk add --no-cache curl
# Download the Java agent bootstrapper and SL CD agent
RUN curl -L -u sealights:${GITHUB_TOKEN} "https://maven.pkg.github.com/Sealights/SL.OnPremise.Agents.Java/packages/java-agent-bootstrapper-ftv-${VERSION}.jar" -o java-agent-bootstrapper-ftv-${VERSION}.jar \
    && curl -L -u sealights:${GITHUB_TOKEN} "https://maven.pkg.github.com/Sealights/SL.OnPremise.Agents.Java/packages/sl-cd-agent.jar" -o sl-cd-agent.jar

RUN ls
# Copy the SL CD agent to the desired location
COPY sl-cd-agent.jar /sl-cd-agent.jar
