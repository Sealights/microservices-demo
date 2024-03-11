@Library('main-shared-library') _

pipeline {
   agent {
        kubernetes {
            yaml kubernetes.base_pod([
                    template_path: "ci/pod_templates/shell_pod.yaml",
                    base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-python:latest",
                    ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
                    memory_request: "300Mi",
                    memory_limit: "700Mi",
                    cpu_request: "0.5",
                    cpu_limit: "0.7",
                    node_selector: "monitor"
            ])
            defaultContainer 'shell'
        }
    }
    
    parameters {
        string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
        string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
        string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
        string(name: 'MACHINE_DNS1', defaultValue: '', description: 'machine dns')
    }
   
    
    
    stages{
        stage("Init test"){
            steps{
                script{
                git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'   
                }
            }
        }
        
        
        stage('download python agent and scanning tests') {
            steps{
                script{
                    sh """
                    pip install robotframework
                    pip install robotframework-requests
                    pip install sealights-python-agent
                    export machine_dns="${params.MACHINE_DNS1}"
                    
                    sl-python start --labid ${params.SL_LABID} --token ${params.SL_TOKEN} --teststage "Robot Tests"
                    robot -xunit integration-tests/robot-tests/api_tests.robot
                    sl-python uploadreports --reportfile "unit.xml" --labid ${params.SL_LABID} --token ${params.SL_TOKEN} 
                    sl-python end --labid ${params.SL_LABID} --token ${params.SL_TOKEN}
                    
                    
                    
                    """
                }
            }
        }
    }
}
