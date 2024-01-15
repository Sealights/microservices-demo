@Library('main-shared-library') _

pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/ApiTests_shell_pod.yaml')
      defaultContainer "shell"
    }
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'BTQ-TIA', description: 'Branch to clone')
    string(name: 'APP_NAME', defaultValue: 'ahmad-BTQ', description: 'app name')
    string(name: 'LAB_UNDER_TEST',defaultValue: 'https://dev-integration.dev.sealights.co/api',description: 'The lab you want to test\nE.g. "https://dev-keren-gw.dev.sealights.co/api"')
    string(name: 'SEALIGHTS_ENV_NAME', defaultValue: 'dev-integration')
    string(name: 'RUN_DATA', defaultValue: 'with changes', description: 'RUN_NUMBER loop number')
    string(name: 'INTEGRATION_BRANCH', defaultValue: 'ahmad-branch', description: 'RUN_NUMBER loop number')



  }

  options {
    buildDiscarder logRotator(numToKeepStr: '30')
    timestamps()
  }



  stages{
    stage("Init test"){
      steps{
        script{
          git credentialsId:'sldevopsd', branch: params.BRANCH, url:'git@github.com:Sealights/SL.BackendApiTests.git'
        }
      }
    }


    stage('download NodeJs agent and scanning Mocha tests') {
      steps{
        script{
          tools.set_npm_registries()
          sh """

          ls
          cat server.json
            echo {"\\"server"\\":  "\\"${params.LAB_UNDER_TEST}"\\", "\\"env"\\":  "\\"${params.SEALIGHTS_ENV_NAME}"\\"} >server.json
            cat server.json
            export APP_NAME="${params.APP_NAME}"
            export BRANCH_NAME="${params.INTEGRATION_BRANCH}"
            export RUN_DATA="${params.RUN_DATA}"
            export EXTERNAL_CUSTOMER_ID="integration"
            export EXTERNAL_USER_EMAIL="integration@sealights.io"
            export EXTERNAL_USER_PASSWORD="SeaLights2019!"
            npm install
            ./node_modules/.bin/tsc
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/modified-BTQ/btq_test_coverage.js --no-timeouts
          """
        }
      }
    }
  }
}
