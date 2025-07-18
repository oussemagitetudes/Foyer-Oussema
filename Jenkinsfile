pipeline {
    agent any

    environment {
        // Chemins pour WSL/Ubuntu (ajustez selon votre installation)
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/oussemagitetudes/Foyer-Oussema.git'
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
