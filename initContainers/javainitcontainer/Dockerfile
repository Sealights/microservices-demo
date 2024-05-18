FROM alpine:3.17.0

ARG GITHUB_TOKEN
ARG VERSION

RUN curl -L -u sealights:${GITHUB_TOKEN} "https://maven.pkg.github.com/https://github.com/Sealights/SL.OnPremise.Agents.Java/packages" -o java-agent-bootstrapper-ftv-${VERSION}.jar -o sl-cd-agent.jar
COPY sl-cd-agent.jar /sl-cd-agent.jar
