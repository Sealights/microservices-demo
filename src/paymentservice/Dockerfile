# Base image with specific Node.js version
FROM node:20.5.0-alpine@sha256:11087abe911baf2fd7e34192f4598bf7e438239e9914f5b7ecda5fb5a7b1a2dd as base

# Install build dependencies (if your packages require them for building)
RUN apk add --no-cache python3 make g++

# Set the working directory in the Docker image
WORKDIR /usr/src/app

# Builder stage to handle dependencies and build
FROM base as builder

# Passing build-time variables
ARG AGENT_URL
ARG GITHUB_SCTOKEN
ARG NPM_REGISTRIES_TOKEN_NORMAL
ARG NPM_REGISTRIES_TOKEN_SEALIGHTS
ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME=0.4.3

# Set environment variables from the build arguments
# This step is crucial for using these tokens during npm install
ENV NPM_REGISTRIES_TOKEN_NORMAL=$NPM_REGISTRIES_TOKEN_NORMAL \
    NPM_REGISTRIES_TOKEN_SEALIGHTS=$NPM_REGISTRIES_TOKEN_SEALIGHTS \
    GITHUB_SCTOKEN=$GITHUB_SCTOKEN \
    SEALIGHTS_TOKEN=$SEALIGHTS_TOKEN

# Create an .npmrc file with authentication for private npm repositories
RUN echo "//registry.npmjs.org/:_authToken=${NPM_REGISTRIES_TOKEN_NORMAL}" > .npmrc && \
    echo "//nex.sealights.co:8081/repository/npm/:_authToken=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >> .npmrc

# Copy your project files into the Docker image
COPY . .

# Install dependencies, including those from private repositories
RUN npm install

# If the AGENT_URL is required for a package, install it separately as needed
# RUN npm i "${AGENT_URL}"

# Execute any build-specific commands, such as compiling TypeScript, etc.
# For example, RUN npm run build

# Removing .npmrc after installation to ensure tokens are not left in the image
RUN rm -f .npmrc

# Configure Sealights (assuming slnodejs is correctly installed at this point)
RUN npx slnodejs config --token ${SEALIGHTS_TOKEN} --appname "paymentservice" --branch ${BRANCH} --build ${BUILD_NAME}
RUN npx slnodejs scan --token ${SEALIGHTS_TOKEN} --buildsessionidfile buildSessionId --workspacepath "." --scm none --es6Modules

# Final stage, preparing the runtime environment
FROM base

# Set working directory
WORKDIR /usr/src/app

# Copy built assets from the builder stage
COPY --from=builder /usr/src/app .

# Expose the port your app will run on
EXPOSE 50051

# Set environment variables for runtime (if needed)
ENV NODE_ENV=production \
    NODE_DEBUG=sl \
    SL_LOG_LEVEL=debug

# Command to run your application
CMD ["npx", "slnodejs", "run", "--tokenfile", "sltoken.txt", "--buildsessionidfile", "buildSessionId", "--labid", "integ_master_loadgenerator", "--workspacepath", ".", "--useinitialcolor", "true", "--useslnode2", "--", "index.js"]
