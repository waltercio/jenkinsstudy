pipeline{
    agent any
    stages{
        stage('clone'){
            steps{
                checkout scmGit(
                branches: [[name: 'main']],
                url: 'ssh://github.com/jenkinsci/git-plugin.git']])
            }
        }
        stage('Build'){
            echo "Test Jenkins"
        }
    }
}