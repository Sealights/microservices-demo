@Library('main-shared-library') _
pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/CI_shell_pod.yaml')
      defaultContainer "shell"
    }
  }
  parameters {
    choice(name: 'BTQ_RUNNING', choices: ['BTQ','line coverage', 'BTQ + line coverage'], description: 'Make your choice of BTQ running')
    choice(name: 'BTQ_RUNNING_TECHNOLOGY', choices: ['All','dotnet','node'], description: 'Make your choice of BTQ running')
    string(name: 'lab', defaultValue: 'dev-integration.dev', description: 'name of the lab/environment for the lab id, default https://dev-integration.dev.sealights.co')
    string(name: 'APP_NAME', defaultValue: 'ahmad-BTQ', description: 'name of the app (integration build)')
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
    string(name: 'CHANGED_BRANCH', defaultValue: 'changed1', description: 'Branch to clone (ahmad-branch)')
    booleanParam(name: 'enable_dd', defaultValue: false, description: 'This parameter is used for enable Datadog agent')
    string(name: 'BUILD_BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to Build images that have the creational LAB_ID (send to ahmad branch to build)')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'sl-token')
    string(name: 'BUILD_NAME', defaultValue: 'ahmad-1', description: 'build name')
    string(name: 'JAVA_AGENT_URL', defaultValue: 'https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent_alpine.tar.gz', description: 'use different java agent')
    string(name: 'DOTNET_AGENT_URL', defaultValue: 'https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz', description: 'use different dotnet agent')
    string(name: 'NODE_AGENT_URL', defaultValue: 'slnodejs', description: 'use different node agent')
    string(name: 'GO_AGENT_URL', defaultValue: 'https://agents.sealights.co/slgoagent/latest/slgoagent-linux-amd64.tar.gz', description: 'use different go agent')
    string(name: 'GO_SLCI_AGENT_URL', defaultValue: 'https://agents.sealights.co/slcli/latest/slcli-linux-amd64.tar.gz', description: 'use different slci go agent')
    string(name: 'PYTHON_AGENT_URL', defaultValue: 'sealights-python-agent', description: 'use different python agent')
    choice(name: 'TEST_TYPE', choices: ['All Tests IN One Image', 'Tests sequential', 'Tests parallel'], description: 'Choose test type')
    string(name: 'SEALIGHTS_ENV_NAME', defaultValue: 'dev-integration',description: 'your environment name')
    string(name: 'LAB_UNDER_TEST',defaultValue: 'https://dev-integration.dev.sealights.co/api',description: 'The lab you want to test\nE.g. "https://dev-keren-gw.dev.sealights.co/api"')
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
    string(name: 'coverageThreshold', defaultValue: '10', description: 'coverageThreshold (line coverage)')
    string(name: 'lineThreshold', defaultValue: '5', description: 'lineThreshold (line coverage)')
    string(name: 'email', defaultValue: "integration@sealights.io", description: 'lab account email (for api token in the lab)-(line coverage)')
    string(name: 'password', defaultValue: "", description: 'lab account password (for api token in the lab)-(line coverage)')
  }

  stages {
    stage('Enabling line coverage for agent'){
      steps {
        script {
          if (params.BTQ_RUNNING == 'line coverage' || params.BTQ_RUNNING == 'BTQ + line coverage') {
            try {
              env.email = "${params.email}" == "" ? "${secrets.get_secret_map('mgmt/integ_account', 'us-west-2').Username}" : "${params.email}"
              env.password = "${params.password}" == "" ? "${secrets.get_secret_map('mgmt/integ_account', 'us-west-2').Password}" : "${params.password}"
              def AUTH_RESPONSE = sh(returnStdout: true, script: """
              curl -X POST \
                -d '{"email": "${env.email}", "password": "${env.password}"}' \
                -H "Content-Type: application/json" \
                https://dev-integration.dev.sealights.co/api/v2/auth/token
              """).trim()
              env.api_token = sh(returnStdout: true, script: "echo '${AUTH_RESPONSE}' | jq -r .token").trim()
              echo "api token : ${env.api_token}"
              def RESPONSE = sh(returnStdout: true, script: """
                curl -X PUT \
                -H "Authorization: Bearer "${env.api_token}"" \
                -H "Content-Type: application/json" \
                -d '{
                    "key": "BuildMethodology",
                    "value": "MethodLines"
                }' \
                https://dev-integration.dev.sealights.co/api/v1/settings/build-preferences/apps/${params.APP_NAME}/branches/${params.BRANCH}
            """).trim()
              if ("${RESPONSE}" != "200" && "${RESPONSE}" != "201") {
                return false
              }
              return true
            } catch (err) {
              error "Failed to Enabling line coverage for agent"
            }
          }
        }
      }
    }

    stage('Enabling line coverage for coverage apis'){
      steps {
        script {
          if (params.BTQ_RUNNING == 'line coverage' || params.BTQ_RUNNING == 'BTQ + line coverage') {
            try {
              def RESPONSE = (sh(returnStdout: true, script: """
              curl -X PUT \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer "${env.api_token}"" \
                -d '{
                  "appName": "${params.APP_NAME}",
                  "branchName": "${params.BRANCH}",
                  "coverageThreshold": "${params.coverageThreshold}",
                  "lineThreshold": "${params.lineThreshold}",
                  "showLineCoverage": true
                }' \
                https://dev-integration.dev.sealights.co/api/v2/coverage-settings
              """)).trim()
              if ("${RESPONSE}" != "200" && "${RESPONSE}" != "201") {
                return false
              }
              return true
            } catch (err) {
              error "Failed to Enabling line coverage for coverage apis"
            }
          }
        }
      }
    }

    //Build parallel images
    stage('Build BTQ') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'line coverage' || params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            env.token = "${params.SL_TOKEN}" == '' ? "${secrets.get_secret('mgmt/layer_token', 'us-west-2')}" : "${params.SL_TOKEN}"
            echo "${env.token}"
            def MapUrl = new HashMap()
            MapUrl.put('JAVA_AGENT_URL', "${params.JAVA_AGENT_URL}")
            MapUrl.put('DOTNET_AGENT_URL', "${params.DOTNET_AGENT_URL}")
            MapUrl.put('NODE_AGENT_URL', "${params.NODE_AGENT_URL}")
            MapUrl.put('GO_AGENT_URL', "${params.GO_AGENT_URL}")
            MapUrl.put('GO_SLCI_AGENT_URL', "${params.GO_SLCI_AGENT_URL}")
            MapUrl.put('PYTHON_AGENT_URL', "${params.PYTHON_AGENT_URL}")

            build_btq(
              sl_report_branch: params.BRANCH,
              sl_token: env.token,
              dev_integraion_sl_token: env.DEV_INTEGRATION_SL_TOKEN,
              build_name: "1-0-${BUILD_NUMBER}",
              branch: params.BRANCH,
              mapurl: MapUrl
            )
          }
        }
      }
    }

    stage('Spin-Up BTQ') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'line coverage' || params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

            def IDENTIFIER = "${params.BRANCH}-${env.CURRENT_VERSION}"
            SpinUpBoutiqeEnvironment(
              enable_dd: params.enable_dd,
              IDENTIFIER: IDENTIFIER,
              branch: params.BRANCH,
              app_name: params.APP_NAME,
              build_branch: params.BUILD_BRANCH,
              java_agent_url: params.JAVA_AGENT_URL,
              dotnet_agent_url: params.DOTNET_AGENT_URL,
              sl_branch: params.BRANCH,
              git_branch: params.BUILD_BRANCH ,
              lab : params.lab
            )
          }
        }
      }
    }

    stage('Full Run') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'line coverage' || params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def testStages_list =
              ["Cucmber-framework-java",
               "Jest-tests",
               "Junit-support-testNG",
               "Cypress-Test-Stage",
               "Junit-without-testNG",
               "Mocha-tests",
               "MS-Tests",
               "NUnit-Tests",
               "Postman-tests",
               "Pytest-tests",
               "Robot-Tests",
               "Soapui-Tests",
               "Junit-without-testNG-gradle",
               "Karate-framework-java"]

            testStages_list.each { TEST_STAGE ->
              schedule_full_run(
                app_name: URLEncoder.encode("${params.APP_NAME}", "UTF-8"),
                branch_name: URLEncoder.encode("${params.BRANCH}", "UTF-8"),
                test_stage: "${TEST_STAGE}",
                token: "${env.token}",
                machine: "dev-integration.dev.sealights.co"
              )
            }
          }
        }
      }
    }

    stage('Run Tests') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'line coverage' || params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            run_tests(
              technology : params.BTQ_RUNNING_TECHNOLOGY,
              branch: params.BRANCH,
              test_type: params.TEST_TYPE,
              Run_all_tests: params.Run_all_tests,
              Cucumber: params.Cucumber,
              Cypress: params.Cypress,
              Cucumberjs: params.Cucumberjs,
              Junit_with_testNG: params.Junit_with_testNG,
              Junit_without_testNG: params.Junit_without_testNG,
              Junit_with_testNG_gradle: params.Junit_with_testNG_gradle,
              Mocha: params.Mocha,
              MS: params.MS,
              NUnit: params.NUnit,
              Postman: params.Postman,
              Pytest: params.Pytest,
              Robot: params.Robot,
              Soapui: params.Soapui,
              Karate: params.Karate
            )
          }
        }
      }
    }

    stage('Run TIA Tests 1-FIRST With SeaLights') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def RUN_DATA = "full-run";
            TIA_Page_Tests(
              technology : params.BTQ_RUNNING_TECHNOLOGY,
              SEALIGHTS_ENV_NAME: params.SEALIGHTS_ENV_NAME,
              LAB_UNDER_TEST: params.LAB_UNDER_TEST,
              run_data: RUN_DATA,
              branch: params.BRANCH,
              app_name: params.APP_NAME
            )

          }
        }
      }
    }

    stage('Run Coverage Tests Before Changes') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def RUN_DATA = "without-changes";
            run_api_tests_before_changes(
              technology : params.BTQ_RUNNING_TECHNOLOGY,
              SEALIGHTS_ENV_NAME: params.SEALIGHTS_ENV_NAME,
              LAB_UNDER_TEST: params.LAB_UNDER_TEST,
              run_data: RUN_DATA,
              integration_branch: params.BRANCH,
              app_name: params.APP_NAME
            )
          }
        }
      }
    }
    stage('Run TIA Test VALIDATION without SeaLights BEFORE TIA') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ' || params.TECHNOLOGY == 'All') {
            def RUN_DATA = "full-run";
            run_TIA_ON_testresult(
              SEALIGHTS_ENV_NAME: params.SEALIGHTS_ENV_NAME,
              LAB_UNDER_TEST: params.LAB_UNDER_TEST,
              run_data: RUN_DATA,
              branch: params.BRANCH,
              lab_id: env.LAB_ID,
              app_name: params.APP_NAME
            )
          }
        }
      }
    }

    stage('Changed - Clone Repository') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            clone_repo(
              branch: params.CHANGED_BRANCH
            )
          }
        }
      }
    }

    stage('Changed Build BTQ') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def MapUrl = new HashMap()
            MapUrl.put('JAVA_AGENT_URL', "${params.JAVA_AGENT_URL}")
            MapUrl.put('DOTNET_AGENT_URL', "${params.DOTNET_AGENT_URL}")
            MapUrl.put('NODE_AGENT_URL', "${params.NODE_AGENT_URL}")
            MapUrl.put('GO_AGENT_URL', "${params.GO_AGENT_URL}")
            MapUrl.put('GO_SLCI_AGENT_URL', "${params.GO_SLCI_AGENT_URL}")
            MapUrl.put('PYTHON_AGENT_URL', "${params.PYTHON_AGENT_URL}")

            build_btq(
              sl_token: env.token,
              sl_report_branch: params.BRANCH,
              dev_integraion_sl_token: env.DEV_INTEGRATION_SL_TOKEN,
              build_name: "1-0-${BUILD_NUMBER}-v2",
              branch: params.CHANGED_BRANCH,
              mapurl: MapUrl
            )
          }
        }
      }
    }




    stage('Changed Spin-Up BTQ') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def IDENTIFIER = "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"

            SpinUpBoutiqeEnvironment(
              IDENTIFIER: IDENTIFIER,
              branch: params.BRANCH,
              git_branch: params.CHANGED_BRANCH,
              app_name: params.APP_NAME,
              build_branch: params.BRANCH,
              java_agent_url: params.JAVA_AGENT_URL,
              dotnet_agent_url: params.DOTNET_AGENT_URL,
              sl_branch: params.BRANCH,
              lab : params.lab
            )
          }
        }
      }
    }

    stage('Changed Run Tests') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            run_tests(
              technology : params.BTQ_RUNNING_TECHNOLOGY,
              Run_all_tests: params.Run_all_tests,
              branch: params.BRANCH,
              test_type: params.TEST_TYPE
            )
          }
        }
      }
    }

    stage('Run TIA Tests 2-SECOND With SeaLights') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def RUN_DATA = "TIA-RUN";
            TIA_Page_Tests(
              technology : params.BTQ_RUNNING_TECHNOLOGY,
              SEALIGHTS_ENV_NAME: params.SEALIGHTS_ENV_NAME,
              LAB_UNDER_TEST: params.LAB_UNDER_TEST,
              run_data: RUN_DATA,
              branch: params.BRANCH,
              app_name: params.APP_NAME
            )
          }
        }
      }
    }

    stage('Run Coverage Tests After Changes') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ') {
            def RUN_DATA = "with-changes";
            run_api_tests_after_changes(
              technology : params.BTQ_RUNNING_TECHNOLOGY,
              SEALIGHTS_ENV_NAME: params.SEALIGHTS_ENV_NAME,
              LAB_UNDER_TEST: params.LAB_UNDER_TEST,
              run_data: RUN_DATA,
              integration_branch: params.BRANCH,
              app_name: params.APP_NAME
            )
          }
        }
      }
    }

    stage('Run TIA Test VALIDATION without SeaLights AFTER TIA') {
      steps {
        script {
          if (params.BTQ_RUNNING == 'BTQ + line coverage' || params.BTQ_RUNNING == 'BTQ' || params.TECHNOLOGY == 'All') {
            def RUN_DATA = "TIA-RUN";
            run_TIA_ON_testresult(
              SEALIGHTS_ENV_NAME: params.SEALIGHTS_ENV_NAME,
              LAB_UNDER_TEST: params.LAB_UNDER_TEST,
              run_data: RUN_DATA,
              branch: params.BRANCH,
              lab_id: env.LAB_ID,
              app_name: params.APP_NAME
            )
          }
        }
      }
    }
  }

  post {
    success {
      script {
        success_btq(
          IDENTIFIER : "${params.BRANCH}-${env.CURRENT_VERSION}"
        )
        success_btq(
          IDENTIFIER : "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"
        )
      }
    }
    failure {
      script {
        set_assume_role([
          env       : "dev",
          account_id: "159616352881",
          role_name : "CD-TF-Role"
        ])
        failure_btq(
          IDENTIFIER : "${params.BRANCH}-${env.CURRENT_VERSION}"
        )
        failure_btq(
          IDENTIFIER : "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"
        )
      }
    }
  }
}

def build_btq(Map params){
  env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

  def parallelLabs = [:]
  //List of all the images name
  env.TOKEN= "${params.sl_token}"

  def services_list = ["adservice","cartservice","checkoutservice", "currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice","sealightservice"]
  //def special_services = ["cartservice"].
  env.BUILD_NAME= "${params.build_name}" == "" ? "${params.branch}-${env.CURRENT_VERSION}" : "${params.build_name}"

  services_list.each { service ->
    parallelLabs["${service}"] = {
      def AGENT_URL = getParamForService(service , params.mapurl)
      build(job: "BTQ-BUILD/${params.branch}", parameters: [string(name: 'SERVICE', value: "${service}"),
                                                            string(name:'TAG' , value:"${env.CURRENT_VERSION}"),
                                                            string(name:'SL_REPORT_BRANCH' , value:"${params.sl_report_branch}"),
                                                            string(name:'BRANCH' , value:"${params.branch}"),
                                                            string(name:'BUILD_NAME' , value:"${env.BUILD_NAME}"),
                                                            string(name:'SL_TOKEN' , value:"${env.TOKEN}"),
                                                            string(name:'AGENT_URL' , value:AGENT_URL[0]),
                                                            string(name:'AGENT_URL_SLCI' , value:AGENT_URL[1])])
    }
  }
  parallel parallelLabs
}

def getParamForService(service, mapurl) {

  switch (service) {
    case "adservice":
      return [mapurl['JAVA_AGENT_URL'].toString(),""]
    case "cartservice":
      return [mapurl['DOTNET_AGENT_URL'].toString(),""]
    case ["checkoutservice","frontend","productcatalogservice","shippingservice"]:
      return [mapurl['GO_AGENT_URL'].toString(),mapurl['GO_SLCI_AGENT_URL'].toString()]
    case ["emailservice","recommendationservice"]:
      return [mapurl['PYTHON_AGENT_URL'].toString(),""]
    case ["currencyservice","paymentservice"]:
      return [mapurl['NODE_AGENT_URL'].toString(),""]
    case "sealightservice":
      return [mapurl['JAVA_AGENT_URL'].toString(),""]
  }
}

def SpinUpBoutiqeEnvironment(Map params){
  env.MACHINE_DNS = "http://dev-${params.IDENTIFIER}.dev.sealights.co:8081"
  env.LAB_ID = create_lab_id(
    token: "${env.TOKEN}",
    machine: "https://${params.lab}.sealights.co",
    app: "${params.app_name}",
    branch: "${params.build_branch}",
    test_env: "${params.IDENTIFIER}",
    lab_alias: "${params.IDENTIFIER}",
    cdOnly: true,
  )
  build(job: 'SpinUpBoutiqeEnvironment', parameters: [string(name: 'ENV_TYPE', value: "DEV"),
                                                      string(name:'IDENTIFIER' , value:"${params.IDENTIFIER}")
                                                      ,string(name:'CUSTOM_EC2_INSTANCE_TYPE' , value:"t3a.large"),
                                                      string(name:'GIT_BRANCH' , value:"${params.git_branch}"),
                                                      string(name:'BTQ_LAB_ID' , value:"${env.LAB_ID}"),
                                                      string(name:'INSTALL_DD' , value:"${params.enable_dd}"),
                                                      string(name:'BTQ_TOKEN' , value:"${env.TOKEN}"),
                                                      string(name:'BTQ_VERSION' , value:"${env.CURRENT_VERSION}"),
                                                      string(name:'BUILD_NAME' , value:"${env.BUILD_NAME}"),
                                                      string(name:'JAVA_AGENT_URL' , value: "${params.java_agent_url}"),
                                                      string(name:'DOTNET_AGENT_URL' , value: "${params.dotnet_agent_url}"),
                                                      string(name:'SL_BRANCH' , value:"${params.sl_branch}")])
}

def run_tests(Map params){
  if (params.test_type == 'Tests parallel') {
    sleep time: 150, unit: 'SECONDS'
    def parallelLabs = [:]
    //List of all the jobs
    def jobs_list = ["BTQ-java-tests(Junit without testNG)", "BTQ-java-tests(Junit without testNG)-gradle",
                     "BTQ-python-tests(Pytest framework)", "BTQ-nodejs-tests(Mocha framework)", "BTQ-dotnet-tests(MS-test framework)",
                     "BTQ-nodejs-tests(Jest framework)", "BTQ-python-tests(Robot framework)", "BTQ-dotnet-tests(NUnit-test framework)",
                     "BTQ-java-tests(Junit support-testNG)", "BTQ-postman-tests", "BTQ-java-tests(Cucumber-framework-java)", "BTQ-java-tests-SoapUi-framework",
                     "BTQ-nodejs-tests-Cypress-framework"]

    jobs_list.each { job ->
      parallelLabs["${job}"] = {
        build(job: "${job}", parameters: [string(name: 'BRANCH', value: "${params.branch}"), string(name: 'SL_LABID', value: "${env.LAB_ID}"), string(name: 'SL_TOKEN', value: "${env.TOKEN}"), string(name: 'MACHINE_DNS1', value: "${env.MACHINE_DNS}")])
      }
    }
    parallel parallelLabs
  } else {
    if (params.test_type == 'Tests sequential') {
      sleep time: 150, unit: 'SECONDS'
      def jobs_list = [
        "BTQ-java-tests(Junit without testNG)",
        "BTQ-python-tests(Pytest framework)",
        "BTQ-nodejs-tests(Mocha framework)",
        "BTQ-dotnet-tests(MS-test framework)",
        "BTQ-nodejs-tests(Jest framework)",
        "BTQ-python-tests(Robot framework)",
        "BTQ-dotnet-tests(NUnit-test framework)",
        "BTQ-java-tests(Junit support-testNG)",
        "BTQ-nodejs-tests-Cypress-framework",
        "BTQ-java-tests-SoapUi-framework",
        "BTQ-java-tests(Cucumber-framework-java)",
        "BTQ-java-tests(Junit without testNG)-gradle",
        "BTQ-postman-tests"
      ]

      jobs_list.each { job ->
        build(job: "${job}", parameters: [
          string(name: 'BRANCH', value: "${params.branch}"),
          string(name: 'SL_LABID', value: "${env.LAB_ID}"),
          string(name: 'SL_TOKEN', value: "${env.TOKEN}"),
          string(name: 'MACHINE_DNS1', value: "${env.MACHINE_DNS}")
        ])
        sleep time: 60, unit: 'SECONDS'
      }
    } else {
      sleep time: 150, unit: 'SECONDS'
      build(job: "All-In-One/${params.branch}", parameters: [
        string(name: 'TECHNOLOGY', value: "${params.technology}"),
        string(name: 'BRANCH', value: "${params.branch}"),
        string(name: 'SL_LABID', value: "${env.LAB_ID}"),
        string(name: 'SL_TOKEN', value: "${env.TOKEN}"),
        string(name: 'MACHINE_DNS', value: "${env.MACHINE_DNS}"),
        booleanParam(name: 'Run_all_tests', value: params.Run_all_tests),
        booleanParam(name: 'Cucumber', value: params.Cucumber),
        booleanParam(name: 'Cypress', value: params.Cypress),
        booleanParam(name: 'Cucumberjs', value: params.Cucumberjs),
        booleanParam(name: 'Junit_with_testNG', value: params.Junit_with_testNG),
        booleanParam(name: 'Junit_without_testNG', value: params.Junit_without_testNG),
        booleanParam(name: 'Junit_with_testNG_gradle', value: params.Junit_with_testNG_gradle),
        booleanParam(name: 'Mocha', value: params.Mocha),
        booleanParam(name: 'MS', value: params.Mocha),
        booleanParam(name: 'NUnit', value: params.NUnit),
        booleanParam(name: 'Postman', value: params.Postman),
        booleanParam(name: 'Pytest', value: params.Pytest),
        booleanParam(name: 'Robot', value: params.Robot),
        booleanParam(name: 'Soapui', value: params.Soapui),
        booleanParam(name: 'Karate', value: params.Karate)
      ])
    }
  }


}

def success_btq(Map params){
  build(job: 'TearDownBoutiqeEnvironment', parameters: [string(name: 'ENV_TYPE', value: "DEV"), string(name: 'IDENTIFIER', value: "${params.IDENTIFIER}")])
  slackSend channel: "#btq-ci", tokenCredentialId: "slack_sldevops", color: "good", message: "BTQ-CI build ${env.CURRENT_VERSION} for branch ${BRANCH_NAME} finished with status ${currentBuild.currentResult} (<${env.BUILD_URL}|Open> and TearDownBoutiqeEnvironment)"
}

def failure_btq(Map params){
  def env_instance_id = sh(returnStdout: true, script: "aws ec2 --region eu-west-1 describe-instances --filters 'Name=tag:Name,Values=EUW-ALLINONE-DEV-${params.IDENTIFIER}' 'Name=instance-state-name,Values=running' | jq -r '.Reservations[].Instances[].InstanceId'")
  sh "aws ec2 --region eu-west-1 stop-instances --instance-ids ${env_instance_id}"
  slackSend channel: "#btq-ci", tokenCredentialId: "slack_sldevops", color: "danger", message: "BTQ-CI build ${env.CURRENT_VERSION} for branch ${BRANCH_NAME} finished with status ${currentBuild.currentResult} (<${env.BUILD_URL}|Open>) and TearDownBoutiqeEnvironment"
}



def run_api_tests_before_changes(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "StableApiTests", parameters: [
      string(name: 'TECHNOLOGY', value: "${params.technology}"),
      string(name: 'RUN_DATA', value: "${params.run_data}"),
      string(name: 'LAB_UNDER_TEST', value: "${params.LAB_UNDER_TEST}"),
      string(name: 'SEALIGHTS_ENV_NAME', value: "${params.SEALIGHTS_ENV_NAME}"),
      string(name: 'BRANCH', value: "BTQ-TIA"),
      string(name: 'INTEGRATION_BRANCH', value: "${params.integration_branch}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}

def run_TIA_ON_testresult(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "TIA-Test-result", parameters: [
      string(name: 'BRANCH', value: "BTQ-TIA"),
      string(name: 'LAB_UNDER_TEST', value: "${params.LAB_UNDER_TEST}"),
      string(name: 'SEALIGHTS_ENV_NAME', value: "${params.SEALIGHTS_ENV_NAME}"),
      string(name: 'RUN_DATA', value: "${params.run_data}"),
      string(name: 'INTEGRAION_BRANCH', value: "${params.branch}"),
      string(name: 'LAB_ID', value: "${params.lab_id}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}




def run_api_tests_after_changes(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "ApiTests", parameters: [
      string(name: 'LAB_UNDER_TEST', value: "${params.LAB_UNDER_TEST}"),
      string(name: 'SEALIGHTS_ENV_NAME', value: "${params.SEALIGHTS_ENV_NAME}"),
      string(name: 'RUN_DATA', value: "${params.run_data}"),
      string(name: 'BRANCH', value: "BTQ-TIA"),
      string(name: 'INTEGRATION_BRANCH', value: "${params.integration_branch}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}

def TIA_Page_Tests(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "TIA-Page-Tests", parameters: [
      string(name: 'BRANCH', value: "BTQ-TIA"),
      string(name: 'LAB_UNDER_TEST', value: "${params.LAB_UNDER_TEST}"),
      string(name: 'SEALIGHTS_ENV_NAME', value: "${params.SEALIGHTS_ENV_NAME}"),
      string(name: 'RUN_DATA', value: "${params.run_data}"),
      string(name: 'INTEGRAION_BRANCH', value: "${params.branch}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}


def clone_repo(Map params){
  // Clone the repository with the specified branch
  git branch: params.branch, url: 'https://github.com/Sealights/microservices-demo.git'
}

def set_assume_role(Map params) {
  params.set_globaly = params.set_globaly == null ? true : params.set_globaly
  def credential_map = sh (returnStdout: true, script: """
                                aws sts assume-role --role-arn arn:aws:iam::${params.account_id}:role/${params.role_name}  \\
                                --role-session-name ${params.env}-access --query \"Credentials\"
                            """).replace('"', '').replaceAll('[\\s]', '').trim()

  def map = convert_to_map(credential_map)
  if (params.set_globaly) {
    env.AWS_ACCESS_KEY_ID = "${map.AccessKeyId}"
    env.AWS_SECRET_ACCESS_KEY = "${map.SecretAccessKey}"
    env.AWS_SESSION_TOKEN = "${map.SessionToken}"
  } else {
    return map
  }
}

def create_lab_id(Map params) {
  try {
    def cdOnlyString = ""
    if (params.cdOnly){
      cdOnlyString = ', "cdOnly": true'
    }
    def lab_id
    if (params.isPR){
      lab_id = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids/pull-request" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "targetBranch": "${params.target_branch}", "isHidden": true }' | jq -r '.data.labId'
           """)).trim()
    } else {
      lab_id = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "labAlias": "${params.lab_alias}", "isHidden": true ${cdOnlyString}}' | jq -r '.data.labId'
           """)).trim()
    }
    echo "LAB ID: ${lab_id}"
    return lab_id
  } catch (err) {
    echo "${lab_id}"
    error "Failed to create lab id"
  }
}

def convert_to_map(mapAsString) {
  def map =
    // Take the String value between
    // the [ and ] brackets.
    mapAsString[1..-2]
    // Split on , to get a List.
      .split(',')
    // Each list item is transformed
    // to a Map entry with key/value.
      .collectEntries { entry ->
        def pair = entry.split(':')
        [(pair.first()): "${pair.last()}"]
      }
  return map
}

def schedule_full_run(Map params) {
  try {
    def RESPONSE = (sh(returnStdout: true, script: "curl -X PUT -w \"%{http_code}\" https://${params.machine}/sl-api/v1/tia/apps/${params.app_name}/branches/${params.branch_name}/testStages/${params.test_stage}/full-run -H \"Authorization: Bearer ${params.token}\" -H \"Content-Type: application/json\" -d \'{\"enable\": true}\'")).trim()
    if ( "${RESPONSE}" != "200" && "${RESPONSE}" != "201" ) {
      return false
    }
    return true
  } catch(err) {
    error "Failed to schedule full-run"
  }
}
