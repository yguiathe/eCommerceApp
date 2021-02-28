pipeline {
    agent any

    environment {
        EC2_HOST = 'ec2-54-215-128-155.us-west-1.compute.amazonaws.com'
        EC2_USER = 'ec2-user'
        EC2_KEY_ID = 'ec9f6f1c-fbd4-4855-8303-1cd3c5906eaa'
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }

    stages {
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                git branch: 'main', url: 'https://github.com/yguiathe/eCommerceApp.git'
            }

        }
        stage('Compile') {
            steps {
                echo "-=- compiling project -=-"
                sh "./mvnw clean compile"
            }
        }

        stage('Unit tests') {
            steps {
                echo "-=- execute unit tests -=-"
                sh "./mvnw test"
                junit 'target/surefire-reports/*.xml'
                jacoco execPattern: 'target/jacoco.exec'
            }
        }

        stage('Package and build image') {
            steps {
                echo "-=- packaging project -=-"
                sh "./mvnw package -DskipTests"
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Push Docker image') {
            steps {
                echo "-=- push Docker image -=-"
                sh "./mvnw dockerfile:push"
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(credentials : ['ec9f6f1c-fbd4-4855-8303-1cd3c5906eaa']) {
                    sh label: '', script: '''ssh -o StrictHostKeyChecking=no -t ec2-user@ec2-54-215-128-155.us-west-1.compute.amazonaws.com "
                        sudo yum update -y;
                        sudo yum install -y docker;
                        sudo service docker start;
                        sudo usermod -a -G docker ec2-user;
                        docker run -d -p 8080:8080 tayfint/auth-course:0.0.1-SNAPSHOT"'''
                }
            }
        }

    }
}
