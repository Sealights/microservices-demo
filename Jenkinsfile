@Library('main-shared-library') _
pipeline {
  agent {
    kubernetes {
      yaml kubernetes.base_pod([
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ci:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        shell_memory_request: "300Mi",
        shell_cpu_request: "0.5",
        shell_memory_limit: "900Mi",
        shell_cpu_limit: "1",
        kaniko_memory_request: "1200Mi",
        kaniko_cpu_request: "2",
        kaniko_memory_limit: "1800Mi",
        kaniko_cpu_limit: "3",
        node_selector: "jenkins"
      ])
      defaultContainer 'shell'
    }
  }

  parameters {
    string(name: 'APP_NAME', defaultValue: 'Collector-BTQ', description: 'name of the app (integration build)')
    string(name: 'BRANCH', defaultValue: 'Collector', description: 'Branch to clone')
    string(name: 'CHANGED_BRANCH', defaultValue: 'changed', description: 'Branch to run changed')
    string(name: 'BUILD_BRANCH', defaultValue: 'Collector', description: 'Branch to Build images that have the creational LAB_ID (send to wahbi branch to build)')
    string(name: 'SL_TOKEN', defaultValue: 'eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctYzNiM2IyY2YtYjA1Yy00ZWM2LThjNjYtZTBmZTJiYzIwNzAzLDE2OTI4Nzc3MDM4ODUiLCJzdWJqZWN0IjoiU2VhTGlnaHRzQGFnZW50IiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50IiwieC1zbC1zZXJ2ZXIiOiJodHRwczovL2Rldi1pbnRlZ3JhdGlvbi5kZXYuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE2OTI4Nzc3MDN9.dORXtjiTVw9vM3u2eO9l2r3f54NwEFPWVnhZnOWqV4_ZA-q2T86X861S6o4G7M371hMnoePRNoWgkjXp9isgEPEHoG_LQ_pvwc66vi5gBy8okjlypKGMTrz-N8bF1LeswguuSDDPIpm0Qq7KSjcm-GZmtO2IhJu4Q6f-tX0otMvvr6_nuwfVReExsT0Mxoyu0ZFs2HHwuIqhu12v1wNUuiTNIxQnGqckLw1qrroTG-qrDa8ydC111ML9C-u4qdS6G0iDsSdrQk9RETe0b1ow1vMXMFZeQ0vBrJDFjMnaCUhU6iid8xjkZG3T6XAI0k5SBRN8R6dtTO45mE638ohJi1_YBQL8hSkHL-8X_QkbRCH6IFqPcku0Wu2AcaRkBKOoiYAowFxnrQgYx5n_FVuTXNwW-s18Gnebd-bTBveCAHQH6CEbnpznXyMNXc15tOVdfp1n3RHLx9YE2lYI3dsTdwUlwNhto4J1Ym3ZOrLW_GZwLzZyIITfmNUOQVspwzsVOioeA48DZNpZhpZUAK5P19v0KY_iyJKxGajWnAUkXbyqc72d7eG5cUsIgv-r_p7fwnO4Rm1FVaZJ4Cpv7b4yf5YHGJ7BADI5Zw6YXuWQ3d9snZfvKOR50KVZGOykqwExYEwBACpN1WSEoIg8No7wTry_xNPmkTYOHbNoWuzyjTo', description: 'sl-token')
    string(name: 'BUILD_NAME', defaultValue: '', description: 'build name')
    string(name: 'JAVA_AGENT_URL', defaultValue: 'https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent_alpine.tar.gz', description: 'use different java agent')
    string(name: 'DOTNET_AGENT_URL', defaultValue: 'https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz', description: 'use different dotnet agent')
    string(name: 'NODE_AGENT_URL', defaultValue: 'slnodejs', description: 'use different node agent')
    string(name: 'GO_AGENT_URL', defaultValue: 'https://agents.sealights.co/slgoagent/latest/slgoagent-linux-amd64.tar.gz', description: 'use different go agent')
    string(name: 'GO_SLCI_AGENT_URL', defaultValue: 'https://agents.sealights.co/slcli/latest/slcli-linux-amd64.tar.gz', description: 'use different slci go agent')
    string(name: 'PYTHON_AGENT_URL', defaultValue: 'sealights-python-agent', description: 'use different python agent')
    string(name: 'lab', defaultValue: 'dev-integration.dev', description: 'name of the lab/environment for the lab id, default https://dev-integration.dev.sealights.co')
    choice(name: 'TEST_TYPE', choices: ['All Tests IN One Image', 'Tests sequential', 'Tests parallel'], description: 'Choose test type')
  }

  stages {
    stage('Clone Repository') {
      steps {
        script {
          clone_repo(
            branch: params.BRANCH
          )
        }
      }
    }


    //Build parallel images
    stage('Build BTQ') {
      steps {
        script {
          def MapUrl = new HashMap()
          MapUrl.put('JAVA_AGENT_URL', "${params.JAVA_AGENT_URL}")
          MapUrl.put('DOTNET_AGENT_URL', "${params.DOTNET_AGENT_URL}")
          MapUrl.put('NODE_AGENT_URL', "${params.NODE_AGENT_URL}")
          MapUrl.put('GO_AGENT_URL', "${params.GO_AGENT_URL}")
          MapUrl.put('GO_SLCI_AGENT_URL', "${params.GO_SLCI_AGENT_URL}")
          MapUrl.put('PYTHON_AGENT_URL', "${params.PYTHON_AGENT_URL}")

          build_btq(
            sl_report_branch: params.BRANCH,
            sl_token: params.SL_TOKEN,
            dev_integraion_sl_token: env.DEV_INTEGRATION_SL_TOKEN,
            build_name: "1-0-${BUILD_NUMBER}",
            branch: params.BRANCH,
            mapurl: MapUrl
          )
        }
      }
    }

    stage('Spin-Up BTQ') {
      steps {
        script {
          env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

          def IDENTIFIER= "${params.BRANCH}-${env.CURRENT_VERSION}"
          SpinUpBoutiqeEnvironment(
            IDENTIFIER : IDENTIFIER,
            branch: params.BRANCH,
            app_name: params.APP_NAME,
            build_branch: params.BUILD_BRANCH,
            java_agent_url: params.JAVA_AGENT_URL,
            dotnet_agent_url: params.DOTNET_AGENT_URL,
            sl_branch : params.BRANCH,
            git_branch : params.BUILD_BRANCH,
            lab : params.lab
          )
        }
      }
    }

    stage('Run Tests') {
      steps {
        script {
          run_tests(
            branch: params.BRANCH,
            test_type: params.TEST_TYPE
          )
        }
      }
    }

    // stage('Run Api-Tests Before Changes') {
    //   steps {
    //     script {
    //       run_api_tests_before_changes(
    //         branch: params.BRANCH,
    //         app_name: params.APP_NAME
    //       )
    //     }
    //   }
    // }


    // stage('Changed - Clone Repository') {
    //   steps {
    //     script {
    //       clone_repo(
    //         branch: params.CHANGED_BRANCH
    //       )
    //     }
    //   }
    // }

    // stage('Changed Build BTQ') {
    //   steps {
    //     script {
    //       def MapUrl = new HashMap()
    //       MapUrl.put('JAVA_AGENT_URL', "${params.JAVA_AGENT_URL}")
    //       MapUrl.put('DOTNET_AGENT_URL', "${params.DOTNET_AGENT_URL}")
    //       MapUrl.put('NODE_AGENT_URL', "${params.NODE_AGENT_URL}")
    //       MapUrl.put('GO_AGENT_URL', "${params.GO_AGENT_URL}")
    //       MapUrl.put('GO_SLCI_AGENT_URL', "${params.GO_SLCI_AGENT_URL}")
    //       MapUrl.put('PYTHON_AGENT_URL', "${params.PYTHON_AGENT_URL}")

    //       build_btq(
    //         sl_token: params.SL_TOKEN,
    //         sl_report_branch: params.BRANCH,
    //         dev_integraion_sl_token: env.DEV_INTEGRATION_SL_TOKEN,
    //         build_name: "1-0-${BUILD_NUMBER}-v2",
    //         branch: params.CHANGED_BRANCH,
    //         mapurl: MapUrl
    //       )
    //     }
    //   }
    // }



    // stage('Changed Spin-Up BTQ') {
    //   steps {
    //     script {
    //       def IDENTIFIER= "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"

    //       SpinUpBoutiqeEnvironment(
    //         IDENTIFIER : IDENTIFIER,
    //         branch: params.CHANGED_BRANCH,
    //         git_branch : params.CHANGED_BRANCH,
    //         app_name: params.APP_NAME,
    //         build_branch: params.BRANCH,
    //         java_agent_url: params.JAVA_AGENT_URL,
    //         dotnet_agent_url: params.DOTNET_AGENT_URL,
    //         sl_branch : params.CHANGED_BRANCH
    //       )
    //     }
    //   }
    // }

    // stage('Changed Run Tests') {
    //   steps {
    //     script {
    //       run_tests(
    //         branch: params.BRANCH,
    //         test_type: params.TEST_TYPE
    //       )
    //     }
    //   }
    // }


    // stage('Run API-Tests After Changes') {
    //   steps {
    //     script {
    //       run_api_tests_after_changes(
    //         branch: params.BRANCH,
    //         app_name: params.APP_NAME
    //       )
    //     }
    //   }
    // }
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

def get_secret (SecretID, Region, Profile="") {
  if (Profile != "") {
    Profile = "--profile ${Profile}"
  }
  String secret_key = "${SecretID.split('/')[-1]}" as String
  def secret_value = (sh(returnStdout: true, script: "aws secretsmanager get-secret-value --secret-id ${SecretID} --region ${Region} ${Profile}| jq -r '.SecretString' | jq -r '.${secret_key}'")).trim()
  return secret_value
}


def build_btq(Map params){
  env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"
  env.TOKEN= "${params.sl_token}"
  def parallelLabs = [:]
  //List of all the images name

  def services_list = ["adservice","cartservice","checkoutservice", "currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice"]
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
  }
}

def SpinUpBoutiqeEnvironment(Map params){
  env.MACHINE_DNS = "http://dev-${params.IDENTIFIER}.dev.sealights.co:8081"
  env.LAB_ID_SPIN = create_lab_id(
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
                                                      string(name:'BTQ_LAB_ID' , value:"${env.LAB_ID_SPIN}"),
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
        build(job: "${job}", parameters: [string(name: 'BRANCH', value: "${params.branch}"), string(name: 'SL_LABID', value: "${env.LAB_ID_SPIN}"), string(name: 'SL_TOKEN', value: "${env.TOKEN}"), string(name: 'MACHINE_DNS1', value: "${env.MACHINE_DNS}")])
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
          string(name: 'SL_LABID', value: "${env.LAB_ID_SPIN}"),
          string(name: 'SL_TOKEN', value: "${env.TOKEN}"),
          string(name: 'MACHINE_DNS1', value: "${env.MACHINE_DNS}")
        ])
        sleep time: 60, unit: 'SECONDS'
      }
    } else {
      sleep time: 150, unit: 'SECONDS'
      build(job: "Wahbi-All-in-image", parameters: [
        string(name: 'BRANCH', value: "${params.branch}"),
        string(name: 'SL_LABID', value: "${env.LAB_ID_SPIN}"),
        string(name: 'SL_TOKEN', value: "${env.TOKEN}"),
        string(name: 'MACHINE_DNS', value: "${env.MACHINE_DNS}")
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
      string(name: 'BRANCH', value: "${params.branch}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}

def run_api_tests_after_changes(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "ApiTests", parameters: [
      string(name: 'BRANCH', value: "${params.branch}"),
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
    if (params.isPR){
      env.LAB_ID = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids/pull-request" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "targetBranch": "${params.target_branch}", "isHidden": true }' | jq -r '.data.labId'
           """)).trim()
    } else {
      env.LAB_ID = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "labAlias": "${params.lab_alias}", "isHidden": true ${cdOnlyString}}' | jq -r '.data.labId'
           """)).trim()
    }
    echo "LAB ID: ${env.LAB_ID}"
    return env.LAB_ID
  } catch (err) {
    echo env.LAB_ID
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





