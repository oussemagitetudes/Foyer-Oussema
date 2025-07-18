pipeline {
    agent any
    
    tools {
        maven 'Maven'      // Nom défini dans Jenkins > Tools > Maven
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/oussemagitetudes/Foyer-Oussema.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                junit 'target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé.'
        }
    }
}
