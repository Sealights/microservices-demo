FROM alpine:latest

ARG GITHUB_TOKEN

WORKDIR /app
RUN mkdir -p ./sealights/logs
RUN mkdir -p ./sealights/agent

RUN apk add --no-cache curl jq

RUN curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ${GITHUB_TOKEN}" https://api.github.com/repos/Sealights/SL.OnPremise.Agents.DotNet/releases/latest | jq -r '.assets[] | select(.name | test("sealights-dotnet-agent-alpine-self-contained.(tar.gz|zip)$")) | .browser_download_url' | while read -r url; do \
  if [[ -n "$url" ]]; then \
    echo "Downloading $url"; \
    curl -L -o "$(basename "$url")" "$url" || { echo "Failed to download $url"; exit 1; }; \
    echo "Downloaded $(basename "$url")"; \
    tar -xzf "$(basename "$url")" --directory /app/sealights/agent || { echo "Failed to extract $(basename "$url")"; exit 1; }; \
  else \
    echo "No valid URL found"; \
    exit 1; \
  fi; \
done

RUN chmod -R 777 /app/sealights
COPY start-app.sh /app/sealights
