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
    booleanParam(name: 'Run_all_tests', defaultValue: true, description: 'Checking this box will run all tests even if individual ones are not checked')
    booleanParam(name: 'Cucumber', defaultValue: false, description: 'Run tests using Cucumber testing framework (java)')
    booleanParam(name: 'Cypress', defaultValue: false, description: 'Run tests using Cypress testing framework')
    booleanParam(name: 'Junit_with_testNG', defaultValue: false, description: 'Run tests using Junit testing framework with testNG (maven)')
    booleanParam(name: 'Junit_without_testNG', defaultValue: false, description: 'Run tests using Junit testing framework without testNG (maven)')
    booleanParam(name: 'Junit_with_testNG_gradle', defaultValue: false, description: 'Run tests using Junit testing framework with testNG (gradle)')
    booleanParam(name: 'Mocha', defaultValue: false, description: 'Run tests using Mocha testing framework')
    booleanParam(name: 'MS', defaultValue: false, description: 'Run tests using MS testing framework')
    booleanParam(name: 'NUnit', defaultValue: false, description: 'Run tests using NUnityour_dns testing framework')
    booleanParam(name: 'Postman', defaultValue: false, description: 'Run tests using postman testing framework')
    booleanParam(name: 'Pytest', defaultValue: false, description: 'Run tests using Pytest testing framework')
    booleanParam(name: 'Robot', defaultValue: false, description: 'Run tests using Robot testing framework')
    booleanParam(name: 'Soapui', defaultValue: false, description: 'Run tests using Soapui testing framework')
    booleanParam(name: 'long_test', defaultValue: false, description: 'Runs a long test for showing tia (not effected by run_all_tests flag)')

  }
  environment {
    MACHINE_DNS = "${params.MACHINE_DNS}"
    machine_dns = "${params.MACHINE_DNS}"
  }
  stages{
    stage("Init test"){
      steps{
        script{
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
        }
      }
    }
    stage('Cypress framework starting'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Cypress == true) {
            build(job:"BTQ-nodejs-tests-Cypress-framework", parameters: [string(name: 'BRANCH', value: "${params.BRANCH}"),string(name: 'SL_LABID', value: "${params.SL_LABID}") , string(name:'SL_TOKEN' , value:"${params.SL_TOKEN}") ,string(name:'MACHINE_DNS1' , value:"${params.MACHINE_DNS}")])
          }
        }
      }
    }

    stage('MS-Tests framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.MS == true) {
              sh """
                echo 'MS-Tests framework starting ..... '
                export machine_dns="${env.MACHINE_DNS}" # Inside the code we use machine_dns envronment variable
                dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll startExecution --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${env.SL_TOKEN}
                dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll run --workingDir . --instrumentationMode tests --target dotnet   --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${env.SL_TOKEN} --targetArgs "test ./integration-tests/dotnet-tests/MS-Tests/"
                dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll endExecution --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${env.SL_TOKEN}
                sleep ${env.wait_time} # Wait at least 10 seconds for the backend to update status that the previous test stage was closed, closing and starting a test stage withing 10 seconds can cause inaccurate test stage coverage
                """
          }
        }
      }
    }


    stage('N-Unit framework starting'){
      steps{
        script{
          if( params.Run_all_tests == true || params.NUnit == true) {
            sh """
                  echo 'N-Unit framework starting ..... '
                  export machine_dns="${env.MACHINE_DNS}"
                  dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll startExecution --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${env.SL_TOKEN}
                  dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll run --workingDir . --instrumentationMode tests --target dotnet   --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${env.SL_TOKEN} --targetArgs "test ./integration-tests/dotnet-tests/NUnit-Tests/"
                  dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll endExecution --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${env.SL_TOKEN}
                  sleep ${env.wait_time}
                  """
          }
        }
      }
    }


    stage('Gradle'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Junit_with_testNG_gradle == true) {
            sh """
                      #!/bin/bash
                      export machine_dns="${env.MACHINE_DNS}"
                      cd ./integration-tests/java-tests-gradle
                      echo ${env.SL_TOKEN}>sltoken.txt
                      echo '{
                          "executionType": "testsonly",
                          "tokenFile": "./sltoken.txt",
                          "createBuildSessionId": false,
                          "testStage": "Junit without testNG-gradle",
                          "runFunctionalTests": true,
                          "labId": "${params.SL_LABID}",
                          "proxy": null,
                          "logEnabled": false,
                          "logDestination": "console",
                          "logLevel": "info",
                          "sealightsJvmParams": {}
                      }' > slgradletests.json


                      echo "Adding Sealights to Tests Project gradle file..."
                      java -jar /sealights/sl-build-scanner.jar -gradle -configfile slgradletests.json -workspacepath .
                      gradle test
                      sleep ${env.wait_time}
                      """
          }
        }
      }
    }
    stage('robot framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Robot == true) {
            sh """
                      pip install robotframework && pip install robotframework-requests
                      export machine_dns="${env.MACHINE_DNS}"
                      echo 'robot framework starting ..... '
                      cd ./integration-tests/robot-tests
                      sl-python start --labid ${SL_LABID} --token ${SL_TOKEN} --teststage "Robot Tests"
                      robot -xunit api_tests.robot
                      sl-python uploadreports --reportfile "unit.xml" --labid ${SL_LABID} --token ${SL_TOKEN}
                      sl-python end --labid ${SL_LABID} --token ${SL_TOKEN}
                      cd ../..
                      sleep ${env.wait_time}
                      """
          }
        }
      }
    }

    stage('Cucumber framework') {
      steps{
        script{
          if( params.Run_all_tests == true || params.Cucumber == true) {
            sh """
                      #!/bin/bash
                      export machine_dns="${env.MACHINE_DNS}"
                      echo 'Cucumber framework starting ..... '
                      cd ./integration-tests/cucumber-framework/
                      echo ${env.SL_TOKEN}>sltoken.txt
                      # shellcheck disable=SC2016
                      echo  '{
                              "executionType": "testsonly",
                              "tokenFile": "./sltoken.txt",
                              "createBuildSessionId": false,
                              "testStage": "Cucmber framework java ",
                              "runFunctionalTests": true,
                              "labId": "${params.SL_LABID}",
                              "proxy": null,
                              "logEnabled": false,
                              "logDestination": "console",
                              "logLevel": "info",
                              "sealightsJvmParams": {}
                              }' > slmaventests.json
                      echo "Adding Sealights to Tests Project POM file..."
                      java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .

                      unset MAVEN_CONFIG
                      ./mvnw test
                      sleep ${env.wait_time}
                      """
          }
        }
      }
    }



    stage('Junit support testNG framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Junit_with_testNG == true) {
            sh """
                      #!/bin/bash
                      echo 'Junit support testNG framework starting ..... '
                      pwd
                      ls
                      cd ./integration-tests/support-testNG
                      export SL_TOKEN="${env.SL_TOKEN}"
                      echo $SL_TOKEN>sltoken.txt
                      export machine_dns="${env.MACHINE_DNS}"
                      # shellcheck disable=SC2016
                      echo  '{
                              "executionType": "testsonly",
                              "tokenFile": "./sltoken.txt",
                              "createBuildSessionId": false,
                              "testStage": "Junit support testNG",
                              "runFunctionalTests": true,
                              "labId": "${params.SL_LABID}",
                              "proxy": null,
                              "logEnabled": false,
                              "logDestination": "console",
                              "logLevel": "info",
                              "sealightsJvmParams": {}
                              }' > slmaventests.json
                      echo "Adding Sealights to Tests Project POM file..."
                      java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .
                      mvn clean package
                      sleep ${env.wait_time}
                      """
          }
        }
      }
    }


    stage('Junit without testNG '){
      steps{
        script{
          if( params.Run_all_tests == true || params.Junit_without_testNG == true) {
            sh """
                      #!/bin/bash
                      echo 'Junit without testNG framework starting ..... '
                      pwd
                      ls
                      cd integration-tests/java-tests
                      export SL_TOKEN="${env.SL_TOKEN}"
                      echo $SL_TOKEN>sltoken.txt
                      export machine_dns="${env.MACHINE_DNS}"
                      # shellcheck disable=SC2016
                      echo  '{
                              "executionType": "testsonly",
                              "tokenFile": "./sltoken.txt",
                              "createBuildSessionId": false,
                              "testStage": "Junit without testNG",
                              "runFunctionalTests": true,
                              "labId": "${params.SL_LABID}",
                              "proxy": null,
                              "logEnabled": false,
                              "logDestination": "console",
                              "logLevel": "info",
                              "sealightsJvmParams": {}
                              }' > slmaventests.json
                      echo "Adding Sealights to Tests Project POM file..."
                      java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .

                      mvn clean package
                      sleep ${env.wait_time}
                      """
          }
        }
      }
    }


    stage('Postman framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Postman == true) {
            sh """
                    export MACHINE_DNS="${env.MACHINE_DNS}"
                    cd ./integration-tests/postman-tests/
                    npm i slnodejs
                    npm install newman
                    npm install newman-reporter-xunit
                    echo 'Postman framework starting ..... '
                    ./node_modules/.bin/slnodejs start --labid ${params.SL_LABID} --token ${env.SL_TOKEN} --teststage "postman tests"
                    npx newman run sealights-excersise.postman_collection.json --env-var machine_dns="${env.MACHINE_DNS}" -r xunit --reporter-xunit-export './result.xml' --suppress-exit-code
                    ./node_modules/.bin/slnodejs uploadReports --labid ${params.SL_LABID} --token ${env.SL_TOKEN} --reportFile './result.xml'
                    ./node_modules/.bin/slnodejs end --labid ${params.SL_LABID} --token ${env.SL_TOKEN}
                    cd ../..
                    sleep ${env.wait_time}
                    """
          }
        }
      }
    }


    // stage('Jest framework'){
    //   steps{
    //     script{
    //
    //       sh """
    //             echo 'Jest framework starting ..... '
    //             export machine_dns="${env.MACHINE_DNS}"
    //             cd ./integration-tests/nodejs-tests/Jest
    //             npm install jest && npm install jest-cli && npm install sealights-jest-plugin
    //             export NODE_DEBUG=sl
    //             export SL_TOKEN="${env.SL_TOKEN}"
    //             export SL_LABID="${params.SL_LABID}"
    //             npm install
    //             npx jest integration-tests/nodejs-tests/Jest/test.js --sl-testStage='Jest tests' --sl-token="${env.SL_TOKEN}" --sl-labId="${params.SL_LABID}"
    //             cd ../..
    //             sleep ${env.wait_time}
    //             """
    //     }
    //   }
    // }



    stage('Mocha framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Mocha == true) {
            sh """
                      export machine_dns="${env.MACHINE_DNS}"
                      cd ./integration-tests/nodejs-tests/mocha
                      npm install
                      npm install slnodejs
                      echo 'Mocha framework starting ..... '
                      ./node_modules/.bin/slnodejs mocha --token "${env.SL_TOKEN}" --labid "${params.SL_LABID}" --teststage 'Mocha tests'  --useslnode2 -- ./test/test.js --recursive --no-timeouts
                      cd ../..
                      sleep ${env.wait_time}
                      """
          }
        }
      }
    }



    stage('Soap-UI framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Soapui == true) {
            sh """
              echo 'Soap-UI framework starting ..... '
              wget https://dl.eviware.com/soapuios/5.7.1/SoapUI-5.7.1-mac-bin.zip
              unzip SoapUI-5.7.1-mac-bin.zip
              cp integration-tests/soapUI/test-soapui-project.xml SoapUI-5.7.1/bin
              cd SoapUI-5.7.1/bin
              echo 'Downloading Sealights Agents...'
              wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
              unzip -o sealights-java-latest.zip
              echo "Sealights agent version used is:" `cat sealights-java-version.txt`
              export SL_TOKEN="${env.SL_TOKEN}"
              echo ${env.SL_TOKEN}>sltoken.txt
              echo  '{
                "executionType": "testsonly",
                "tokenFile": "./sltoken.txt",
                "createBuildSessionId": false,
                "testStage": "Soap-UI framework",
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
              sed -i "s#machine_dns#${env.MACHINE_DNS}#" test-soapui-project.xml
              sed "s#machine_dns#${env.MACHINE_DNS}#" test-soapui-project.xml
              export SL_JAVA_OPTS="-javaagent:sl-test-listener.jar -Dsl.token=${env.SL_TOKEN} -Dsl.labId=${params.SL_LABID} -Dsl.testStage=Soapui-Tests -Dsl.log.enabled=true -Dsl.log.level=debug -Dsl.log.toConsole=true"
              sed -i -r "s/(^\\S*java)(.*com.eviware.soapui.tools.SoapUITestCaseRunner)/\\1 \\\$SL_JAVA_OPTS \\2/g" testrunner.sh
              sh -x ./testrunner.sh -s "TestSuite 1" "test-soapui-project.xml"
              sleep ${env.wait_time}
              """
          }
        }
      }
    }
    stage('Pytest framework'){
      steps{
        script{
          if( params.Run_all_tests == true || params.Pytest == true) {
            sh"""
                  pip install pytest && pip install requests
                  echo 'Pytest tests starting ..... '
                  export machine_dns="${env.MACHINE_DNS}"
                  cd ./integration-tests/python-tests
                  pip install pytest
                  pip install requests
                  sl-python pytest --teststage "Pytest tests"  --labid ${params.SL_LABID} --token ${env.SL_TOKEN} python-tests.py
                  cd ../..
                  sleep ${env.wait_time}
                  """
          }
        }
      }
    }
  }
}

