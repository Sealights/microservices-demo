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

# Download and rename the specific asset with detailed logging
RUN curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" \
        https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest \
    | jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url' \
    | while read -r url; do \
        if [ -n "$url" ]; then \
            echo "Downloading $url"; \
            curl -L -o "/tmp/$(basename "$url")" "$url"; \
            if [ $? -ne 0 ]; then \
                echo "Failed to download $url"; \
                exit 1; \
            fi; \
            if [ $(stat -c%s "/tmp/$(basename "$url")") -le 100 ]; then \
                echo "Downloaded file is too small, indicating a possible error."; \
                echo "Content of the downloaded file:"; \
                cat "/tmp/$(basename "$url")"; \
                exit 1; \
            fi; \
        else \
            echo "No valid URL found"; \
            exit 1; \
        fi; \
    done

# Verify the downloaded files
RUN ls -l /tmp

# Rename the downloaded .tar.gz file
RUN mv /tmp/sealights-dotnet-agent-alpine-self-contained.tar.gz /app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz

# List files to verify download
RUN ls -l /app/sealights/agent

# Extract the downloaded tar.gz file with detailed logging
RUN if [ -f /app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz ]; then \
        tar -xzf /app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz --directory /app/sealights/agent || { \
            echo "Extraction failed, file content:"; \
            cat /app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz; \
            exit 1; \
        }; \
    else \
        echo "File not found: /app/sealights/agent/sealights-dotnet-agent-alpine.tar.gz"; \
        exit 1; \
    fi

# Set permissions for the sealights directory
RUN chmod -R 777 /app/sealights

# Copy additional files
COPY start-app.sh /app/sealights
