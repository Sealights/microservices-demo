FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs ./sealights/agent

# Install required packages including file and unzip
RUN apk add --no-cache curl jq file unzip

# Download and extract the zip file with debugging
RUN set -x; \
    echo "Using GITHUB_TOKEN: ${GITHUB_TOKEN}"; \
    response=$(curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest); \
    echo "GitHub API response: $response"; \
    echo "$response" | jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url' | \
    while read -r url; do \
        echo "Found URL: $url"; \
        if [[ -n "$url" ]]; then \
            echo "Downloading $url"; \
            curl -L -o "$(basename "$url")" "$url" || { echo "Failed to download $url"; exit 1; }; \
            echo "Downloaded $(basename "$url")"; \
            ls -l "$(basename "$url")"; \
            filetype=$(file -b --mime-type "$(basename "$url")"); \
            echo "File type: $filetype"; \
            if [ ! -s "$(basename "$url")" ]; then \
                echo "File is empty or invalid"; \
                exit 1; \
            fi; \
            if [ "$filetype" == "application/zip" ]; then \
                unzip "$(basename "$url")" -d /app/sealights/agent || { echo "Failed to unzip $(basename "$url")"; exit 1; }; \
            elif [ "$filetype" == "application/gzip" ]; then \
                tar -xzf "$(basename "$url")" --directory /app/sealights/agent || { echo "Failed to extract $(basename "$url")"; exit 1; }; \
            else \
                echo "Unsupported file type: $filetype"; \
                exit 1; \
            fi; \
        else \
            echo "No valid URL found"; \
            exit 1; \
        fi; \
    done

RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights
