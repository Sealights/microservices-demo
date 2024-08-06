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
    choice(name: 'TECHNOLOGY', choices: ['All','dotnet','node'], description: 'Make your choice of BTQ running')
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
    string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
    string(name: 'MACHINE_DNS', defaultValue: 'http://10.2.11.97:8081', description: 'machine dns')
    booleanParam(name: 'Run_all_tests', defaultValue: true, description: 'Checking this box will run all tests even if individual ones are not checked')
    booleanParam(name: 'Cypress', defaultValue: false, description: 'Run tests using Cypress testing framework')
    booleanParam(name: 'MS', defaultValue: false, description: 'Run tests using MS testing framework')
    booleanParam(name: 'Cucumberjs', defaultValue: false, description: 'Run tests using Cucumberjs testing framework (maven)')
    booleanParam(name: 'NUnit', defaultValue: false, description: 'Run tests using NUnityour_dns testing framework')
    booleanParam(name: 'Junit_with_testNG_gradle', defaultValue: false, description: 'Run tests using Junit testing framework with testNG (gradle)')
    booleanParam(name: 'Robot', defaultValue: false, description: 'Run tests using Robot testing framework')
    booleanParam(name: 'Cucumber', defaultValue: false, description: 'Run tests using Cucumber testing framework (java)')
    booleanParam(name: 'Junit_with_testNG', defaultValue: false, description: 'Run tests using Junit testing framework with testNG (maven)')
    booleanParam(name: 'Junit_without_testNG', defaultValue: false, description: 'Run tests using Junit testing framework without testNG (maven)')
    booleanParam(name: 'Postman', defaultValue: false, description: 'Run tests using postman testing framework')
    booleanParam(name: 'Mocha', defaultValue: false, description: 'Run tests using Mocha testing framework')
    booleanParam(name: 'Soapui', defaultValue: false, description: 'Run tests using Soapui testing framework')
    booleanParam(name: 'Pytest', defaultValue: false, description: 'Run tests using Pytest testing framework')
    booleanParam(name: 'Karate', defaultValue: false, description: 'Run tests using Karate testing framework (maven)')
    booleanParam(name: 'long_test', defaultValue: false, description: 'Runs a long test for showing tia (not effected by run_all_tests flag)')
  }
  environment {
    MACHINE_DNS = "${params.MACHINE_DNS}"
    machine_dns = "${params.MACHINE_DNS}"
    GH_TOKEN = secrets.get_secret('mgmt/github_token', 'us-west-2')
  }
  stages {
    stage("Init test") {
      steps {
        script {
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
        }
      }
    }
    stage('MS-Tests framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.MS == true || params.TECHNOLOGY == 'dotnet') {
            sh """
                mkdir -p ./sealights/agent
                ls

                DOTNET_LATEST_VERSION=\$(gh release view --repo sealights/SL.OnPremise.Agents.DotNet --json tagName --jq '.tagName')
                gh release download \$DOTNET_LATEST_VERSION --repo sealights/SL.OnPremise.Agents.DotNet -D ./sealights/agent

                # Ensure the target directory exists and has proper permissions
                mkdir -p /app/sealights/agent
                chmod -R 755 /app/sealights

                unzip ./sealights/agent/sealights-dotnet-agent-linux-self-contained.zip -d /app/sealights/agent

                echo 'MS-Tests framework starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                dotnet /app/sealights/agent/SL.DotNet.dll startExecution --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN}
                sleep 10
                dotnet /app/sealights/agent/SL.DotNet.dll run --workingDir . --instrumentationMode tests --target dotnet   --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN} --targetArgs "test ./integration-tests/dotnet-tests/MS-Tests/"
                dotnet /app/sealights/agent/SL.DotNet.dll endExecution --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN}
            """
          }
        }
      }
    }
    stage('N-Unit framework starting') {
      steps {
        script {
          if (params.Run_all_tests == true || params.NUnit == true || params.TECHNOLOGY == 'dotnet') {
            sh """
                echo 'N-Unit framework starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                dotnet /app/sealights/agent/SL.DotNet.dll startExecution --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN}
                sleep 10
                dotnet /app/sealights/agent/SL.DotNet.dll run --workingDir . --instrumentationMode tests --target dotnet   --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN} --targetArgs "test ./integration-tests/dotnet-tests/NUnit-Tests/"
                sleep 10
                dotnet /app/sealights/agent/SL.DotNet.dll endExecution --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN}
              """
          }
        }
      }
    }
    stage('Cypress framework starting') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Cypress == true || params.TECHNOLOGY == 'node') {
            build(job: "BTQ-nodejs-tests-Cypress-framework", parameters: [string(name: 'BRANCH', value: "${params.BRANCH}"), string(name: 'SL_LABID', value: "${params.SL_LABID}"), string(name: 'SL_TOKEN', value: "${params.SL_TOKEN}"), string(name: 'MACHINE_DNS1', value: "${params.MACHINE_DNS}")])
          }
        }
      }
    }

    stage('Cucumberjs framework starting') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Cucumberjs == true || params.TECHNOLOGY == 'node') {
            sh """
                  echo 'Cucumberjs framework starting ..... '
                  cd integration-tests/Cucumber-js
                  echo ${env.SL_TOKEN}>sltoken.txt
                  npm install @cucumber/cucumber axios sealights-cucumber-plugin
                  export SL_PACKAGE=\$(node -p "require.resolve('sealights-cucumber-plugin')")
                  export machine_dns="${env.MACHINE_DNS}"
                  echo '{
                    "tokenfile": "sltoken.txt",
                    "labid": "${params.SL_LABID}",
                    "testStage": "CucumberJS-Tests"
                    }' > sl.conf
                  ./node_modules/.bin/slnodejs start --tokenfile ./sltoken.txt --labid ${params.SL_LABID} --teststage "CucumberJS-Tests"
                  node_modules/.bin/cucumber-js ./features --require \$SL_PACKAGE --require 'features/**/*.@(js|cjs|mjs)'
                  ./node_modules/.bin/slnodejs end --tokenfile ./sltoken.txt --labid ${params.SL_LABID}
                  sleep 10
                  """
          }
        }
      }
    }

//    stage('Karate framework') {
//      steps {
//        script {
//          if (params.Run_all_tests == true || params.Karate == true || params.TECHNOLOGY == 'java') {
//            sh """
//              #!/bin/bash
//              echo 'Karate framework starting ..... '
//              cd ./integration-tests/karate-tests/
//              echo ${env.SL_TOKEN}>sltoken.txt
//              echo  '{
//                "executionType": "testsonly",
//                "tokenFile": "./sltoken.txt",
//                "createBuildSessionId": false,
//                "testStage": "Karate-framework-java",
//                "runFunctionalTests": true,
//                "labId": "${params.SL_LABID}",
//                "proxy": null,
//                "logEnabled": false,
//                "logDestination": "console",
//                "logLevel": "info",
//                "sealightsJvmParams": {}
//                }' > slmaventests.json
//              echo "Adding Sealights to Tests Project POM file..."
//              java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .
//              mvn -q clean test -Dkarate.env=${env.MACHINE_DNS}
//              sleep 10
//            """
//          }
//        }
//      }
//    }
    stage('Junit without testNG ') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Junit_without_testNG == true || params.TECHNOLOGY == 'java') {
            sh """
            #!/bin/bash
            export lab_id="${params.SL_LABID}"
            echo 'Junit without testNG framework starting ..... '
            cd integration-tests/java-tests
            export SL_TOKEN="${params.SL_TOKEN}"
            echo $SL_TOKEN>sltoken.txt
            export machine_dns="${params.MACHINE_DNS}"
            # shellcheck disable=SC2016
            #add maven version inside echo maven and gradle
            echo  '{
                    "executionType": "testsonly",
                    "tokenFile": "./sltoken.txt",
                    "createBuildSessionId": false,
                    "testStage": "Junit-without-testNG",
                    "runFunctionalTests": true,
                    "labId": "${params.SL_LABID}",
                    "proxy": null,
                    "logEnabled": false,
                    "logDestination": "console",
                    "logLevel": "warn",
                    "sealightsJvmParams": {}
                    }' > slmaventests.json
            echo "Adding Sealights to Tests Project POM file..."
            java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .
            #mvn dependency:get -Dartifact=io.sealights.on-premise.agents.plugin:sealights-maven-plugin:4.0.103  -gs ./settings-github.xml
            mvn clean package
          """
          }
        }
      }
    }
    stage('Junit support testNG framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Junit_with_testNG == true || params.TECHNOLOGY == 'java') {
            sh """
                #!/bin/bash
                export lab_id="${params.SL_LABID}"
                echo 'Junit support testNG framework starting ..... '
                pwd
                ls
                cd ./integration-tests/support-testNG
                export SL_TOKEN="${params.SL_TOKEN}"
                echo $SL_TOKEN>sltoken.txt
                export machine_dns="${params.MACHINE_DNS}"
                # shellcheck disable=SC2016
                echo  '{
                        "executionType": "testsonly",
                        "tokenFile": "./sltoken.txt",
                        "createBuildSessionId": false,
                        "testStage": "Junit-support-testNG",
                        "runFunctionalTests": true,
                        "labId": "${params.SL_LABID}",
                        "proxy": null,
                        "logEnabled": false,
                        "logDestination": "console",
                        "logLevel": "warn",
                        "sealightsJvmParams": {}
                        }' > slmaventests.json
                echo "Adding Sealights to Tests Project POM file..."
                java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .
                mvn clean package
              """
          }
        }
      }
    }
    stage('Gradle framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Junit_without_testNG == true || params.TECHNOLOGY == 'java') {
            sh """
                #!/bin/bash
                export lab_id="${params.SL_LABID}"
                export machine_dns="${params.MACHINE_DNS}"
                cd ./integration-tests/java-tests-gradle
                echo $SL_TOKEN>sltoken.txt
                echo '{
                    "executionType": "testsonly",
                    "tokenFile": "./sltoken.txt",
                    "createBuildSessionId": false,
                    "testStage": "Junit-without-testNG-gradle",
                    "runFunctionalTests": true,
                    "labId": "${params.SL_LABID}",
                    "proxy": null,
                    "logEnabled": false,
                    "logDestination": "console",
                    "logLevel": "warn",
                    "sealightsJvmParams": {}
                }' > slgradletests.json
                echo "Adding Sealights to Tests Project gradle file..."
                java -jar /sealights/sl-build-scanner.jar -gradle -configfile slgradletests.json -workspacepath .
                gradle test
            """
          }
        }
      }
    }
    stage('Cucumber framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Cucumber == true || params.TECHNOLOGY == 'java') {
            sh """
                #!/bin/bash
                export lab_id="${params.SL_LABID}"
                export machine_dns="${params.MACHINE_DNS}"
                echo 'Cucumber framework starting ..... '
                cd ./integration-tests/cucumber-framework/
                echo ${params.SL_TOKEN}>sltoken.txt
                # shellcheck disable=SC2016
                echo  '{
                        "executionType": "testsonly",
                        "tokenFile": "./sltoken.txt",
                        "createBuildSessionId": false,
                        "testStage": "Cucmber-framework-java ",
                        "runFunctionalTests": true,
                        "labId": "${params.SL_LABID}",
                        "proxy": null,
                        "logEnabled": false,
                        "logDestination": "console",
                        "logLevel": "warn",
                        "sealightsJvmParams": {}
                        }' > slmaventests.json
                echo "Adding Sealights to Tests Project POM file..."
                java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .
                unset MAVEN_CONFIG
                ./mvnw test
              """
          }
        }
      }
    }

    stage('Mocha framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Mocha == true || params.TECHNOLOGY == 'node') {
            sh """
                echo 'Mocha framework starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                export Lab_id="${params.SL_LABID}"
                cd ./integration-tests/nodejs-tests/mocha
                cp -r /nodeModules/node_modules .
                npm install
                npm install slnodejs
                ./node_modules/.bin/slnodejs mocha --token "${params.SL_TOKEN}" --labid "${params.SL_LABID}" --teststage 'Mocha-tests'  --useslnode2 -- ./test/test.js --recursive --testTimeout=30000
                cd ../..
              """
          }
        }
      }
    }

//    stage('robot framework'){
//      steps{
//        script{
//          sh """
//                    echo "the env var is $machine_dns"
//                    export machine_dns="${params.MACHINE_DNS}"
//                    echo 'robot framework starting ..... '
//                    cd ./integration-tests/robot-tests
//                    sl-python start --labid ${SL_LABID} --token ${SL_TOKEN} --teststage "Robot-Tests"
//                    robot -xunit api_tests.robot
//                    sl-python uploadreports --reportfile "unit.xml" --labid ${SL_LABID} --token ${SL_TOKEN}
//                    sl-python end --labid ${SL_LABID} --token ${SL_TOKEN}
//                    cd ../..
//                    """
//        }
//      }
//    }
    stage('Postman framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Postman == true) {
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
    stage('Soap-UI framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Soapui == true) {
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
                export SL_TOKEN="${params.SL_TOKEN}"
                echo ${params.SL_TOKEN}>sltoken.txt
                echo  '{
                  "executionType": "testsonly",
                  "tokenFile": "./sltoken.txt",
                  "createBuildSessionId": false,
                  "testStage": "Soap-UI-framework",
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
                sed -i "s#machine_dns#${params.MACHINE_DNS}#" test-soapui-project.xml
                sed "s#machine_dns#${params.MACHINE_DNS}#" test-soapui-project.xml
                export SL_JAVA_OPTS="-javaagent:sl-test-listener.jar -Dsl.token=${params.SL_TOKEN} -Dsl.labId=${params.SL_LABID} -Dsl.testStage=Soapui-Tests -Dsl.log.enabled=true -Dsl.log.level=debug -Dsl.log.toConsole=true"
                sed -i -r "s/(^\\S*java)(.*com.eviware.soapui.tools.SoapUITestCaseRunner)/\\1 \\\$SL_JAVA_OPTS \\2/g" testrunner.sh
                sh -x ./testrunner.sh -s "TestSuite 1" "test-soapui-project.xml"
              """
          }
        }
      }
    }
    stage('Pytest framework') {
      steps {
        script {
          if (params.Run_all_tests == true || params.Pytest == true) {
            sh """
            export SL_SAVE_LOG_FILE=true
            echo 'Pytest tests starting ..... '
            export machine_dns="${params.MACHINE_DNS}"
            cd ./integration-tests/python-tests
            pip install pytest
            pip install requests
            sl-python pytest --teststage "Pytest-tests"  --labid ${params.SL_LABID} --token ${params.SL_TOKEN} python-tests.py
            ls
            cd ../..
          """
          }
        }
      }
    }
  }
}

