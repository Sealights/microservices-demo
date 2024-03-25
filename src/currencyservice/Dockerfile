# Use Node.js 20.6.1 Alpine image as the base
FROM node:20.6.1-alpine@sha256:d75175d449921d06250afd87d51f39a74fc174789fa3c50eba0d3b18369cc749 as base

# Builder stage for installing dependencies and setting up the app
FROM base as builder

# Accept build arguments for various tokens and parameters
ARG SEALIGHTS_TOKEN
ARG BUILD_NAME
ARG AGENT_URL
ARG BRANCH
ARG GITHUB_SCTOKEN
ARG NPM_REGISTRIES_TOKEN_NORMAL
ARG NPM_REGISTRIES_TOKEN_SEALIGHTS

# Install dependencies needed for certain npm packages' post-install scripts
RUN apk add --update --no-cache \
    python3 \
    make \
    g++

WORKDIR /usr/src/app

# Copy package.json and package-lock.json to leverage Docker caching for npm install
COPY package*.json ./

# Initialize .npmrc with tokens for private repositories
RUN echo "//registry.npmjs.org/:_authToken=${NPM_REGISTRIES_TOKEN_NORMAL}" >> .npmrc && \
    echo "//nex.sealights.co:8081/repository/:_authToken=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >> .npmrc

# Install production dependencies
RUN npm install --only=production

# Install any additional packages including those from private URLs
RUN npm install ${AGENT_URL}

# Final stage to set up the runtime environment
FROM base

WORKDIR /usr/src/app

# Copy installed dependencies from builder
COPY --from=builder /usr/src/app/node_modules ./node_modules

# Copy the rest of the application code
COPY . .

# Run configuration commands for Sealights
RUN ./node_modules/.bin/slnodejs config --token ${SEALIGHTS_TOKEN} --appname "currencyservice" --branch ${BRANCH} --build ${BUILD_NAME} && \
    ./node_modules/.bin/slnodejs scan --token ${SEALIGHTS_TOKEN} --buildsessionidfile buildSessionId --workspacepath "." --scm none --es6Modules

# Set environment variables for debugging and Sealights logging
ENV NODE_DEBUG=sl
ENV SL_LOG_LEVEL=debug

# Expose the port the app runs on
EXPOSE 7000

# Define the command to run the app
CMD ["./node_modules/.bin/slnodejs", "run", "--token", "${SL_TOKEN}", "--buildsessionidfile", "buildSessionId", "--labid", "${SL_LAB_ID}", "--workspacepath", ".", "server.js"]
