pipeline {
    agent any

    tools {
        maven 'Maven' // nom que tu as défini dans Jenkins > Global Tool Configuration
        jdk 'JDK17'   // nom que tu as défini pour Java 17
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/oussemagitetudes/Foyer-Oussema.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean test'
            }
        }

        stage('Code Coverage') {
            steps {
                sh './mvnw jacoco:report'
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
