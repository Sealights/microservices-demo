#FROM alpine:latest
#
#ARG GITHUB_TOKEN
#ARG DOTNET_LATEST_VERSION
#WORKDIR /sealights
#RUN mkdir -p ./logs
#RUN mkdir -p ./agent
#
## Install necessary packages
#RUN apk add --no-cache curl jq unzip git github-cli
#
## Install GitHub CLI
#RUN curl -fsSL https://github.com/cli/cli/releases/download/v2.21.2/gh_2.21.2_linux_amd64.tar.gz | tar xz -C /usr/local/bin --strip-components=1 gh_2.21.2_linux_amd64/bin/gh
#
## Verify installation of GitHub CLI
#RUN gh --version
## Configure GitHub CLI to use the GITHUB_TOKEN
#RUN export GH_TOKEN=$GITHUB_TOKEN
#
##RUN gh release download $DOTNET_LATEST_VERSION --repo sealights/SL.OnPremise.Agents.DotNet
#
#RUN gh release download $DOTNET_LATEST_VERSION --repo sealights/SL.OnPremise.Agents.DotNet -D /sealights/agent
#
## Download the latest release asset
##RUN gh release download -R Sealights/SL.OnPremise.Agents.DotNet --pattern "sealights-dotnet-agent-alpine-self-contained.zip" -D /app/sealights/agent
#
#RUN ls /sealights/agent
#
## Unzip the downloaded file
#RUN unzip /sealights/agent/sealights-dotnet-agent-alpine-self-contained.zip -d /sealights/agent
#RUN chmod -R 777 /sealights

FROM alpine:latest

WORKDIR /temp
RUN mkdir -p ./logs
RUN mkdir -p ./agent
RUN wget -nv -O sealights-dotnet-agent-alpine.tar.gz https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz
RUN tar -xzf ./sealights-dotnet-agent-alpine.tar.gz --directory /temp/agent && rm ./sealights-dotnet-agent-alpine.tar.gz
RUN chmod -R 777 /temp
