FROM alpine:latest

ARG GITHUB_TOKEN
ARG GITHUB_SCTOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

RUN apk add --no-cache curl jq unzip

# Configure npm to use GitHub package registry
RUN echo "@sealights:registry=https://npm.pkg.github.com" >> .npmrc && \
    echo "email=devops@sealights.io" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >> .npmrc

# Fetch the download URL for the Sealights agent and handle both tar.gz and zip files
RUN echo "Fetching release assets..." && \
    curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" \
         https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest | jq '.assets[].name' && \
    curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" \
         https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest | jq -r \
         '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url' > urls.txt && \
    cat urls.txt && \
    while read -r url; do \
        if [[ -n "$url" ]]; then \
            echo "Attempting to download $url"; \
            filename=$(basename "$url"); \
            status=$(curl -L -w "%{http_code}" -o "$filename" "$url" -s); \
            if [[ "$status" != "200" ]]; then \
                echo "Failed to download $url with status $status"; \
                cat "$filename"; \
                continue; \
            fi; \
            if [[ "$filename" == *.zip ]]; then \
                if unzip -t "$filename"; then \
                    unzip "$filename" -d /app/sealights/agent || { echo "Failed to unzip $filename"; exit 1; }; \
                    break; \
                else \
                    echo "Failed to unzip $filename"; \
                    exit 1; \
                fi; \
            elif [[ "$filename" == *.tar.gz ]]; then \
                if tar -tzf "$filename"; then \
                    tar -xzf "$filename" --directory /app/sealights/agent || { echo "Failed to extract $filename"; exit 1; }; \
                    break; \
                else \
                    echo "Failed to extract $filename"; \
                    exit 1; \
                fi; \
            fi; \
        else \
            echo "No valid URL found"; \
        fi; \
    done < urls.txt

RUN chmod -R 777 /app/sealights

COPY start-app.sh /app/sealights

CMD ["/bin/sh", "/app/sealights/start-app.sh"]
