// Simple Jenkins Pipeline for WeChat Application
// This pipeline builds, tests, and deploys the application

pipeline {
    agent any
    
    // Tools needed for the build
    tools {
        maven 'Maven-3.9.0'
        nodejs 'NodeJS-18'
    }
    
    stages {
        // Step 1: Get the code from repository
        stage('Checkout Code') {
            steps {
                echo 'Getting code from repository...'
                checkout scm
            }
        }
        
        // Step 2: Build the backend (Java Spring Boot)
        stage('Build Backend') {
            steps {
                echo 'Building backend application...'
                dir('backend') {
                    sh 'mvn clean compile'
                }
            }
        }
        
        // Step 3: Test the backend
        stage('Test Backend') {
            steps {
                echo 'Running backend tests...'
                dir('backend') {
                    sh 'mvn test'
                }
            }
        }
        
        // Step 4: Package the backend
        stage('Package Backend') {
            steps {
                echo 'Creating backend JAR file...'
                dir('backend') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        
        // Step 5: Build the frontend (React)
        stage('Build Frontend') {
            steps {
                echo 'Building frontend application...'
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }
        
        // Step 6: Test the frontend
        stage('Test Frontend') {
            steps {
                echo 'Running frontend tests...'
                dir('frontend') {
                    sh 'npm test -- --watchAll=false'
                }
            }
        }
        
        // Step 7: Build Docker images
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images...'
                
                // Build backend Docker image
                script {
                    def backendImage = docker.build("wechat-backend:${BUILD_NUMBER}", "./backend")
                }
                
                // Build frontend Docker image
                script {
                    def frontendImage = docker.build("wechat-frontend:${BUILD_NUMBER}", "./frontend")
                }
            }
        }
        
        // Step 8: Deploy the application
        stage('Deploy Application') {
            steps {
                echo 'Deploying application with Docker Compose...'
                sh 'docker-compose down'
                sh 'docker-compose up -d'
            }
        }
    }
    
    // What to do when the pipeline finishes
    post {
        always {
            echo 'Pipeline finished!'
            // Clean up workspace
            cleanWs()
        }
        
        failure {
            echo '❌ Build or deployment failed!'
            // Send failure email notification
            emailext (
                subject: "❌ WeChat Build #${BUILD_NUMBER} - FAILED",
                body: """
                <h2>Build Failed! ⚠️</h2>
                
                <p>The WeChat application build or deployment has failed.</p>
                
                """,
                mimeType: 'text/html',
                to: "pratikmane610@gmail.com"
            )
        }
    }
}
