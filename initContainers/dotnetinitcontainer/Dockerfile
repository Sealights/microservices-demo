# Use an appropriate base image
FROM alpine:latest

# Install curl and jq
RUN apk add --no-cache curl jq

# Set environment variables
ARG GITHUB_TOKEN
WORKDIR /app

# Create necessary directories
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

# Download and rename the specific asset
RUN curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest \
    | jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.tar.gz$")) | .browser_download_url' \
    | while read -r url; do \
        if [ -n "$url" ]; then \
          echo "Downloading $url"; \
          curl -L -o "/app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz" "$url" || echo "Failed to download $url"; \
        else \
          echo "No valid URL found"; \
        fi \
      done

# List files to verify download
RUN ls -l /app/sealights/agent

# Extract the downloaded tar.gz file
RUN tar -xzf /app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz --directory /app/sealights/agent
RUN chmod -R 777 /app/sealights
# Copy additional files
COPY start-app.sh /app/sealights
