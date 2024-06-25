@Library('main-shared-library@node_plugins_ci') _

pipeline {
   agent {
        kubernetes {
            yaml kubernetes.base_pod([
                    template_path: "ci/pod_templates/shell_pod.yaml",
                    base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ui-ci:latest",
                    ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
                     memory_request: "1000Mi",
                    memory_limit: "3000Mi",
                    cpu_request: "1",
                    cpu_limit: "1.5",
                    node_selector: "jenkins"
            ])
            defaultContainer 'shell'
        }
    }
    
    parameters {
        string(name: 'BRANCH', defaultValue: 'slnodejs', description: 'Branch to clone (ahmad-branch)')
        string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
        string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
        string(name: 'MACHINE_DNS1', defaultValue: '', description: 'machine dns')
        string(name: 'CYPRESS_VERSION', defaultValue: '', description: 'Please enter the Cypress version to use')
        booleanParam(name: 'node_ci', defaultValue: false, description: 'Download package from github packages')
        
        
    }
    options{
        buildDiscarder logRotator(numToKeepStr: '20')
        timestamps()
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
                    github.set_github_registries()
                    sh """
                    cd integration-tests/cypress/
                    if [ "${params.node_ci}" = true ]; then
                        npm install @sealights/sealights-cypress-plugin --registry=https://npm.pkg.github.com
                    else
                        npm install sealights-cypress-plugin
                    fi
                    npm install 
                    export NODE_DEBUG=sl
                    export CYPRESS_SL_ENABLE_REMOTE_AGENT=false
                    export CYPRESS_SL_TEST_STAGE="Cypress-Test-Stage"
                    export MACHINE_DNS="${params.MACHINE_DNS1}" 
                    export CYPRESS_machine_dns="${params.MACHINE_DNS1}" 
                    export CYPRESS_SL_LAB_ID="${params.SL_LABID}"
                    export CYPRESS_SL_TOKEN="${params.SL_TOKEN}"

                    npx cypress run --spec "cypress/integration/api.spec.js"

                    """
                }
            }
        }
    }
    
}
