FROM alpine:3.17.0
RUN apk --no-cache add curl
RUN curl -L -u sealights:${GITHUB_SCTOKEN} "https://maven.pkg.github.com/https://github.com/Sealights/SL.OnPremise.Agents.Java/packages" -o java-agent-bootstrapper-ftv-${VERSION}-sources.jar
RUN mv java-agent-bootstrapper-ftv-${VERSION}-sources.jar sl-cd-agent.jar

COPY sl-cd-agent.jar /sl-cd-agent.jar
