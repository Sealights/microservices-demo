FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

RUN apk add --no-cache curl jq unzip

# Fetch the download URL for the sealights agent and unzip the file
RUN curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest | jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url' | while read -r url; do \
    if [[ -n "$url" ]]; then \
        echo "Downloading $url"; \
        filename=$(basename "$url"); \
        curl -L -o "$filename" "$url" || echo "Failed to download $url"; \
        if [[ "$filename" == *.zip ]]; then \
            unzip "$filename" -d /app/sealights/agent; \
        elif [[ "$filename" == *.tar.gz ]]; then \
            tar -xzf "$filename" --directory /app/sealights/agent; \
        fi; \
    else \
        echo "No valid URL found"; \
    fi; \
done

RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights
