# Jenkins Pipeline Setup Guide for WeChat Project

## Prerequisites

### 1. Jenkins Installation
- Jenkins 2.400+ with the following plugins:
  - Pipeline
  - Docker Pipeline
  - Email Extension
  - Slack Notification
  - HTML Publisher
  - Credentials Binding

### 2. Required Tools
- Maven 3.9.0
- Node.js 18+
- Docker
- Docker Compose

## Credentials Setup

Create the following credentials in Jenkins (Manage Jenkins > Credentials):

### 1. Docker Hub Credentials
- **ID**: `docker-hub-credentials`
- **Type**: Username with password
- **Username**: Your Docker Hub username
- **Password**: Your Docker Hub password

### 2. Database Credentials
- **ID**: `postgres-db-credentials`
- **Type**: Username with password
- **Username**: Database username
- **Password**: Database password

### 3. JWT Secret
- **ID**: `jwt-secret`
- **Type**: Secret text
- **Secret**: Your JWT secret key (at least 32 characters)

### 4. Email Credentials
- **ID**: `gmail-app-credentials`
- **Type**: Username with password
- **Username**: Gmail username
- **Password**: Gmail app password

### 5. Cloudinary Credentials
- **ID**: `cloudinary-credentials`
- **Type**: Secret text
- **Secret**: JSON with cloud_name and upload_preset
```json
{
  "cloud_name": "your_cloud_name",
  "upload_preset": "your_upload_preset"
}
```

### 6. ZEGO Credentials
- **ID**: `zego-credentials`
- **Type**: Secret text
- **Secret**: JSON with app_id and server_secret
```json
{
  "app_id": "your_zego_app_id",
  "server_secret": "your_zego_server_secret"
}
```

## Pipeline Configuration

### 1. Create Pipeline Job
1. New Item → Pipeline
2. Name: `WeChat-Pipeline`
3. Configure:
   - **Pipeline Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: Your GitHub repository URL
   - **Branch**: `*/master` and `*/develop`
   - **Script Path**: `Jenkinsfile`

### 2. Webhook Setup
Configure GitHub webhook:
- URL: `http://your-jenkins-url/github-webhook/`
- Content type: `application/json`
- Events: Push, Pull request

### 3. Environment Variables
Set the following environment variables in Jenkins:
```bash
DOCKER_HUB_REPO=pratikmane0112/wechat
DB_HOST=postgres
DB_PORT=5432
DB_NAME_STAGING=wechat_staging  
DB_NAME_PROD=wechat_production
REDIS_HOST=redis
REDIS_PORT=6379
```

## Branch Strategy

### Master Branch
- Triggers production deployment
- Requires manual approval
- Runs full test suite
- Blue-green deployment

### Develop Branch
- Triggers staging deployment
- Automatic deployment
- Integration tests
- Quality gates

### Feature Branches
- Builds and tests only
- No deployment
- Code quality checks

## Notifications

### Slack Integration
1. Install Slack plugin
2. Configure Slack token
3. Set channel: `#deployments`

### Email Notifications
1. Configure SMTP settings
2. Set team email: `team@wechat.com`
3. HTML email templates included

## Quality Gates

### Backend
- Unit tests (Maven Surefire)
- Integration tests
- Code coverage (JaCoCo)
- Security scan (OWASP)
- Static analysis (SonarQube optional)

### Frontend
- Unit tests (Jest)
- Code coverage
- Linting (ESLint)
- Security audit (npm audit)
- Build optimization

## Deployment Strategies

### Staging
- Direct deployment
- Health checks
- Integration tests

### Production
- Blue-green deployment
- Manual approval required
- Health checks
- Rollback capability

## Monitoring

### Health Checks
- Backend: `/actuator/health`
- Frontend: `/health`
- Database: PostgreSQL connection
- Redis: Ping command

### Logging
- Application logs in containers
- Jenkins build logs
- Deployment notifications

## Troubleshooting

### Common Issues

1. **Docker build fails**
   - Check Dockerfile syntax
   - Verify base images are available
   - Check network connectivity

2. **Tests fail**
   - Review test logs
   - Check database connections
   - Verify environment variables

3. **Deployment fails**
   - Check docker-compose syntax
   - Verify credentials
   - Check port availability

### Debug Commands
```bash
# Check Docker images
docker images | grep wechat

# Check running containers
docker ps | grep wechat

# View container logs
docker logs wechat-backend-prod

# Check application health
curl -f http://localhost:5000/actuator/health
```

## Security Best Practices

1. Use least privilege principles
2. Store secrets in Jenkins credentials
3. Regular credential rotation
4. Container security scanning
5. Network isolation
6. HTTPS in production
7. Database encryption

## Backup Strategy

### Database Backups
```bash
# Automated backup script
docker exec wechat-postgres-prod pg_dump -U username wechat_production > backup_$(date +%Y%m%d).sql
```

### Application Backups
- Docker images stored in registry
- Configuration files in Git
- Persistent volumes backed up

## Performance Optimization

### Build Optimization
- Multi-stage Docker builds
- Dependency caching
- Parallel builds
- Image layer optimization

### Deployment Optimization
- Health check tuning
- Resource limits
- Load balancing
- CDN integration

## Getting Started

1. Clone the repository
2. Set up Jenkins with required plugins
3. Configure credentials
4. Create pipeline job
5. Configure webhooks
6. Test with feature branch
7. Deploy to staging
8. Promote to production

For support, contact the DevOps team or check the project documentation.
