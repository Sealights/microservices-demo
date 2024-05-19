FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

# Install necessary packages
RUN apk update && apk install -y \
        curl \
        jq \
        unzip\
        git\
        gh

# Install GitHub CLI
RUN curl -fsSL https://github.com/cli/cli/releases/download/v2.21.2/gh_2.21.2_linux_amd64.tar.gz | tar xz -C /usr/local/bin --strip-components=1 gh_2.21.2_linux_amd64/bin/gh

# Verify installation of GitHub CLI
RUN gh --version

# Authenticate GitHub CLI
RUN echo $GITHUB_TOKEN | /usr/local/bin/gh auth login --with-token

# Download the latest release asset
RUN /usr/local/bin/gh release download -R Sealights/SL.OnPremise.Agents.DotNet --pattern "sealights-dotnet-agent-alpine-self-contained.zip" -D /app/sealights/agent

RUN ls /app/sealights/agent

# Unzip the downloaded file
RUN unzip /app/sealights/agent/sealights-dotnet-agent-alpine-self-contained.zip -d /app/sealights/agent
RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights
