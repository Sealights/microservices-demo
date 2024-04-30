@Library('main-shared-library') _
pipeline {
  agent {
    kubernetes {
      yaml kubernetes.base_pod([
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ci:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        shell_memory_request: "2000Mi",
        shell_cpu_request: "1.0",
        shell_memory_limit: "3000Mi",
        shell_cpu_limit: "1.5",
        kaniko_memory_request: "3500Mi",
        kaniko_cpu_request: "1.0",
        kaniko_memory_limit: "4500Mi",
        kaniko_cpu_limit: "2.5",
        kaniko_storage_limit: "6500Mi",
        node_selector: "jenkins"
      ])
      defaultContainer 'shell'
    }
  }
  parameters {
    string(name: 'TAG', defaultValue: '1.2.2', description: 'Latest tag')
    string(name: 'BRANCH', defaultValue: 'main', description: 'Default branch')
    choice(name: 'LANG', choices: ['javaInitContainer', 'dotnetInitContainer'], description: 'Choose lang technology')
  }
  environment {
    ECR_URI = "sealights/${params.LANG}"
    GITHUB_TOKEN = secrets.get_secret('mgmt/github_token', 'us-west-2')
  }
  stages {
    stage('Init') {
      steps {
        script {
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
          env.verion = sh(returnStdout: true, script: """gh api \\
                        -H "Accept: application/vnd.github+json" \\
                        -H "X-GitHub-Api-Version: 2022-11-28" \\
                        /users/Sealights/packages/maven/io.sealights.on-premise.agents.java-agent-bootstrapper-ftv/versions \\
                        | jq -r '.[0].name'""").trim()
          echo "${env.verion}"
        }
      }
    }
    stage("Create ECR repository") {
      steps {
        script {
          def repo_policy = libraryResource('ci/ecr/repo_policy.json')
          ecr.create_repo([
            artifact_name: "${params.LANG}",
            key_type: "KMS"
          ])
          ecr.set_repo_policy([
            artifact_name: "${params.LANG}",
            repo_policy: repo_policy
          ])
        }
      }
    }
    stage('Build InitContainer Image') {
      steps {
        container(name: 'kaniko') {
          script {
            def CONTEXT = "./initContainers/${params.LANG}"
            def DP = "${CONTEXT}/Dockerfile"
            def D = "${env.ECR_URI}:latest"
            def DD ="${env.ECR_URI}:${BUILD_NUMBER}"
            def VERSION = "${env.verion}"
            sh """
                /kaniko/executor \
                --context ${CONTEXT} \
                --dockerfile ${DP} \
                --destination ${D} \
                --destination ${DD} \
                --build-arg GITHUB_TOKEN=${env.GITHUB_TOKEN} \
                --build-arg VERSION=${VERSION}
            """
          }
        }
      }
    }
  }
}
