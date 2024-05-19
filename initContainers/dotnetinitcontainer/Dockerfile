FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

RUN apk add --no-cache curl jq unzip

RUN curl "https://github.com/Sealights/SL.OnPremise.Agents.DotNet/releases/download/v3.9.6/sealights-dotnet-agent-alpine-self-contained.zip"
# Fetch the download URL for the sealights agent and unzip the file
#RUN DOWNLOAD_URL=$(curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest | jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url') && \
#    if [[ -n "$DOWNLOAD_URL" ]]; then \
#        echo "Downloading $DOWNLOAD_URL"; \
#        curl -L -o sealights-dotnet-agent-alpine-self-contained.zip "$DOWNLOAD_URL" || echo "Failed to download $DOWNLOAD_URL"; \
#    else \
#        echo "No valid URL found"; \
#    fi

RUN ls

RUN unzip ./sealights-dotnet-agent-alpine-self-contained.zip -d /app/sealights/agent
RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights

