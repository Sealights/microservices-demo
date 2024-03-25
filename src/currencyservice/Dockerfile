FROM node:20.6.1-alpine@sha256:d75175d449921d06250afd87d51f39a74fc174789fa3c50eba0d3b18369cc749 as base

FROM base as builder

ARG SEALIGHTS_TOKEN
ARG BUILD_NAME
ARG AGENT_URL
ARG BRANCH
ARG GITHUB_SCTOKEN
ARG NPM_REGISTRIES_TOKEN_NORMAL
ARG NPM_REGISTRIES_TOKEN_SEALIGHTS

# Some packages (e.g. @google-cloud/profiler) require additional
# deps for post-install scripts
RUN apk add --update --no-cache \
    python3 \
    make \
    g++

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install --only=production

# Create an .npmrc file on-the-fly with your GitHub PAT
RUN echo "//registry.npmjs.org/:_authToken=${NPM_REGISTRIES_TOKEN_NORMAL}" >> .npmrc && \
    echo "//nex.sealights.co:8081/repository/:_authToken=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >>  .npmrc

RUN npm install ${AGENT_URL}

FROM base

WORKDIR /usr/src/app

COPY --from=builder /usr/src/app/node_modules ./node_modules

COPY . .

#run comfig
RUN  ./node_modules/.bin/slnodejs config --token ${SEALIGHTS_TOKEN} --appname "currencyservice" --branch ${BRANCH} --build ${BUILD_NAME}
#sl-scan
RUN ./node_modules/.bin/slnodejs scan --token ${SEALIGHTS_TOKEN} --buildsessionidfile buildSessionId --workspacepath "." --scm none --es6Modules
ENV NODE_DEBUG=sl
ENV SL_LOG_LEVEL=debug


EXPOSE 7000
CMD  ./node_modules/.bin/slnodejs run --token "${SL_TOKEN}" --buildsessionidfile buildSessionId --labid "${SL_LAB_ID}" --workspacepath . server.js
