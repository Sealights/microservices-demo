# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM node:20.5.0-alpine@sha256:11087abe911baf2fd7e34192f4598bf7e438239e9914f5b7ecda5fb5a7b1a2dd as base

FROM base as builder

ARG AGENT_URL
ARG GITHUB_SCTOKEN
ARG NPM_REGISTRIES_TOKEN_NORMAL
ARG NPM_REGISTRIES_TOKEN_SEALIGHTS
ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME=0.4.3

# Some packages (e.g. @google-cloud/profiler) require additional
# deps for post-install scripts
RUN apk add --update --no-cache \
    python3 \
    make \
    g++

WORKDIR /usr/src/app

#COPY package*.json ./
COPY . .
RUN npm install --only=production

# Optionally set them as environment variables if you need them at runtime
ENV NPM_REGISTRIES_TOKEN_NORMAL=$NPM_REGISTRIES_TOKEN_NORMAL \
    NPM_REGISTRIES_TOKEN_SEALIGHTS=$NPM_REGISTRIES_TOKEN_SEALIGHTS \
    GITHUB_SCTOKEN=$GITHUB_SCTOKEN

# Run echo commands to print the values (useful for debugging; remove in production)
RUN echo "NPM_REGISTRIES_TOKEN_NORMAL=${NPM_REGISTRIES_TOKEN_NORMAL}" && \
    echo "NPM_REGISTRIES_TOKEN_SEALIGHTS=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" && \
    echo "GITHUB_SCTOKEN=${GITHUB_SCTOKEN}"


# Create an .npmrc
RUN echo "//registry.npmjs.org/:_authToken=${NPM_REGISTRIES_TOKEN_NORMAL}" >> .npmrc && \
    echo "//nex.sealights.co:8081/repository/:_authToken=${NPM_REGISTRIES_TOKEN_SEALIGHTS}" >> .npmrc && \
    echo "//npm.pkg.github.com/:_authToken=${GITHUB_SCTOKEN}" >>  .npmrc

RUN npm i ${AGENT_URL}

RUN cat package.json

RUN mkdir ./node_modules/slnodejs && cp -r ./node_modules/@sealights/slnodejs-canary-internal/* ./node_modules/slnodejs
RUN chmod +x ./node_modules/slnodejs/
RUN ls ./node_modules/.bin

RUN test -d ./node_modules/@sealights/slnodejs-canary-internal && echo "Directory 'slnodejs-canary' exists in node_modules." || echo "Directory 'slnodejs-canary' does not exist in node_modules."

RUN  ./node_modules/slnodejs config --token ${SEALIGHTS_TOKEN} --appname "paymentservice" --branch ${BRANCH} --build ${BUILD_NAME}


RUN ./node_modules/slnodejs scan --token ${SEALIGHTS_TOKEN} --buildsessionidfile buildSessionId --workspacepath "." --scm none --es6Modules

FROM base

WORKDIR /usr/src/app
COPY --from=builder /usr/src/app/buildSessionId .

#RUN ./node_modules/.bin/slnodejs run --tokenfile sltoken.txt --buildsessionidfile buildSessionId --labid integ_master_loadgenerator --workspacepath "." --useinitialcolor true --useslnode2 -- ./server.js

COPY --from=builder /usr/src/app/node_modules ./node_modules

COPY . .
ENV NODE_DEBUG=sl
ENV SL_LOG_LEVEL=debug
EXPOSE 50051

CMD  ./node_modules/slnodejs run --token "${SL_TOKEN}" --buildsessionidfile buildSessionId --labid "${SL_LAB_ID}" --workspacepath . index.js
