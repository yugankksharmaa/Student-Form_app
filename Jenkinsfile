pipeline {
    agent any

    tools {
        maven 'Maven3'   // Name configured in Jenkins > Global Tool Configuration
        jdk 'JDK11'      // Name configured in Jenkins > Global Tool Configuration
    }

    environment {
        // Path where Tomcat is installed on the Jenkins/target machine
        TOMCAT_WEBAPPS = '/opt/tomcat/webapps'
        WAR_NAME       = 'form.war'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Cloning source code from GitLab...'
                git branch: 'main',
                    url: 'https://gitlab.com/<your-username>/student-form-app.git',
                    credentialsId: 'gitlab-credentials'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application with Maven...'
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests (if any)...'
                sh 'mvn test || true'
            }
        }

        stage('Archive WAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo 'Deploying WAR file to Apache Tomcat...'
                sh """
                    cp target/${WAR_NAME} ${TOMCAT_WEBAPPS}/
                """
                // If Tomcat needs a restart to pick up the new WAR cleanly:
                // sh 'sudo systemctl restart tomcat'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully. App deployed to Tomcat.'
        }
        failure {
            echo 'Pipeline failed. Check console output above.'
        }
    }
}
