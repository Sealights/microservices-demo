FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

RUN apk add --no-cache wget jq unzip

# Use wget to download the Sealights agent zip file
RUN wget --header="Authorization: token ${GITHUB_TOKEN}" \
    https://github.com/Sealights/SL.OnPremise.Agents.DotNet/releases/download/v3.9.6/sealights-dotnet-agent-alpine-self-contained.zip -O /app/sealights/agent/sealights-dotnet-agent-alpine-self-contained.zip

RUN ls /app/sealights/agent

# Unzip the downloaded file
RUN unzip /app/sealights/agent/sealights-dotnet-agent-alpine-self-contained.zip -d /app/sealights/agent
RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights

