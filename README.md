# WeChat Social Platform

A modern social media application built with Spring Boot and React, featuring real-time messaging, posts, stories, and video calling capabilities.

## 📋 Project Introduction

WeChat is a full-stack social media platform that allows users to:
- Create and share posts with images
- Send real-time messages to other users
- Share stories and reels
- Make video calls
- Follow and interact with other users

The application is live ✅[here](https://pratik0112-wechat.vercel.app/)

## � Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.1.4** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Primary database
- **Redis** - Caching and session storage
- **WebSocket** - Real-time communication

### Frontend
- **React 18** - Frontend framework
- **JavaScript/JSX** - Programming language
- **Redux** - State management
- **Material-UI** - Component library
- **Axios** - HTTP client

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for testing
- **Spring Boot Test** - Integration testing
- **Jest** - Frontend testing

## 🐳 Docker

The application is containerized using Docker for easy deployment and development.

### Docker Setup
- **Backend Dockerfile**: Builds the Spring Boot application
- **Frontend Dockerfile**: Builds and serves the React application
- **Docker Compose**: Orchestrates all services (database, backend, frontend)

### Running with Docker
```bash
# Start all services
docker-compose up -d

# View running containers
docker-compose ps

# Stop all services
docker-compose down
```

## 🔧 Jenkins CI/CD

Automated build and deployment pipeline using Jenkins.

### Pipeline Stages
1. **Checkout Code** - Get latest code from repository
2. **Build Backend** - Compile Java application
3. **Test Backend** - Run JUnit and Mockito tests
4. **Build Frontend** - Compile React application
5. **Test Frontend** - Run Jest tests
6. **Build Docker Images** - Create containerized applications
7. **Deploy Application** - Deploy using Docker Compose
8. **Send Notifications** - Email notifications on success/failure

### Jenkins Features
- Automated testing on every commit
- Docker image building and deployment
- Email notifications for build failure
- Clean workspace management

## 🧪 Testing with JUnit & Mockito

Comprehensive testing strategy ensuring code quality and reliability.

### Backend Testing
- **Unit Tests**: Testing individual components with JUnit 5
- **Mock Tests**: Using Mockito to mock dependencies
- **Integration Tests**: Testing complete workflows
- **Repository Tests**: Testing data access layer

### Test Coverage
- Service layer testing with mocked dependencies
- Controller testing with MockMvc
- Repository testing with @DataJpaTest
- Security testing for authentication flows

## ☁️ AWS EC2 Deployment

Application deployment on Amazon Web Services EC2 instances.

### Deployment Process
1. Launch EC2 instances with appropriate configuration
2. Install Docker and Docker Compose
3. Clone application repository
4. Set up environment variables
5. Deploy using Docker Compose

## � Getting Started

### Prerequisites
- Java 17
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL (for local development)

### Local Development
```bash
# Clone repository
git clone https://github.com/PratikMane0112/WeChat.git
cd WeChat

# Start with Docker Compose
docker-compose up -d

# Access application
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
```

### Manual Setup
```bash
# Backend
cd backend
./mvnw spring-boot:run

# Frontend (in new terminal)
cd frontend
npm install
npm start
```
## 🔨 Architecture
![jenkins-pipeline-flow](https://github.com/user-attachments/assets/cd3be48e-8b6e-42be-93e9-91de74348aa0)



## 👨‍💻 Author

**Pratik Mane**
- GitHub: [@PratikMane0112](https://github.com/PratikMane0112)
