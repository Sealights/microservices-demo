FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs ./sealights/agent

# Install required packages including file
RUN apk add --no-cache curl jq tar file

# Download and extract the tar file with debugging
RUN set -x; \
    curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" \
        https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest | \
    jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url' | \
    while read -r url; do \
        echo "Found URL: $url"; \
        if [[ -n "$url" ]]; then \
            echo "Downloading $url"; \
            curl -L -o "$(basename "$url")" "$url" || { echo "Failed to download $url"; exit 1; }; \
            echo "Downloaded $(basename "$url")"; \
            ls -l "$(basename "$url")"; \
            file "$(basename "$url")"; \
            echo "Content of $(basename "$url"):"; \
            cat "$(basename "$url")"; \
            if [ ! -s "$(basename "$url")" ]; then \
                echo "File is empty or invalid"; \
                exit 1; \
            fi; \
            tar -tzf "$(basename "$url")" || { echo "Failed to list contents of $(basename "$url")"; exit 1; }; \
            tar -xzf "$(basename "$url")" --directory /app/sealights/agent || { echo "Failed to extract $(basename "$url")"; exit 1; }; \
        else \
            echo "No valid URL found"; \
            exit 1; \
        fi; \
    done

RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights
