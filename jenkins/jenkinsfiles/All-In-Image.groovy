@Library('main-shared-library@abed/nodejs-ci') _

pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/All_In_Image_shell_pod.yaml')
      defaultContainer "shell"
    }
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '30')
    timestamps()
  }

  parameters {
    string(name: 'BRANCH', defaultValue: "${env.BRANCH_NAME}", description: 'Branch to clone (ahmad-branch)')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
    string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
    string(name: 'MACHINE_DNS', defaultValue: 'http://10.2.11.97:8081', description: 'machine dns')
    booleanParam(name: 'Run_all_tests', defaultValue: false, description: 'Checking this box will run all tests even if individual ones are not checked')
    booleanParam(name: 'Cypress', defaultValue: true, description: 'Run tests using Cypress testing framework')
    booleanParam(name: 'Mocha', defaultValue: true, description: 'Run tests using Mocha testing framework')

  }
  environment {
    MACHINE_DNS = "${params.MACHINE_DNS}"
    machine_dns = "${params.MACHINE_DNS}"
  }
  stages{
    stage('Setup'){
      steps{
        script {
            if (params.NODEJS_CI) {
                github.set_github_registries()
            }
        }
      }
    }
    stage('Cypress framework starting'){
      steps{
        script {
          if (params.Run_all_tests == true || params.Cypress == true) {
            build(job: "Cypress_test/${env.BRANCH_NAME}", parameters: [string(name: 'BRANCH', value: "${params.BRANCH}"), string(name: 'SL_LABID', value: "${params.SL_LABID}"), string(name: 'SL_TOKEN', value: "${params.SL_TOKEN}"), string(name: 'MACHINE_DNS1', value: "${params.MACHINE_DNS}"),booleanParam(name: 'NODEJS_CI', value: true)])
          }
        }
      }
    }
    stage('Postman framework'){
      steps{
        script {
            if (true) {
            sh """
            echo 'Postman framework starting ..... '
            export MACHINE_DNS="${params.MACHINE_DNS}"
            cd ./integration-tests/postman-tests/
            cp -r /nodeModules/node_modules .
            if [ "${params.NODEJS_CI}" = "true" ]; then
              npm install @sealights/sealights-newman-runner@canary || {
                  echo "Failed to install @sealights/sealights-newman-runner"
                  exit 1
              }
            fi
            npx sealights-newman-runner ... --token ${params.SL_TOKEN} --sl-labid ${params.SL_LABID} --sl-testStage "postman-tests" -c sealights-excersise.postman_collection.json --env-var machine_dns="${params.MACHINE_DNS}"
            cd ../..
          """
          }
        }
      }
    }
  }
}


