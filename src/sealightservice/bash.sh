#!/bin/bash

VERSION=$1
AUTH=ZGV2b3BzOnMyMDE1bA==
NEXUS_HOST=http://nex.sealights.co:8081/repository/sealights-internal

AGENT_TEST_LISTENER="$NEXUS_HOST/io/sealights/on-premise/agents/java-agent-bootstrapper/$VERSION/java-agent-bootstrapper-$VERSION.jar"
AGENT_BUILD_SCANNER="$NEXUS_HOST/io/sealights/on-premise/agents/java-agent/java-build-agent/$VERSION/java-build-agent-$VERSION.jar"
AGENT_CD_AGENT="$NEXUS_HOST/io/sealights/on-premise/agents/java-agent-bootstrapper-ftv/$VERSION/java-agent-bootstrapper-ftv-$VERSION.jar"

wget --header="Authorization: Basic $AUTH" "$AGENT_BUILD_SCANNER"

wget --header="Authorization: Basic $AUTH" "$AGENT_TEST_LISTENER"

wget --header="Authorization: Basic $AUTH" "$AGENT_CD_AGENT"

mv "java-agent-bootstrapper-$VERSION.jar" "sl-test-listener-$VERSION.jar"
mv "java-build-agent-$VERSION.jar" "sl-build-scanner-$VERSION.jar"
mv "java-agent-bootstrapper-ftv-$VERSION.jar" "sl-cd-agent-$VERSION.jar"
