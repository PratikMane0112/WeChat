# 🚀 WeChat Social Platform - Enterprise DevOps Architecture

<div align="center">

![WeChat Social Platform](https://img.shields.io/badge/WeChat-Social%20Platform-blue?style=for-the-badge&logo=wechat)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.4-brightgreen?style=for-the-badge&logo=spring)
![React](https://img.shields.io/badge/React-18.2.0-blue?style=for-the-badge&logo=react)
![AWS](https://img.shields.io/badge/AWS-ECS%20Fargate-orange?style=for-the-badge&logo=amazon-aws)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker)

### *Production-Ready Social Media Platform with Modern DevOps Pipeline*

[🌐 Live Demo](https://pratik0112-wechat.vercel.app/) • [📚 Documentation](#documentation) • [🚀 Quick Start](#quick-start)

</div>

---

## 🏗️ System Architecture Overview

### 📊 **Complete System Architecture**

<div align="center">

![System Architecture](https://raw.githubusercontent.com/PratikMane0112/WeChat/master/docs/architecture-diagram.svg)

*Enterprise-grade social media platform with comprehensive DevOps pipeline and AWS cloud infrastructure*

</div>

### 🌐 **AWS Network & Security Architecture**

<div align="center">

![Network Architecture](https://raw.githubusercontent.com/PratikMane0112/WeChat/master/docs/network-architecture-diagram.svg)

*Multi-AZ deployment with comprehensive security groups, VPC configuration, and monitoring*

</div>

### 🔄 **DevOps Pipeline Architecture**

<div align="center">

![DevOps Pipeline](https://raw.githubusercontent.com/PratikMane0112/WeChat/master/docs/devops-pipeline-diagram.svg)

*Complete CI/CD pipeline from development to production with monitoring and observability*

</div>

---

## 🎯 Project Overview

**Enterprise-grade social media platform** built with Spring Boot and React, featuring real-time messaging, video calling, and comprehensive DevOps automation. Demonstrates production-ready architecture with AWS cloud deployment and CI/CD pipelines.

### ✨ Core Features
- 🔐 **JWT Authentication** with Spring Security
- 💬 **Real-time Messaging** via WebSocket/STOMP
- 📹 **Video Calling** with ZEGOCLOUD integration
- 📱 **Social Feed** with posts, stories, and reels
- ☁️ **Cloud Storage** using Cloudinary and AWS S3
- 🚀 **Auto-scaling** ECS Fargate deployment

---

## 💻 Technology Stack

### **Backend** 
- **Framework**: Spring Boot 3.1.4, Java 17
- **Database**: PostgreSQL 15 (Multi-AZ), Redis Cache
- **Security**: Spring Security, JWT Authentication
- **Real-time**: WebSocket/STOMP messaging
- **Testing**: JUnit 5, Mockito (80%+ coverage)

### **Frontend**
- **Framework**: React 18.2.0, TypeScript
- **State**: Redux Toolkit, Material-UI
- **Real-time**: WebSocket client integration
- **Testing**: Jest, React Testing Library

### **DevOps & Cloud**
- **Containerization**: Docker, AWS ECR
- **Orchestration**: AWS ECS Fargate, Auto-scaling
- **CI/CD**: Jenkins Pipeline, automated testing
- **Infrastructure**: Terraform, CloudFormation IaC
- **Monitoring**: CloudWatch, Grafana, Prometheus

---

## 🚀 Quick Start

### **Local Development**
```bash
# Clone and setup
git clone https://github.com/PratikMane0112/WeChat.git
cd WeChat

# Start with Task runner
task dev:setup    # Setup environment
task dev:run      # Run all services
task dev:test     # Run comprehensive tests
```

### **Production Deployment**
```bash
# Infrastructure provisioning
task infra:apply ENV=production

# CI/CD Pipeline
task ci:build     # Build and test
task cd:deploy    # Deploy to AWS ECS
```

---

## 🔧 DevOps Features

### **CI/CD Pipeline**
- ✅ Automated testing (Unit, Integration, Security)
- 🐳 Multi-stage Docker builds with optimization
- 🔒 OWASP security scanning and vulnerability checks
- 🚀 Blue-green deployments with zero downtime
- 📊 Comprehensive monitoring and alerting

### **Infrastructure as Code**
- 🏗️ **Terraform** modules for AWS infrastructure
- ☁️ **CloudFormation** templates for complete stack
- 🔧 **30+ Task automation** scripts for operations
- 📈 **Cost optimization** with Spot instances and auto-scaling

### **Quality Assurance**
- 🧪 **80%+ test coverage** with JUnit 5 and Mockito
- 🔍 **Static code analysis** and quality gates
- 🛡️ **Security-first** approach with automated scans
- 📊 **Performance monitoring** and optimization

---

## 📚 Documentation

### 🔗 Additional Resources
- [🏗️ Infrastructure Setup Guide](./infrastructure/README.md)
- [🔧 Development Guide](./docs/DEVELOPMENT.md)
- [🚀 Task Automation Reference](./Taskfile.yml)

### 📋 Key Commands
```bash
# Development
task dev:setup          # Complete environment setup
task dev:run            # Start all services locally
task ci:build           # Run full test suite

# Infrastructure  
task infra:apply         # Deploy AWS infrastructure
task deploy:production   # Production deployment
task health:check        # System health verification

# Testing
task test:backend        # Backend tests with coverage
task test:security       # Security vulnerability scans
task test:integration    # End-to-end testing
```

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Run tests (`task ci:build`)
4. Commit changes (`git commit -m 'Add amazing feature'`)
5. Push to branch (`git push origin feature/amazing-feature`)
6. Open Pull Request

---

## 👨‍💻 Author

**Pratik Mane** 
- 🐙 GitHub: [@PratikMane0112](https://github.com/PratikMane0112)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

---

<div align="center">

### ⭐ **Star this repository if you found it helpful!**

**Built with ❤️ showcasing modern full-stack development and DevOps practices**

![Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)
![Testing](https://img.shields.io/badge/Testing-80%25%20Coverage-green)
![Deployment](https://img.shields.io/badge/Deployment-Automated-orange)
![Monitoring](https://img.shields.io/badge/Monitoring-Real--time-purple)

</div>
