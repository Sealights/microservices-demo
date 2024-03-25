# Use a specific version of Node.js as a base image
FROM node:20.5.0-alpine@sha256:11087abe911baf2fd7e34192f4598bf7e438239e9914f5b7ecda5fb5a7b1a2dd as base

# Builder stage to handle building your application
FROM base as builder

# Set build arguments
ARG AGENT_URL
ARG GITHUB_SCTOKEN
ARG NPM_REGISTRIES_TOKEN_NORMAL
ARG NPM_REGISTRIES_TOKEN_SEALIGHTS
ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME=0.4.3

# Install dependencies needed for building certain npm packages
RUN apk add --update --no-cache \
    python3 \
    make \
    g++

WORKDIR /usr/src/app

# Copy the entire project into the container
# Make sure your .dockerignore file is configured to exclude what you don't need
COPY . .

# Install production dependencies only
RUN npm install --only=production

# Set environment variables from build arguments
ENV NPM_REGISTRIES_TOKEN_NORMAL=$NPM_REGISTRIES_TOKEN_NORMAL \
    NPM_REGISTRIES_TOKEN_SEALIGHTS=$NPM_REGISTRIES_TOKEN_SEALIGHTS \
    GITHUB_SCTOKEN=$GITHUB_SCTOKEN

# Configure .npmrc for private package access
RUN echo "//registry.npmjs.org/:_authToken=${NPM_REGISTRIES_TOKEN_NORMAL}" >> .npmrc && \
    echo "//nex.sealights.co:8081/repository/:_authToken=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >>  .npmrc

# Install the agent using the provided URL
RUN npm i ${AGENT_URL}

# Configure Sealights
RUN ./node_modules/.bin/slnodejs config --token ${SEALIGHTS_TOKEN} --appname "paymentservice" --branch ${BRANCH} --build ${BUILD_NAME}

# Scan with Sealights
RUN ./node_modules/.bin/slnodejs scan --token ${SEALIGHTS_TOKEN} --buildsessionidfile buildSessionId --workspacepath "." --scm none --es6Modules

FROM base

WORKDIR /usr/src/app
# Copy essential files from the builder stage
COPY --from=builder /usr/src/app/buildSessionId .

# Copy the node_modules directory from the builder stage
COPY --from=builder /usr/src/app/node_modules ./node_modules
COPY . .
ENV NODE_DEBUG=sl
ENV SL_LOG_LEVEL=debug
EXPOSE 50051
CMD ./node_modules/.bin/slnodejs run --token "${SL_TOKEN}" --buildsessionidfile buildSessionId --labid "${SL_LAB_ID}" --workspacepath . index.js
