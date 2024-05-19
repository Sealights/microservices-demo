FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

# Install necessary packages
RUN apk add --no-cache curl jq unzip git github-cli

# Install GitHub CLI
RUN curl -fsSL https://github.com/cli/cli/releases/download/v2.21.2/gh_2.21.2_linux_amd64.tar.gz | tar xz -C /usr/local/bin --strip-components=1 gh_2.21.2_linux_amd64/bin/gh

# Verify installation of GitHub CLI
RUN gh --version
# Configure GitHub CLI to use the GITHUB_TOKEN
RUN export GH_TOKEN=$GITHUB_TOKEN

# Download the latest release asset
RUN gh release download -R Sealights/SL.OnPremise.Agents.DotNet --pattern "sealights-dotnet-agent-alpine-self-contained.zip" -D /app/sealights/agent

RUN ls /app/sealights/agent

# Unzip the downloaded file
RUN unzip /app/sealights/agent/sealights-dotnet-agent-alpine-self-contained.zip -d /app/sealights/agent
RUN chmod -R 777 /app/sealights
