# Base image with specific Node.js version
FROM node:20.6.1-alpine@sha256:d75175d449921d06250afd87d51f39a74fc174789fa3c50eba0d3b18369cc749 as base

# Builder stage to handle building your application
FROM base as builder

# Setting build arguments
ARG SEALIGHTS_TOKEN
ARG BUILD_NAME
ARG AGENT_URL
ARG BRANCH
ARG GITHUB_SCTOKEN
ARG NPM_REGISTRIES_TOKEN_NORMAL
ARG NPM_REGISTRIES_TOKEN_SEALIGHTS

# Installing system dependencies required by npm packages
RUN apk add --update --no-cache \
    python3 \
    make \
    g++

WORKDIR /usr/src/app

# Copying package.json and package-lock.json
COPY package*.json ./

# Setting up .npmrc for accessing private npm packages
RUN echo "//registry.npmjs.org/:_authToken=${NPM_REGISTRIES_TOKEN_NORMAL}" > .npmrc && \
    echo "//nex.sealights.co:8081/repository/:_authToken=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >> .npmrc

# Installing production dependencies
RUN npm install --only=production

# Installing any additional dependencies including AGENT_URL if it's not part of package.json
RUN npm install "${AGENT_URL}"

# Final stage, setting up the runtime environment
FROM base

WORKDIR /usr/src/app

# Copying built node_modules from the builder stage
COPY --from=builder /usr/src/app/node_modules ./node_modules

# Copying the rest of the application code
COPY . .

# Configuring Sealights with the provided token and project details
RUN ./node_modules/.bin/slnodejs config --token ${SEALIGHTS_TOKEN} --appname "currencyservice" --branch ${BRANCH} --build ${BUILD_NAME}

# Running Sealights scan
RUN ./node_modules/.bin/slnodejs scan --token ${SEALIGHTS_TOKEN} --buildsessionidfile buildSessionId --workspacepath "." --scm none --es6Modules

# Setting environment variables for runtime debugging and logging
ENV NODE_DEBUG=sl
ENV SL_LOG_LEVEL=debug

# Exposing the port the application listens on
EXPOSE 7000

# Command to run the application
CMD ["./node_modules/.bin/slnodejs", "run", "--token", "${SL_TOKEN}", "--buildsessionidfile", "buildSessionId", "--labid", "${SL_LAB_ID}", "--workspacepath", ".", "server.js"]
