pipeline{
    agent any
    stages{
        stage('clone'){
            steps{
                git 'git@github.ibm.com:mss-qa/api-automation.git'
            }
        }
        stage('Build'){
            echo "Test Jenkins"
        }
    }
}