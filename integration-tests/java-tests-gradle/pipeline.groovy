@Library('main-shared-library') _

pipeline {
   agent {
        kubernetes {
            yaml kubernetes.base_pod([
                    template_path: "ci/pod_templates/shell_pod.yaml",
                    base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/jnlp-java:latest",
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
                dir("microservices-demo"){
                    git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
                    }
                }
            }
        }
        
        
        
        
        stage('download java cd-agent') {
            steps{
                script{
                    sh """
                    echo 'Downloading Sealights Agents...'
                    wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
                    unzip -o sealights-java-latest.zip
                    echo "Sealights agent version used is:" `cat sealights-java-version.txt`
                    export SL_TOKEN="${params.SL_TOKEN}"
                    echo $SL_TOKEN>sltoken.txt 
                    
                    echo  '{ 
                      "executionType": "testsonly",
                      "tokenFile": "./sltoken.txt",
                      "createBuildSessionId": false,
                      "testStage": "Junit without testNG-gradle",
                      "runFunctionalTests": true,
                      "labId": "${params.SL_LABID}",
                      "proxy": null,
                      "logEnabled": false,
                      "logDestination": "console",
                      "logLevel": "warn",
                      "sealightsJvmParams": {}
                      }' > slgradletests.json

                    echo "Adding Sealights to Tests Project gradle file..."
                    java -jar sl-build-scanner.jar -gradle -configfile slgradletests.json -workspacepath ./microservices-demo/integration-tests/java-tests-gradle
                    
                    """
                }
            }
        }
        
        
        
         stage('gradle clean build') {
            steps{
                script{
                    // Run the maven build
                    echo "machine_dns1 parameter value: ${params.MACHINE_DNS1}"
                    
                    sh '''
                        export machine_dns="${MACHINE_DNS1}"

                        cd microservices-demo/integration-tests/java-tests-gradle
                        ./gradlew clean build
                    '''
                }
            }
        }
    }
}