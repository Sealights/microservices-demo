@Library('main-shared-library@node_plugins_ci') _
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
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
    string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
    string(name: 'MACHINE_DNS', defaultValue: 'http://10.2.11.97:8081', description: 'machine dns')

  }
  environment {
    MACHINE_DNS = "${params.MACHINE_DNS}"
    machine_dns = "${params.MACHINE_DNS}"
    wait_time = "20"
  }
  stages{
    stage('Testcafe framework') {
      steps{
        script{
            sh """
                echo 'Testcafe framework starting ..... '
                cd ./integration-tests/testcafe/
                npm install

                npx testcafe 'chromium:headless --no-sandbox' tests.js --reporter sealights --sl-toke ${env.SL_TOKEN} --sl-testStage 'Testcafe tests' --sl-labId ${params.SL_LABID}
                sleep ${env.wait_time}
                """
        }
      }
    }
  }
}

