FROM alpine:latest

WORKDIR /sealights
RUN mkdir -p ./logs
RUN mkdir -p ./agent
RUN wget -nv -O sealights-dotnet-agent-alpine.tar.gz https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz

RUN tar -xzf ./sealights-dotnet-agent-alpine.tar.gz --directory /sealights/agent
RUN chmod -R 777 /sealights