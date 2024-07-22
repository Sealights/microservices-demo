@Library('main-shared-library') _
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
    string(name: 'BRANCH', defaultValue: 'slnodejs', description: 'Branch to clone (ahmad-branch)')
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
    stage('Cypress framework starting'){
      steps{
        script {
          if (params.Run_all_tests == true || params.Cypress == true) {
            build(job: "cypress-12", parameters: [string(name: 'BRANCH', value: "${params.BRANCH}"), string(name: 'SL_LABID', value: "${params.SL_LABID}"), string(name: 'SL_TOKEN', value: "${params.SL_TOKEN}"), string(name: 'MACHINE_DNS1', value: "${params.MACHINE_DNS}"), booleanParam(name: 'node_ci', value: true)])
          }
        }
      }
    }

    stage('Postman framework'){
      steps{
        script {
            if (params.Run_all_tests == true) {
              sh """
            echo 'Postman framework starting ..... '
            export MACHINE_DNS="${params.MACHINE_DNS}"
            cd ./integration-tests/postman-tests/
            cp -r /nodeModules/node_modules .
            npm i slnodejs
            npm install newman
            npm install newman-reporter-xunit
            ./node_modules/.bin/slnodejs start --labid ${params.SL_LABID} --token ${params.SL_TOKEN} --teststage "postman-tests"
            npx newman run sealights-excersise.postman_collection.json --env-var machine_dns="${params.MACHINE_DNS}" -r xunit --reporter-xunit-export './result.xml' --suppress-exit-code
            ./node_modules/.bin/slnodejs uploadReports --labid ${params.SL_LABID} --token ${params.SL_TOKEN} --reportFile './result.xml'
            ./node_modules/.bin/slnodejs end --labid ${params.SL_LABID} --token ${params.SL_TOKEN}
            cd ../..
          """
          }
        }
      }
    }
//    stage('Jest framework'){
//      steps{
//        script{
//          sh """
//          echo 'Jest framework starting ..... '
//          export machine_dns="${params.MACHINE_DNS}"
//          cd ./integration-tests/nodejs-tests/Jest
//          cp -r /nodeModules/node_modules .
//          npm i jest-cli
//          export NODE_DEBUG=sl
//          export SL_TOKEN="${params.SL_TOKEN}"
//          export SL_LABID="${params.SL_LABID}"
//          npm install
//          npx jest integration-tests/nodejs-tests/Jest/test.js --sl-testStage='Jest-tests' --sl-token="${params.SL_TOKEN}" --sl-labId="${params.SL_LABID}" --testTimeout=30000
//          cd ../..
//        """
//        }
//      }
//    }
  }
}


