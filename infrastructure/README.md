# 🏗️ Infrastructure Documentation

## Overview

This document provides comprehensive information about the AWS infrastructure used for the WeChat Social Platform, including architecture decisions, resource configurations, and operational procedures.

## Infrastructure Architecture

### Network Layer
```
VPC (10.0.0.0/16)
├── Public Subnets (10.0.1.0/24, 10.0.2.0/24)
│   ├── Application Load Balancer
│   └── NAT Gateways
└── Private Subnets (10.0.10.0/24, 10.0.20.0/24)
    ├── ECS Fargate Tasks
    ├── RDS PostgreSQL
    └── ElastiCache Redis
```

### Security Groups Configuration

#### ALB Security Group
- **Inbound**: HTTP (80), HTTPS (443) from 0.0.0.0/0
- **Outbound**: All traffic to VPC CIDR

#### ECS Security Group  
- **Inbound**: HTTP (8080) from ALB Security Group
- **Outbound**: All traffic for external API calls

#### RDS Security Group
- **Inbound**: PostgreSQL (5432) from ECS Security Group
- **Outbound**: None

#### Redis Security Group
- **Inbound**: Redis (6379) from ECS Security Group
- **Outbound**: None

## Resource Specifications

### Compute Resources

#### ECS Fargate Configuration
```yaml
Service: wechat-backend-service
  Task Definition: wechat-backend-task
  CPU: 1024 (1 vCPU)
  Memory: 2048 MB (2 GB)
  
Service: wechat-frontend-service  
  Task Definition: wechat-frontend-task
  CPU: 512 (0.5 vCPU)
  Memory: 1024 MB (1 GB)

Auto Scaling:
  Min Capacity: 2 tasks
  Max Capacity: 10 tasks
  Target CPU: 70%
  Target Memory: 80%
```

#### Database Configuration
```yaml
RDS PostgreSQL:
  Engine: PostgreSQL 15.4
  Instance Class: db.t3.medium
  Allocated Storage: 100 GB
  Storage Type: gp3
  Multi-AZ: true
  Backup Retention: 7 days
  Encryption: true

ElastiCache Redis:
  Engine: Redis 7.0
  Node Type: cache.t3.medium  
  Nodes: 2 (with replication)
  Encryption: true
  Backup: enabled
```

## Infrastructure as Code

### CloudFormation Template Usage

```bash
# Deploy infrastructure
aws cloudformation create-stack \
  --stack-name wechat-infrastructure \
  --template-body file://infrastructure/cloudformation-template.yaml \
  --parameters \
    ParameterKey=Environment,ParameterValue=production \
    ParameterKey=ProjectName,ParameterValue=wechat \
    ParameterKey=DatabasePassword,ParameterValue=<secure-password> \
  --capabilities CAPABILITY_IAM

# Update stack
aws cloudformation update-stack \
  --stack-name wechat-infrastructure \
  --template-body file://infrastructure/cloudformation-template.yaml \
  --parameters file://infrastructure/parameters.json \
  --capabilities CAPABILITY_IAM
```

### Terraform Alternative

```bash
# Initialize Terraform
cd infrastructure/terraform
terraform init

# Plan infrastructure changes
terraform plan -var="environment=production" \
                -var="db_password=<secure-password>"

# Apply infrastructure
terraform apply -var="environment=production" \
                 -var="db_password=<secure-password>"

# Destroy infrastructure (if needed)
terraform destroy
```

## Cost Optimization

### Resource Sizing Strategy

#### Development Environment
```yaml
ECS Tasks: 1 task per service (minimum)
RDS: db.t3.micro
ElastiCache: cache.t3.micro
Estimated Monthly Cost: $80-120
```

#### Staging Environment  
```yaml
ECS Tasks: 2 tasks per service
RDS: db.t3.small
ElastiCache: cache.t3.small
Estimated Monthly Cost: $200-300
```

#### Production Environment
```yaml
ECS Tasks: 2-10 tasks (auto-scaling)
RDS: db.t3.medium (Multi-AZ)
ElastiCache: cache.t3.medium (2 nodes)
Estimated Monthly Cost: $400-800
```

### Cost Monitoring

```bash
# Set up billing alerts
aws budgets create-budget \
  --account-id <account-id> \
  --budget file://infrastructure/budget-config.json

# Enable Cost Explorer API
aws ce get-cost-and-usage \
  --time-period Start=2024-01-01,End=2024-01-31 \
  --granularity MONTHLY \
  --metrics BlendedCost
```

## Monitoring and Alerting

### CloudWatch Alarms

#### Application Health
- ECS Service CPU Utilization > 80%
- ECS Service Memory Utilization > 85%
- ALB Target Response Time > 2 seconds
- ALB HTTP 5xx Error Rate > 5%

#### Database Health
- RDS CPU Utilization > 80%
- RDS Free Storage Space < 10 GB
- RDS Connection Count > 80% of max
- ElastiCache CPU Utilization > 80%

#### Infrastructure Health
- NAT Gateway Packet Drop Count > 1000
- VPC Flow Logs indicating unusual traffic
- AWS Service Health Dashboard notifications

### Log Management

```yaml
Log Groups:
  - /aws/ecs/wechat-backend: Backend application logs
  - /aws/ecs/wechat-frontend: Frontend access logs
  - /aws/rds/instance/wechat-db: Database logs
  - /aws/applicationloadbalancer/wechat-alb: Load balancer logs

Retention Policy:
  - Development: 7 days
  - Staging: 30 days  
  - Production: 90 days
```

## Security Configuration

### IAM Roles and Policies

#### ECS Task Execution Role
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "secretsmanager:GetSecretValue"
      ],
      "Resource": "*"
    }
  ]
}
```

#### ECS Task Role
```json
{
  "Version": "2012-10-17", 
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ses:SendEmail",
        "ses:SendRawEmail",
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::wechat-assets/*",
        "arn:aws:ses:us-east-1:*:identity/*"
      ]
    }
  ]
}
```

### Network Security

#### VPC Flow Logs
```bash
# Enable VPC Flow Logs
aws ec2 create-flow-logs \
  --resource-type VPC \
  --resource-ids vpc-12345678 \
  --traffic-type ALL \
  --log-destination-type cloud-watch-logs \
  --log-group-name /aws/vpc/flowlogs
```

#### WAF Configuration
```yaml
Rules:
  - AWS Managed Core Rule Set
  - AWS Managed Known Bad Inputs  
  - Rate Limiting: 2000 requests per 5 minutes per IP
  - Geo Blocking: Block high-risk countries
  - SQL Injection Protection
  - XSS Protection
```

## Backup and Disaster Recovery

### Database Backup Strategy

#### Automated Backups
```yaml
RDS Automated Backups:
  Retention Period: 7 days
  Backup Window: 03:00-04:00 UTC
  Maintenance Window: Sun 04:00-05:00 UTC
  
Manual Snapshots:
  Frequency: Before major deployments
  Retention: 30 days
  Cross-region replication: Enabled
```

#### Redis Backup
```yaml
ElastiCache Snapshots:
  Automatic: Daily at 03:00 UTC
  Manual: Before configuration changes
  Retention: 5 days
```

### Disaster Recovery Plan

#### RTO/RPO Targets
- **Recovery Time Objective (RTO)**: 4 hours
- **Recovery Point Objective (RPO)**: 1 hour

#### Recovery Procedures
1. **Database Recovery**: Point-in-time restore from automated backups
2. **Application Recovery**: Deploy from ECR images to new ECS cluster
3. **DNS Switchover**: Update Route 53 records to new ALB
4. **Data Sync**: Restore Redis cache from snapshots

## Operational Procedures

### Deployment Process

```bash
# Update ECS service with new task definition
aws ecs update-service \
  --cluster wechat-cluster \
  --service wechat-backend-service \
  --task-definition wechat-backend-task:LATEST \
  --desired-count 4

# Monitor deployment progress
aws ecs describe-services \
  --cluster wechat-cluster \
  --services wechat-backend-service
```

### Scaling Operations

```bash
# Manual scaling
aws ecs update-service \
  --cluster wechat-cluster \
  --service wechat-backend-service \
  --desired-count 6

# Update auto-scaling configuration
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/wechat-cluster/wechat-backend-service \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy
```

### Maintenance Windows

#### Database Maintenance
- **Schedule**: Every Sunday 04:00-05:00 UTC
- **Activities**: OS patches, minor version updates
- **Notification**: 48 hours advance notice

#### Application Maintenance  
- **Schedule**: As needed with zero-downtime deployments
- **Activities**: Application updates, configuration changes
- **Process**: Blue-green deployment strategy

## Troubleshooting Guide

### Common Issues

#### High CPU Utilization
```bash
# Check ECS service metrics
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=wechat-backend-service \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-01T23:59:59Z \
  --period 300 \
  --statistics Average
```

#### Database Connection Issues
```bash
# Check RDS connection count
aws cloudwatch get-metric-statistics \
  --namespace AWS/RDS \
  --metric-name DatabaseConnections \
  --dimensions Name=DBInstanceIdentifier,Value=wechat-db \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-01T23:59:59Z \
  --period 300 \
  --statistics Maximum
```

#### Application Health Check Failures
```bash
# Check ALB target health
aws elbv2 describe-target-health \
  --target-group-arn arn:aws:elasticloadbalancing:us-east-1:123456789012:targetgroup/wechat-backend/1234567890123456
```

### Log Analysis

```bash
# Query CloudWatch Logs
aws logs filter-log-events \
  --log-group-name /aws/ecs/wechat-backend \
  --start-time 1640995200000 \
  --filter-pattern "ERROR"

# Stream real-time logs
aws logs tail /aws/ecs/wechat-backend --follow
```

## Contact Information

For infrastructure-related questions or issues:
- **DevOps Team**: devops@wechat.com
- **On-Call Engineer**: +1-XXX-XXX-XXXX
- **Slack Channel**: #infrastructure-alerts
