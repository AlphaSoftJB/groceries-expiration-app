pipeline {
    agent {
        docker {
            image 'node:20-alpine'
            args '-u 0:0'
        }
    }
    
    environment {
        // Define environment variables for the build
        BACKEND_DIR = 'backend'
        FRONTEND_DIR = 'frontend'
        DOCKER_REGISTRY = 'your-docker-registry.com'
        IMAGE_NAME = 'groceries-expiration-app'
        TAG = "\${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Get the code from the repository
                checkout scm
            }
        }

        stage('Backend Build & Test') {
            agent {
                docker {
                    image 'gradle:8.5.0-jdk17-alpine'
                }
            }
            steps {
                dir(env.BACKEND_DIR) {
                    // Run unit and integration tests
                    sh 'gradle test'
                    // Build the final JAR
                    sh 'gradle bootJar'
                }
            }
        }

        stage('Frontend Build & Test') {
            steps {
                dir(env.FRONTEND_DIR) {
                    // Install dependencies
                    sh 'npm install'
                    // Run frontend tests (e.g., Jest, Detox)
                    sh 'npm test'
                    // Build the production bundle (for web/mobile)
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir(env.BACKEND_DIR) {
                    // Build the Docker image
                    sh "docker build -f Dockerfile.ci -t \${DOCKER_REGISTRY}/\${IMAGE_NAME}-backend:\${TAG} ."
                }
            }
        }

        stage('Docker Push') {
            steps {
                // Push the image to the registry
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "docker login \${DOCKER_REGISTRY} -u \${DOCKER_USERNAME} -p \${DOCKER_PASSWORD}"
                    sh "docker push \${DOCKER_REGISTRY}/\${IMAGE_NAME}-backend:\${TAG}"
                }
            }
        }

        stage('Deploy') {
            steps {
                // Placeholder for deployment script (e.g., Kubernetes, ECS, Lambda)
                echo "Deploying \${IMAGE_NAME}-backend:\${TAG} to production environment..."
                // sh './deploy.sh'
            }
        }
    }
}
