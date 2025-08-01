#!/bin/bash

# WeChat Application Deployment Script
# This script can be used for manual deployment or as a reference

set -e

# Configuration
PROJECT_NAME="wechat"
DOCKER_REGISTRY="pratikmane0112"
VERSION=${1:-latest}
ENVIRONMENT=${2:-staging}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        error "Docker is not running. Please start Docker and try again."
    fi
    log "Docker is running"
}

# Check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed. Please install it and try again."
    fi
    log "Docker Compose is available"
}

# Load environment variables
load_env() {
    if [ -f ".env.${ENVIRONMENT}" ]; then
        log "Loading environment variables from .env.${ENVIRONMENT}"
        source ".env.${ENVIRONMENT}"
    else
        warn "Environment file .env.${ENVIRONMENT} not found. Using default values."
    fi
}

# Build Docker images
build_images() {
    log "Building Docker images..."
    
    # Build backend
    log "Building backend image..."
    docker build -t "${DOCKER_REGISTRY}/${PROJECT_NAME}-backend:${VERSION}" -f backend/Dockerfile backend/
    
    # Build frontend
    log "Building frontend image..."
    docker build -t "${DOCKER_REGISTRY}/${PROJECT_NAME}-frontend:${VERSION}" -f frontend/Dockerfile frontend/
    
    log "Images built successfully"
}

# Push images to registry
push_images() {
    log "Pushing images to Docker registry..."
    
    docker push "${DOCKER_REGISTRY}/${PROJECT_NAME}-backend:${VERSION}"
    docker push "${DOCKER_REGISTRY}/${PROJECT_NAME}-frontend:${VERSION}"
    
    log "Images pushed successfully"
}

# Deploy application
deploy() {
    log "Deploying WeChat application to ${ENVIRONMENT}..."
    
    # Set environment variables for docker-compose
    export DOCKER_HUB_REPO="${DOCKER_REGISTRY}/${PROJECT_NAME}"
    export BACKEND_IMAGE_TAG="${VERSION}"
    export FRONTEND_IMAGE_TAG="${VERSION}"
    
    # Choose compose file based on environment
    if [ "${ENVIRONMENT}" == "production" ]; then
        COMPOSE_FILE="docker-compose.prod.yml"
    else
        COMPOSE_FILE="docker-compose.staging.yml"
    fi
    
    # Stop existing containers
    log "Stopping existing containers..."
    docker-compose -f "${COMPOSE_FILE}" down || true
    
    # Start new containers
    log "Starting new containers..."
    docker-compose -f "${COMPOSE_FILE}" up -d
    
    # Wait for services to be ready
    log "Waiting for services to be ready..."
    sleep 30
    
    # Health check
    check_health
}

# Health check
check_health() {
    log "Performing health checks..."
    
    # Check backend health
    local backend_port
    if [ "${ENVIRONMENT}" == "production" ]; then
        backend_port="5000"
    else
        backend_port="5001"
    fi
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f "http://localhost:${backend_port}/actuator/health" > /dev/null 2>&1; then
            log "Backend health check passed"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            error "Backend health check failed after ${max_attempts} attempts"
        fi
        
        warn "Backend not ready, attempt ${attempt}/${max_attempts}. Waiting 10 seconds..."
        sleep 10
        ((attempt++))
    done
    
    # Check frontend health
    local frontend_port
    if [ "${ENVIRONMENT}" == "production" ]; then
        frontend_port="3000"
    else
        frontend_port="3001"
    fi
    
    if curl -f "http://localhost:${frontend_port}/health" > /dev/null 2>&1; then
        log "Frontend health check passed"
    else
        error "Frontend health check failed"
    fi
    
    log "All health checks passed"
}

# Rollback function
rollback() {
    local previous_version=${1:-"previous"}
    warn "Rolling back to version: ${previous_version}"
    
    export BACKEND_IMAGE_TAG="${previous_version}"
    export FRONTEND_IMAGE_TAG="${previous_version}"
    
    if [ "${ENVIRONMENT}" == "production" ]; then
        COMPOSE_FILE="docker-compose.prod.yml"
    else
        COMPOSE_FILE="docker-compose.staging.yml"
    fi
    
    docker-compose -f "${COMPOSE_FILE}" up -d
    
    log "Rollback completed"
}

# Backup function
backup() {
    log "Creating backup..."
    
    local backup_dir="./backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "${backup_dir}"
    
    # Backup database
    if [ "${ENVIRONMENT}" == "production" ]; then
        docker exec wechat-postgres-prod pg_dump -U "${DB_USERNAME}" wechat_production > "${backup_dir}/database.sql"
    else
        docker exec wechat-postgres-staging pg_dump -U "${DB_USERNAME}" wechat_staging > "${backup_dir}/database.sql"
    fi
    
    # Backup volumes
    docker run --rm -v wechat_postgres_data_${ENVIRONMENT}:/data -v "${PWD}/${backup_dir}":/backup alpine tar czf /backup/postgres_data.tar.gz -C /data .
    
    log "Backup created at ${backup_dir}"
}

# Cleanup function
cleanup() {
    log "Cleaning up unused Docker resources..."
    
    # Remove unused images
    docker image prune -f
    
    # Remove unused volumes
    docker volume prune -f
    
    # Remove unused networks
    docker network prune -f
    
    log "Cleanup completed"
}

# Show logs
show_logs() {
    local service=${1:-""}
    
    if [ "${ENVIRONMENT}" == "production" ]; then
        COMPOSE_FILE="docker-compose.prod.yml"
    else
        COMPOSE_FILE="docker-compose.staging.yml"
    fi
    
    if [ -n "${service}" ]; then
        docker-compose -f "${COMPOSE_FILE}" logs -f "${service}"
    else
        docker-compose -f "${COMPOSE_FILE}" logs -f
    fi
}

# Main deployment function
main() {
    log "Starting WeChat deployment process..."
    log "Version: ${VERSION}"
    log "Environment: ${ENVIRONMENT}"
    
    check_docker
    check_docker_compose
    load_env
    
    case "${3:-deploy}" in
        "build")
            build_images
            ;;
        "push")
            build_images
            push_images
            ;;
        "deploy")
            deploy
            ;;
        "rollback")
            rollback "${4}"
            ;;
        "backup")
            backup
            ;;
        "cleanup")
            cleanup
            ;;
        "logs")
            show_logs "${4}"
            ;;
        "health")
            check_health
            ;;
        *)
            log "Usage: $0 [version] [environment] [action] [options]"
            log "Actions: build, push, deploy, rollback, backup, cleanup, logs, health"
            log "Example: $0 1.0.0 production deploy"
            log "Example: $0 latest staging rollback 1.0.0"
            log "Example: $0 latest staging logs backend"
            exit 1
            ;;
    esac
    
    log "Operation completed successfully!"
}

# Run main function
main "$@"
