FROM alpine:latest

WORKDIR /temp
RUN mkdir -p ./logs
RUN mkdir -p ./agent
RUN wget -nv -O sealights-dotnet-agent-alpine.tar.gz https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz
RUN tar -xzf ./sealights-dotnet-agent-alpine.tar.gz --directory /temp/agent && rm ./sealights-dotnet-agent-alpine.tar.gz
RUN chmod -R 777 /temp
