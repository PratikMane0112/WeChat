pipeline {
    agent any
    
    environment {
        // Docker Hub credentials
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        DOCKER_HUB_REPO = 'pratikmane0112/wechat'
        
        // Application versions
        BACKEND_IMAGE_TAG = "${BUILD_NUMBER}"
        FRONTEND_IMAGE_TAG = "${BUILD_NUMBER}"
        
        // Database credentials
        DB_CREDENTIALS = credentials('postgres-db-credentials')
        JWT_SECRET = credentials('jwt-secret')
        
        // Email credentials
        MAIL_CREDENTIALS = credentials('gmail-app-credentials')
        
        // Cloudinary credentials
        CLOUDINARY_CREDENTIALS = credentials('cloudinary-credentials')
        
        // ZEGO credentials
        ZEGO_CREDENTIALS = credentials('zego-credentials')
    }
    
    tools {
        maven 'Maven-3.9.0'
        nodejs 'NodeJS-18'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    // Get commit info for notifications
                    env.GIT_COMMIT_MSG = sh(
                        script: 'git log -1 --pretty=%B',
                        returnStdout: true
                    ).trim()
                    env.GIT_AUTHOR = sh(
                        script: 'git log -1 --pretty=%an',
                        returnStdout: true
                    ).trim()
                }
            }
        }
        
        stage('Build Backend') {
            steps {
                dir('backend') {
                    script {
                        echo 'Building Spring Boot application...'
                        sh 'mvn clean compile'
                    }
                }
            }
        }
        
        stage('Test Backend') {
            steps {
                dir('backend') {
                    script {
                        echo 'Running backend tests...'
                        sh 'mvn test'
                    }
                }
                post {
                    always {
                        publishTestResults testResultsPattern: 'backend/target/surefire-reports/*.xml'
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'backend/target/site/jacoco',
                            reportFiles: 'index.html',
                            reportName: 'Backend Code Coverage Report'
                        ])
                    }
                }
            }
        }
        
        stage('Package Backend') {
            steps {
                dir('backend') {
                    script {
                        echo 'Packaging Spring Boot application...'
                        sh 'mvn package -DskipTests'
                    }
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    script {
                        echo 'Installing frontend dependencies...'
                        sh 'npm ci'
                        
                        echo 'Building React application...'
                        sh 'npm run build'
                    }
                }
            }
        }
        
        stage('Test Frontend') {
            steps {
                dir('frontend') {
                    script {
                        echo 'Running frontend tests...'
                        sh 'npm test -- --coverage --watchAll=false'
                    }
                }
                post {
                    always {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'frontend/coverage/lcov-report',
                            reportFiles: 'index.html',
                            reportName: 'Frontend Code Coverage Report'
                        ])
                    }
                }
            }
        }
        
        stage('Security Scan') {
            parallel {
                stage('Backend Security') {
                    steps {
                        dir('backend') {
                            script {
                                echo 'Running OWASP dependency check...'
                                sh 'mvn org.owasp:dependency-check-maven:check'
                            }
                        }
                    }
                }
                stage('Frontend Security') {
                    steps {
                        dir('frontend') {
                            script {
                                echo 'Running npm audit...'
                                sh 'npm audit --audit-level=high'
                            }
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        script {
                            echo 'Building backend Docker image...'
                            def backendImage = docker.build("${DOCKER_HUB_REPO}-backend:${BACKEND_IMAGE_TAG}", 
                                                           "-f backend/Dockerfile backend/")
                            
                            echo 'Pushing backend image to Docker Hub...'
                            docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                                backendImage.push()
                                backendImage.push('latest')
                            }
                        }
                    }
                }
                stage('Build Frontend Image') {
                    steps {
                        script {
                            echo 'Building frontend Docker image...'
                            def frontendImage = docker.build("${DOCKER_HUB_REPO}-frontend:${FRONTEND_IMAGE_TAG}", 
                                                            "-f frontend/Dockerfile frontend/")
                            
                            echo 'Pushing frontend image to Docker Hub...'
                            docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                                frontendImage.push()
                                frontendImage.push('latest')
                            }
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    echo 'Deploying to staging environment...'
                    sh '''
                        # Update docker-compose for staging
                        envsubst < docker-compose.staging.yml > docker-compose.temp.yml
                        
                        # Deploy to staging
                        docker-compose -f docker-compose.temp.yml down
                        docker-compose -f docker-compose.temp.yml up -d
                        
                        # Wait for services to be ready
                        sleep 30
                        
                        # Health check
                        curl -f http://staging.wechat.local/health || exit 1
                    '''
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'master'
            }
            steps {
                script {
                    echo 'Deploying to production environment...'
                    
                    // Deployment approval
                    input message: 'Deploy to production?', 
                          ok: 'Deploy',
                          submitterParameter: 'DEPLOYER'
                    
                    sh '''
                        # Update docker-compose for production
                        envsubst < docker-compose.prod.yml > docker-compose.temp.yml
                        
                        # Blue-Green deployment
                        docker-compose -f docker-compose.temp.yml up -d --scale backend=2 --scale frontend=2
                        
                        # Wait for new instances to be ready
                        sleep 60
                        
                        # Health check
                        curl -f http://wechat.local/health || exit 1
                        
                        # Scale down old instances
                        docker-compose -f docker-compose.temp.yml up -d --scale backend=1 --scale frontend=1
                    '''
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'master'
                }
            }
            steps {
                script {
                    echo 'Running integration tests...'
                    dir('backend') {
                        sh 'mvn test -Dtest=**/*IntegrationTest'
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
            
            // Archive artifacts
            archiveArtifacts artifacts: 'backend/target/*.jar,frontend/build/**/*', 
                           fingerprint: true
        }
        
        success {
            script {
                // Send success notification
                slackSend(
                    channel: '#deployments',
                    color: 'good',
                    message: """
                        ✅ *WeChat Deployment Successful*
                        
                        *Branch:* ${env.BRANCH_NAME}
                        *Build:* ${env.BUILD_NUMBER}
                        *Commit:* ${env.GIT_COMMIT_MSG}
                        *Author:* ${env.GIT_AUTHOR}
                        *Duration:* ${currentBuild.durationString}
                        
                        *Docker Images:*
                        • Backend: ${DOCKER_HUB_REPO}-backend:${BACKEND_IMAGE_TAG}
                        • Frontend: ${DOCKER_HUB_REPO}-frontend:${FRONTEND_IMAGE_TAG}
                    """
                )
                
                // Email notification
                emailext(
                    subject: "✅ WeChat Build #${BUILD_NUMBER} - SUCCESS",
                    body: """
                        <h2>Build Successful!</h2>
                        <p><strong>Project:</strong> WeChat Social Platform</p>
                        <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Commit:</strong> ${env.GIT_COMMIT_MSG}</p>
                        <p><strong>Author:</strong> ${env.GIT_AUTHOR}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        
                        <h3>Deployed Images:</h3>
                        <ul>
                            <li>Backend: ${DOCKER_HUB_REPO}-backend:${BACKEND_IMAGE_TAG}</li>
                            <li>Frontend: ${DOCKER_HUB_REPO}-frontend:${FRONTEND_IMAGE_TAG}</li>
                        </ul>
                        
                        <p><a href="${BUILD_URL}">View Build Details</a></p>
                    """,
                    mimeType: 'text/html',
                    to: 'team@wechat.com'
                )
            }
        }
        
        failure {
            script {
                // Send failure notification
                slackSend(
                    channel: '#deployments',
                    color: 'danger',
                    message: """
                        ❌ *WeChat Deployment Failed*
                        
                        *Branch:* ${env.BRANCH_NAME}
                        *Build:* ${env.BUILD_NUMBER}
                        *Commit:* ${env.GIT_COMMIT_MSG}
                        *Author:* ${env.GIT_AUTHOR}
                        *Duration:* ${currentBuild.durationString}
                        
                        *Error:* Check console output for details
                        *Build URL:* ${BUILD_URL}
                    """
                )
                
                // Email notification
                emailext(
                    subject: "❌ WeChat Build #${BUILD_NUMBER} - FAILED",
                    body: """
                        <h2>Build Failed!</h2>
                        <p><strong>Project:</strong> WeChat Social Platform</p>
                        <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                        <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                        <p><strong>Commit:</strong> ${env.GIT_COMMIT_MSG}</p>
                        <p><strong>Author:</strong> ${env.GIT_AUTHOR}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        
                        <p>Please check the console output for error details.</p>
                        <p><a href="${BUILD_URL}console">View Console Output</a></p>
                    """,
                    mimeType: 'text/html',
                    to: 'team@wechat.com'
                )
            }
        }
        
        unstable {
            script {
                slackSend(
                    channel: '#deployments',
                    color: 'warning',
                    message: """
                        ⚠️ *WeChat Build Unstable*
                        
                        *Branch:* ${env.BRANCH_NAME}
                        *Build:* ${env.BUILD_NUMBER}
                        *Duration:* ${currentBuild.durationString}
                        
                        Some tests may have failed. Please review.
                        *Build URL:* ${BUILD_URL}
                    """
                )
            }
        }
    }
}
