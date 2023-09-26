@Library('main-shared-library') _

pipeline {
   agent {
        kubernetes {
            yaml kubernetes.base_pod([
                    template_path: "ci/pod_templates/shell_pod.yaml",
                    base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ui-ci:latest",
                    ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
                    memory_request: "500Mi",
                    memory_limit: "2000Mi",
                    cpu_request: "0.5",
                    cpu_limit: "1",
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
        
        
        stage('download NodeJs agent and scanning Cypress tests') {
            steps{
                script{
                    sh """
                    cd integration-tests/cypress/
                    npm install 
                    export CYPRESS_SL_TEST_STAGE="Cypress-Test-Stage"
                    export MACHINE_DNS="${params.MACHINE_DNS1}" 
                    export CYPRESS_SL_LAB_ID="${params.SL_LABID}"
                    export CYPRESS_SL_TOKEN="${params.SL_TOKEN}"
                    npx cypress run --spec "cypress/integration/api.spec.js" 
                    """
                }
            }
        }
    }
    
}
