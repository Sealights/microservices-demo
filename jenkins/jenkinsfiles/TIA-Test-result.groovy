@Library('main-shared-library') _

pipeline {
  agent {
    kubernetes {
      yaml kubernetes.base_pod([
        template_path: "ci/pod_templates/shell_pod.yaml",
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ci:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        memory_request: "1500Mi",
        memory_limit: "7000Mi",
        cpu_request: "2.5",
        cpu_limit: "5",
        node_selector: "nightly"
      ])
      defaultContainer 'shell'
    }
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'BTQ-TIA', description: 'Branch to clone')
    string(name: 'INTEGRAION_BRANCH', defaultValue: 'ahmad-branch', description: 'integration branch Branche')
    string(name: 'APP_NAME', defaultValue: 'ahmad-BTQ', description: 'app name')
    string(name: 'LAB_ID', defaultValue: '', description: 'lab_id')
    string(name: 'RUN_DATA', defaultValue: 'full-run', description: 'RUN_NUMBER loop number')
    string(name: 'LAB_UNDER_TEST',defaultValue: 'https://dev-integration.dev.sealights.co/api',description: 'The lab you want to test\nE.g. "https://dev-keren-gw.dev.sealights.co/api"')
    string(name: 'SEALIGHTS_ENV_NAME', defaultValue: 'dev-integration')


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
            echo {"\\"server"\\":  "\\"${params.LAB_UNDER_TEST}"\\", "\\"env"\\":  "\\"${params.SEALIGHTS_ENV_NAME}"\\"} >server.json
            export APP_NAME="${params.APP_NAME}"
            export BRANCH_NAME="${params.INTEGRAION_BRANCH}"
            export LAB_ID="${params.LAB_ID}"
            export RUN_DATA="${params.RUN_DATA}"
            export EXTERNAL_CUSTOMER_ID="integration"
            export EXTERNAL_USER_EMAIL="integration@sealights.io"
            export EXTERNAL_USER_PASSWORD="SeaLights2019!"
            npm install chai chai-deep-equal-in-any-order --save-dev
            npm install
            ./node_modules/.bin/tsc
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/TIA-Tests/TIA-test-result-spec.js --no-timeouts
          """
        }
      }
    }
  }

}
