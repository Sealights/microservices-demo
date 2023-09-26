@Library('main-shared-library') _

pipeline {
   agent {
        kubernetes {
            yaml kubernetes.base_pod([
                    template_path: "ci/pod_templates/shell_pod.yaml",
                    base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-java-agent-11:latest",
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
        
        
        
        
        stage('download java cd-agent') {
            steps{
                script{
                    sh """
                    wget https://dl.eviware.com/soapuios/5.7.1/SoapUI-5.7.1-mac-bin.zip
                    unzip SoapUI-5.7.1-mac-bin.zip
                    
                     ls 
                    
                    cp integration-tests/soapUI/test-soapui-project.xml SoapUI-5.7.1/bin
                    
                    cd SoapUI-5.7.1/bin
                    
                    
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
                      "testStage": "Cucmber framework",
                      "runFunctionalTests": true,
                      "labId": "${params.SL_LABID}",
                      "proxy": null,
                      "logEnabled": false,
                      "logDestination": "console",
                      "logLevel": "warn",
                      "sealightsJvmParams": {}
                      }' > slmaventests.json
                     
                    echo "Adding Sealights to Tests Project POM file..."
                      
                   
                    pwd
                    
                    #cp ${WORKSPACE}/sl-test-listener.jar integration-tests/soapUI
                    
                    #cd integration-tests/soapUI
                    
                    ls 
                    
                    #export LAB_ID="${params.SL_LABID}"
                    #export TOKEN="${params.SL_TOKEN}"
                    
                    sed -i "s#machine_dns#${params.MACHINE_DNS1}#" test-soapui-project.xml
                    sed "s#machine_dns#${params.MACHINE_DNS1}#" test-soapui-project.xml
                     
                    #export JAVA_TOOL_OPTIONS="-javaagent:sl-test-listener.jar -Dsl.token=${params.SL_TOKEN} -Dsl.labId=${params.SL_LABID} -Dsl.testStage=Soapui-Tests"
                    export SL_JAVA_OPTS="-javaagent:sl-test-listener.jar -Dsl.token=${params.SL_TOKEN} -Dsl.labId=${params.SL_LABID} -Dsl.testStage=Soapui-Tests -Dsl.log.enabled=true -Dsl.log.level=debug -Dsl.log.toConsole=true"
                     
                    sed -i -r "s/(^\\S*java)(.*com.eviware.soapui.tools.SoapUITestCaseRunner)/\\1 \\\$SL_JAVA_OPTS \\2/g" testrunner.sh
                    
                    sh -x ./testrunner.sh -s "TestSuite 1" "test-soapui-project.xml"
                    
                    """
                }
            }
        }
    }
}