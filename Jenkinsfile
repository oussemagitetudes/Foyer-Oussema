pipeline {
    agent any

    tools {
        maven 'Maven'      // Nom défini dans Jenkins > Tools > Maven
        jdk 'JDK17'        // Nom défini dans Jenkins > Tools > JDK
    }

    environment {
        // Chemins ou variables utiles si besoin
        PROJECT_NAME = "Foyer-Oussema"
        SOURCE_CODE_PATH = "${WORKSPACE}/"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Clonage du projet depuis GitHub..."
                git url: 'https://github.com/oussemagitetudes/Foyer-Oussema.git'
            }
        }

        stage('Build & Test') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    sh 'mvn clean test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: "${SOURCE_CODE_PATH}/target/surefire-reports/*.xml"
                }
            }
        }

        stage('Code Coverage') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    sh 'mvn jacoco:report'
                }
            }
            post {
                success {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé.'
            cleanWs()
        }
        success {
            echo '✅ Build terminé avec succès !'
        }
        failure {
            echo '❌ Le pipeline a échoué.'
        }
    }
}
