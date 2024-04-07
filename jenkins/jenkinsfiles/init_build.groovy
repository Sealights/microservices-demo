@Library('main-shared-library') _
pipeline{
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
        kaniko_storage_limit:"6500Mi",
        node_selector: "jenkins"
      ])
      defaultContainer 'shell'
    }
  }
  parameters {
    string(name: 'TAG', defaultValue: '1.2.2', description: 'latest tag')
    string(name: 'BRANCH', defaultValue: 'main', description: 'defult branch')
    string(name: 'LANG', defaultValue: '', description: 'Service name to build')
    string(name: 'VERSION', defaultValue: '', description: 'agent version')

  }
  environment{
    ECR_FULL_NAME = "btq-${params.SERVICE}"
    ECR_URI = "534369319675.dkr.ecr.us-west-2.amazonaws.com/${env.ECR_FULL_NAME}"
  }
  stages{
    stage('Init') {
      steps {
        script {
          // Clone the repository with the specified branch.
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
        }
        stage("Create ECR repository") {
          def repo_policy = libraryResource 'ci/ecr/repo_policy.json'
          ecr.create_repo([
            artifact_name: "${env.ECR_FULL_NAME}",
            key_type: "KMS"
          ])
          ecr.set_repo_policy([
            artifact_name: "${env.ECR_FULL_NAME}",
            repo_policy: repo_policy
          ])
        }
        stage("Build Docker ${params.LANG} Image") {
          container(name: 'kaniko'){
            script {
              def CONTEXT = "./initContainers/${params.LANG}InitContainer"
              def DP = "${CONTEXT}/Dockerfile"
              def D = "${env.ECR_URI}:${params.TAG}"
              def VERSION = "${params.VERSION}"
              def GITHUB_TOKEN = secrets.get_secret('mgmt/github_token', 'us-west-2')

              sh """
                    /kaniko/executor \
                    --context ${CONTEXT} \
                    --dockerfile ${DP} \
                    --destination ${D} \
                    --build-arg GITHUB_TOKEN=${GITHUB_TOKEN} \
                    --build-arg VERSION=${VERSION}
                """
            }
          }
        }
      }
    }
  }
}

