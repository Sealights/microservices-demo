FROM python:3.10.8-slim@sha256:49749648f4426b31b20fca55ad854caa55ff59dc604f2f76b57d814e0a47c181 as base
FROM base as builder

ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME=0.4.3
ARG AGENT_URL=sealights-python-agent
# Install Git in the base stage
RUN apt-get -qq update \
    && apt-get install -y --no-install-recommends \
        wget g++ git \
    && rm -rf /var/lib/apt/lists/*

# get packages
COPY requirements.txt .
RUN pip install -r requirements.txt
RUN pip install ${AGENT_URL}

FROM builder

ENV PYTHONUNBUFFERED=1
# get packages

COPY --from=builder /usr/local/lib/python3.10/ /usr/local/lib/python3.10/
WORKDIR /recommendationservice
COPY . .
RUN sl-python config --appname recommendationservice --branchname ${BRANCH} --buildname ${BUILD_NAME} --workspacepath ./ --token ${SEALIGHTS_TOKEN} --exclude "*grpc*"
RUN sl-python scan --scm none --token ${SEALIGHTS_TOKEN}
ENV SL_DEBUG=true
#COPY --from=builder /recommendationservice/buildSessionId.txt .
#RUN export SL_BUILD_SESSION_ID=`cat buildSessionId.txt`
ENV PORT "8080"
EXPOSE 8080
CMD sl-python run python recommendation_server.py --labid ${SL_LAB_ID} --token ${SL_TOKEN}
