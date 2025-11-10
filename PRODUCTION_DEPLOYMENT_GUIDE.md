# Production Deployment Guide

## Overview

This guide provides comprehensive instructions for deploying the Groceries Expiration Tracking App backend to production environments including AWS, Google Cloud, Azure, and Heroku.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Deployment Options](#deployment-options)
3. [Docker Deployment](#docker-deployment)
4. [AWS Deployment](#aws-deployment)
5. [Google Cloud Deployment](#google-cloud-deployment)
6. [Azure Deployment](#azure-deployment)
7. [Heroku Deployment](#heroku-deployment)
8. [Database Setup](#database-setup)
9. [Environment Configuration](#environment-configuration)
10. [SSL/TLS Configuration](#ssltls-configuration)
11. [Monitoring and Logging](#monitoring-and-logging)
12. [Backup and Recovery](#backup-and-recovery)
13. [Security Hardening](#security-hardening)
14. [Performance Optimization](#performance-optimization)
15. [CI/CD Pipeline](#cicd-pipeline)

---

## Prerequisites

- Domain name (recommended)
- SSL certificate (Let's Encrypt or commercial)
- Cloud provider account
- MySQL 8.0 database
- Firebase project configured
- Docker installed (for containerized deployments)
- Git repository

---

## Deployment Options

### Option 1: Docker Compose (Simplest)
**Best for:** Small to medium deployments, development staging

**Pros:**
- Easy setup
- Consistent environment
- Quick deployment

**Cons:**
- Manual scaling
- Limited high availability

### Option 2: AWS Elastic Beanstalk
**Best for:** AWS users, automatic scaling

**Pros:**
- Managed service
- Auto-scaling
- Load balancing
- Easy monitoring

**Cons:**
- AWS-specific
- Can be expensive

### Option 3: Google Cloud Run
**Best for:** Serverless, pay-per-use

**Pros:**
- Serverless
- Auto-scaling
- Pay only for usage
- Easy deployment

**Cons:**
- Cold starts
- GCP-specific

### Option 4: Azure App Service
**Best for:** Microsoft ecosystem

**Pros:**
- Managed service
- Good integration with Azure services
- Auto-scaling

**Cons:**
- Azure-specific
- Can be expensive

### Option 5: Heroku
**Best for:** Quick deployment, startups

**Pros:**
- Easiest deployment
- Free tier available
- Add-ons ecosystem

**Cons:**
- Expensive at scale
- Limited customization

---

## Docker Deployment

### Step 1: Optimize Dockerfile

**File: `backend-java/Dockerfile`**
```dockerfile
# Multi-stage build for smaller image
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy Firebase credentials
COPY src/main/resources/firebase-admin-sdk.json /app/firebase-admin-sdk.json

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "-Xmx512m", "-Xms256m", "app.jar"]
```

### Step 2: Create Production Docker Compose

**File: `docker-compose.prod.yml`**
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: groceries-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: groceries_db
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./mysql-init:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"
    networks:
      - groceries-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend-java
      dockerfile: Dockerfile
    container_name: groceries-backend
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/groceries_db?useSSL=true&requireSSL=true
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      FIREBASE_CREDENTIALS_PATH: file:/app/firebase-admin-sdk.json
      JAVA_OPTS: -Xmx512m -Xms256m
    ports:
      - "8080:8080"
    networks:
      - groceries-network
    volumes:
      - ./logs:/app/logs
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  nginx:
    image: nginx:alpine
    container_name: groceries-nginx
    restart: always
    depends_on:
      - backend
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
      - ./nginx/logs:/var/log/nginx
    networks:
      - groceries-network

volumes:
  mysql_data:
    driver: local

networks:
  groceries-network:
    driver: bridge
```

### Step 3: Configure Nginx

**File: `nginx/nginx.conf`**
```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server backend:8080;
    }

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
    limit_conn_zone $binary_remote_addr zone=conn_limit:10m;

    server {
        listen 80;
        server_name your-domain.com www.your-domain.com;
        
        # Redirect HTTP to HTTPS
        return 301 https://$server_name$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name your-domain.com www.your-domain.com;

        # SSL Configuration
        ssl_certificate /etc/nginx/ssl/fullchain.pem;
        ssl_certificate_key /etc/nginx/ssl/privkey.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers on;

        # Security Headers
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;

        # Logging
        access_log /var/log/nginx/access.log;
        error_log /var/log/nginx/error.log;

        # GraphQL endpoint
        location /graphql {
            limit_req zone=api_limit burst=20 nodelay;
            limit_conn conn_limit 10;

            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_cache_bypass $http_upgrade;
            
            # Timeouts
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        # GraphiQL (disable in production)
        location /graphiql {
            deny all;
            return 404;
        }

        # Health check
        location /actuator/health {
            proxy_pass http://backend;
            access_log off;
        }

        # WebSocket support (if needed)
        location /ws {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "Upgrade";
            proxy_set_header Host $host;
        }
    }
}
```

### Step 4: Deploy with Docker Compose

```bash
# Create environment file
cat > .env << EOF
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_USER=groceries_user
MYSQL_PASSWORD=your_secure_password
EOF

# Build and start services
docker-compose -f docker-compose.prod.yml up -d

# Check logs
docker-compose -f docker-compose.prod.yml logs -f

# Check status
docker-compose -f docker-compose.prod.yml ps
```

---

## AWS Deployment

### Option A: Elastic Beanstalk

#### Step 1: Install EB CLI

```bash
pip install awsebcli
```

#### Step 2: Initialize EB Application

```bash
cd backend-java

# Initialize
eb init -p docker groceries-app --region us-east-1

# Create environment
eb create groceries-prod \
  --instance-type t3.medium \
  --database.engine mysql \
  --database.version 8.0 \
  --envvars \
    SPRING_PROFILES_ACTIVE=prod,\
    FIREBASE_CREDENTIALS_PATH=/app/firebase-admin-sdk.json
```

#### Step 3: Configure Elastic Beanstalk

**File: `.ebextensions/01-environment.config`**
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    SPRING_PROFILES_ACTIVE: prod
    JAVA_OPTS: -Xmx512m -Xms256m
  aws:elasticbeanstalk:environment:proxy:
    ProxyServer: nginx
  aws:autoscaling:launchconfiguration:
    InstanceType: t3.medium
    IamInstanceProfile: aws-elasticbeanstalk-ec2-role
  aws:autoscaling:asg:
    MinSize: 2
    MaxSize: 10
  aws:elasticbeanstalk:healthreporting:system:
    SystemType: enhanced
```

#### Step 4: Deploy

```bash
# Deploy application
eb deploy

# Check status
eb status

# View logs
eb logs

# Open application
eb open
```

### Option B: ECS (Elastic Container Service)

#### Step 1: Create ECR Repository

```bash
# Create repository
aws ecr create-repository --repository-name groceries-app

# Get login command
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789012.dkr.ecr.us-east-1.amazonaws.com
```

#### Step 2: Build and Push Image

```bash
# Build image
docker build -t groceries-app backend-java/

# Tag image
docker tag groceries-app:latest \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/groceries-app:latest

# Push image
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/groceries-app:latest
```

#### Step 3: Create ECS Task Definition

**File: `ecs-task-definition.json`**
```json
{
  "family": "groceries-app",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "groceries-backend",
      "image": "123456789012.dkr.ecr.us-east-1.amazonaws.com/groceries-app:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:region:account:secret:db-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/groceries-app",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

#### Step 4: Create ECS Service

```bash
# Register task definition
aws ecs register-task-definition --cli-input-json file://ecs-task-definition.json

# Create service
aws ecs create-service \
  --cluster groceries-cluster \
  --service-name groceries-service \
  --task-definition groceries-app \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-12345],securityGroups=[sg-12345],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:region:account:targetgroup/groceries-tg,containerName=groceries-backend,containerPort=8080"
```

---

## Google Cloud Deployment

### Option A: Cloud Run

#### Step 1: Build and Push to Container Registry

```bash
# Set project
gcloud config set project groceries-app-project

# Build image
gcloud builds submit --tag gcr.io/groceries-app-project/groceries-backend backend-java/
```

#### Step 2: Deploy to Cloud Run

```bash
# Deploy
gcloud run deploy groceries-backend \
  --image gcr.io/groceries-app-project/groceries-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 1Gi \
  --cpu 1 \
  --max-instances 10 \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod \
  --set-secrets SPRING_DATASOURCE_PASSWORD=db-password:latest

# Get URL
gcloud run services describe groceries-backend --region us-central1 --format 'value(status.url)'
```

### Option B: Google Kubernetes Engine (GKE)

#### Step 1: Create GKE Cluster

```bash
# Create cluster
gcloud container clusters create groceries-cluster \
  --num-nodes 3 \
  --machine-type n1-standard-2 \
  --region us-central1

# Get credentials
gcloud container clusters get-credentials groceries-cluster --region us-central1
```

#### Step 2: Create Kubernetes Deployment

**File: `k8s/deployment.yaml`**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: groceries-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: groceries-backend
  template:
    metadata:
      labels:
        app: groceries-backend
    spec:
      containers:
      - name: backend
        image: gcr.io/groceries-app-project/groceries-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: groceries-backend-service
spec:
  type: LoadBalancer
  selector:
    app: groceries-backend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

#### Step 3: Deploy to GKE

```bash
# Apply deployment
kubectl apply -f k8s/deployment.yaml

# Check status
kubectl get pods
kubectl get services

# Get external IP
kubectl get service groceries-backend-service
```

---

## Azure Deployment

### Option A: Azure App Service

#### Step 1: Create App Service

```bash
# Login to Azure
az login

# Create resource group
az group create --name groceries-rg --location eastus

# Create App Service plan
az appservice plan create \
  --name groceries-plan \
  --resource-group groceries-rg \
  --sku B2 \
  --is-linux

# Create web app
az webapp create \
  --resource-group groceries-rg \
  --plan groceries-plan \
  --name groceries-app \
  --runtime "JAVA:17-java17"
```

#### Step 2: Configure and Deploy

```bash
# Configure app settings
az webapp config appsettings set \
  --resource-group groceries-rg \
  --name groceries-app \
  --settings \
    SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATASOURCE_URL="jdbc:mysql://..." \
    SPRING_DATASOURCE_USERNAME="..." \
    SPRING_DATASOURCE_PASSWORD="..."

# Deploy JAR
az webapp deploy \
  --resource-group groceries-rg \
  --name groceries-app \
  --src-path backend-java/target/groceries-expiration-tracker-1.0.0.jar \
  --type jar
```

---

## Heroku Deployment

### Step 1: Install Heroku CLI

```bash
# Install Heroku CLI
curl https://cli-assets.heroku.com/install.sh | sh

# Login
heroku login
```

### Step 2: Create Heroku App

```bash
cd backend-java

# Create app
heroku create groceries-app

# Add MySQL addon
heroku addons:create jawsdb:kitefin

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set FIREBASE_CREDENTIALS_PATH=/app/firebase-admin-sdk.json
```

### Step 3: Create Procfile

**File: `Procfile`**
```
web: java -jar -Xmx300m -Xss512k target/groceries-expiration-tracker-1.0.0.jar --server.port=$PORT
```

### Step 4: Deploy

```bash
# Initialize git (if not already)
git init
heroku git:remote -a groceries-app

# Deploy
git add .
git commit -m "Deploy to Heroku"
git push heroku main

# Open app
heroku open

# View logs
heroku logs --tail
```

---

## Database Setup

### MySQL Production Configuration

**File: `mysql-init/01-init.sql`**
```sql
-- Create database
CREATE DATABASE IF NOT EXISTS groceries_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER IF NOT EXISTS 'groceries_user'@'%' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON groceries_db.* TO 'groceries_user'@'%';
FLUSH PRIVILEGES;

-- Performance tuning
SET GLOBAL max_connections = 200;
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL innodb_log_file_size = 268435456; -- 256MB
```

### Database Migration

```bash
# Using Flyway (if configured)
mvn flyway:migrate

# Or using Spring Boot
java -jar app.jar --spring.jpa.hibernate.ddl-auto=update
```

---

## Environment Configuration

### Production Application Properties

**File: `application-prod.properties`**
```properties
# Server Configuration
server.port=8080
server.compression.enabled=true
server.http2.enabled=true

# Database Configuration
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# GraphQL Configuration
spring.graphql.graphiql.enabled=false
spring.graphql.path=/graphql

# Logging
logging.level.root=INFO
logging.level.com.groceries=INFO
logging.file.name=/app/logs/application.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true

# Firebase
firebase.credentials.path=${FIREBASE_CREDENTIALS_PATH}
firebase.database.url=${FIREBASE_DATABASE_URL}

# Security
spring.security.jwt.secret=${JWT_SECRET}
spring.security.jwt.expiration=86400000

# CORS
cors.allowed.origins=${CORS_ALLOWED_ORIGINS:*}
```

---

## SSL/TLS Configuration

### Let's Encrypt with Certbot

```bash
# Install Certbot
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Auto-renewal
sudo certbot renew --dry-run
```

### Manual SSL Configuration

```bash
# Generate self-signed certificate (for testing)
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout privkey.pem -out fullchain.pem

# Place certificates
mkdir -p nginx/ssl
cp fullchain.pem nginx/ssl/
cp privkey.pem nginx/ssl/
```

---

## Monitoring and Logging

### Prometheus + Grafana

**File: `docker-compose.monitoring.yml`**
```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"
    networks:
      - groceries-network

  grafana:
    image: grafana/grafana
    volumes:
      - grafana_data:/var/lib/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    networks:
      - groceries-network

volumes:
  prometheus_data:
  grafana_data:

networks:
  groceries-network:
    external: true
```

### ELK Stack (Elasticsearch, Logstash, Kibana)

```yaml
# Add to docker-compose.prod.yml
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
```

---

## Backup and Recovery

### Database Backup Script

**File: `scripts/backup-db.sh`**
```bash
#!/bin/bash

# Configuration
DB_HOST="localhost"
DB_NAME="groceries_db"
DB_USER="groceries_user"
DB_PASS="your_password"
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup database
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME | gzip > $BACKUP_DIR/backup_$DATE.sql.gz

# Delete backups older than 30 days
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete

echo "Backup completed: backup_$DATE.sql.gz"
```

### Automated Backups with Cron

```bash
# Add to crontab
crontab -e

# Daily backup at 2 AM
0 2 * * * /path/to/backup-db.sh
```

---

## Security Hardening

### 1. Environment Variables

Never hardcode secrets! Use environment variables or secret management services.

### 2. Database Security

```sql
-- Restrict user privileges
REVOKE ALL PRIVILEGES ON *.* FROM 'groceries_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON groceries_db.* TO 'groceries_user'@'%';
FLUSH PRIVILEGES;

-- Enable SSL
REQUIRE SSL;
```

### 3. Application Security

```properties
# Enable security headers
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
spring.security.require-ssl=true
```

### 4. Firewall Rules

```bash
# Allow only necessary ports
ufw allow 22/tcp   # SSH
ufw allow 80/tcp   # HTTP
ufw allow 443/tcp  # HTTPS
ufw enable
```

---

## Performance Optimization

### 1. JVM Tuning

```bash
# Optimal JVM flags
JAVA_OPTS="-Xms512m -Xmx1024m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:+OptimizeStringConcat"
```

### 2. Database Indexing

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_expiration_date ON items(expiration_date);
CREATE INDEX idx_user_id ON items(user_id);
CREATE INDEX idx_storage_location ON items(storage_location);
```

### 3. Caching

```properties
# Enable Spring Cache
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=600s
```

---

## CI/CD Pipeline

### GitHub Actions

**File: `.github/workflows/deploy.yml`**
```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: |
        cd backend-java
        mvn clean package -DskipTests
    
    - name: Build Docker image
      run: |
        docker build -t groceries-app backend-java/
        docker tag groceries-app:latest ${{ secrets.DOCKER_REGISTRY }}/groceries-app:latest
    
    - name: Push to Registry
      run: |
        echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
        docker push ${{ secrets.DOCKER_REGISTRY }}/groceries-app:latest
    
    - name: Deploy to Production
      run: |
        # Add your deployment commands here
        # e.g., SSH to server and pull new image
```

---

## Post-Deployment Checklist

- [ ] Application is accessible via HTTPS
- [ ] Database connection is working
- [ ] GraphQL endpoint is responding
- [ ] Push notifications are working
- [ ] Monitoring is set up
- [ ] Logging is configured
- [ ] Backups are scheduled
- [ ] SSL certificate is valid
- [ ] Security headers are set
- [ ] Performance is acceptable
- [ ] Error tracking is enabled
- [ ] Documentation is updated

---

## Troubleshooting

### Common Issues

**Issue: Out of memory errors**
```bash
# Increase JVM memory
JAVA_OPTS="-Xmx2048m"
```

**Issue: Database connection timeout**
```properties
# Increase timeout
spring.datasource.hikari.connection-timeout=60000
```

**Issue: Slow GraphQL queries**
```java
// Add DataLoader for N+1 query problem
// Enable query complexity analysis
```

---

## Support and Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [AWS Documentation](https://docs.aws.amazon.com/)
- [Google Cloud Documentation](https://cloud.google.com/docs)
- [Azure Documentation](https://docs.microsoft.com/azure/)

---

**Last Updated:** November 6, 2025  
**Version:** 1.0.0
