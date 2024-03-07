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
  }
  stages{
    stage("Init test"){
      steps{
        script{
          git branch: params.BRANCH
        }
      }
    }
  
   stage('robot framework'){
     steps{
       script{
         sh """
                   echo "the env var is $machine_dns"
                   export machine_dns="${params.MACHINE_DNS}"
                   echo 'robot framework starting ..... '
                   cd ./integration-tests/robot-tests
                   sl-python start --labid ${SL_LABID} --token ${SL_TOKEN} --teststage "Robot-Tests"
                   robot -xunit api_tests.robot
                   sl-python uploadreports --reportfile "unit.xml" --labid ${SL_LABID} --token ${SL_TOKEN}
                   sl-python end --labid ${SL_LABID} --token ${SL_TOKEN}
                   cd ../..
                   """
       }
     }
   }
  }
}

