# Groceries Expiration Tracking App - Deployment Guide

This guide provides comprehensive instructions for deploying the Groceries Expiration Tracking App, which consists of a Kotlin/Spring Boot backend, a React Native mobile frontend, and a Next.js web frontend.

## Architecture Overview

The application is composed of three main components:

1.  **Backend**: Kotlin/Spring Boot with GraphQL API (Port 8080)
2.  **Database**: PostgreSQL (Port 5432)
3.  **Web Frontend**: React/Next.js (Port 3000)
4.  **Mobile Frontend**: React Native (iOS/Android)

## Prerequisites

*   Docker and Docker Compose installed
*   Node.js 18+ (for local development)
*   Java 17+ (for backend development)
*   PostgreSQL (if running without Docker)

## Quick Start with Docker Compose

The easiest way to deploy the entire stack is using Docker Compose.

### 1. Environment Configuration

Create a `.env` file in the root directory:

\`\`\`bash
# Database Configuration
DB_USER=postgres
DB_PASSWORD=your_secure_password
DB_NAME=groceries_app_db

# Backend Configuration
SPRING_PROFILES_ACTIVE=default

# Web Frontend Configuration
NEXT_PUBLIC_GRAPHQL_URL=http://localhost:8080/graphql
\`\`\`

### 2. Build and Start All Services

From the root directory (`GroceriesExpirationApp/`):

\`\`\`bash
docker-compose up -d
\`\`\`

This command will:
*   Start a PostgreSQL database
*   Build and start the Kotlin/Spring Boot backend
*   Build and start the React web frontend

### 3. Access the Application

*   **Web Frontend**: http://localhost:3000
*   **Backend GraphQL API**: http://localhost:8080/graphql
*   **Database**: localhost:5432

### 4. Stop All Services

\`\`\`bash
docker-compose down
\`\`\`

To remove all data (including the database volume):

\`\`\`bash
docker-compose down -v
\`\`\`

## Individual Service Deployment

### Backend Deployment

#### Option 1: Docker

\`\`\`bash
cd backend
docker build -t groceries-backend:latest .
docker run -p 8080:8080 \\
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/groceries_app_db \\
  -e SPRING_DATASOURCE_USERNAME=postgres \\
  -e SPRING_DATASOURCE_PASSWORD=postgres \\
  groceries-backend:latest
\`\`\`

#### Option 2: Manual Build

\`\`\`bash
cd backend
./gradlew bootJar
java -jar build/libs/groceries-app-0.0.1-SNAPSHOT.jar
\`\`\`

### Web Frontend Deployment

#### Option 1: Docker

\`\`\`bash
cd web
docker build -t groceries-web:latest .
docker run -p 3000:3000 \\
  -e NEXT_PUBLIC_GRAPHQL_URL=http://localhost:8080/graphql \\
  groceries-web:latest
\`\`\`

#### Option 2: Manual Build

\`\`\`bash
cd web
pnpm install
pnpm run build
pnpm start
\`\`\`

### Database Setup

#### Using Docker

\`\`\`bash
docker run -d \\
  --name groceries_db \\
  -e POSTGRES_USER=postgres \\
  -e POSTGRES_PASSWORD=postgres \\
  -e POSTGRES_DB=groceries_app_db \\
  -p 5432:5432 \\
  postgres:14-alpine
\`\`\`

#### Manual Installation

1.  Install PostgreSQL 14+
2.  Create a database:
    \`\`\`sql
    CREATE DATABASE groceries_app_db;
    \`\`\`
3.  Update the backend's `application.properties` with the connection details.

## Cloud Deployment

### AWS Deployment

#### Using AWS ECS (Elastic Container Service)

1.  **Push Docker Images to ECR**:
    \`\`\`bash
    aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
    
    docker tag groceries-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/groceries-backend:latest
    docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/groceries-backend:latest
    
    docker tag groceries-web:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/groceries-web:latest
    docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/groceries-web:latest
    \`\`\`

2.  **Create ECS Task Definitions** for backend and web frontend.

3.  **Set up RDS PostgreSQL** instance for the database.

4.  **Configure Application Load Balancer** to route traffic to the services.

### Google Cloud Platform (GCP)

#### Using Cloud Run

1.  **Build and Push to Container Registry**:
    \`\`\`bash
    gcloud builds submit --tag gcr.io/<project-id>/groceries-backend backend/
    gcloud builds submit --tag gcr.io/<project-id>/groceries-web web/
    \`\`\`

2.  **Deploy to Cloud Run**:
    \`\`\`bash
    gcloud run deploy groceries-backend \\
      --image gcr.io/<project-id>/groceries-backend \\
      --platform managed \\
      --region us-central1 \\
      --allow-unauthenticated
    
    gcloud run deploy groceries-web \\
      --image gcr.io/<project-id>/groceries-web \\
      --platform managed \\
      --region us-central1 \\
      --allow-unauthenticated
    \`\`\`

3.  **Set up Cloud SQL** for PostgreSQL.

### Vercel (Web Frontend Only)

The web frontend can be deployed to Vercel:

\`\`\`bash
cd web
vercel --prod
\`\`\`

Ensure you set the `NEXT_PUBLIC_GRAPHQL_URL` environment variable in the Vercel dashboard.

## Production Considerations

### Security

1.  **Use HTTPS**: Always use SSL/TLS certificates in production.
2.  **Environment Variables**: Never commit sensitive data. Use secret management services (AWS Secrets Manager, GCP Secret Manager, etc.).
3.  **Database Security**: Use strong passwords, enable SSL connections, and restrict network access.
4.  **CORS Configuration**: Update the backend's CORS settings to only allow your frontend domain.

### Performance

1.  **Database Indexing**: Ensure proper indexes on frequently queried columns.
2.  **Caching**: Implement Redis for caching GraphQL responses.
3.  **CDN**: Use a CDN (CloudFlare, AWS CloudFront) for static assets.

### Monitoring

1.  **Application Monitoring**: Use tools like New Relic, Datadog, or AWS CloudWatch.
2.  **Error Tracking**: Integrate Sentry or similar for error reporting.
3.  **Logging**: Centralize logs using ELK stack or cloud-native solutions.

### Scaling

1.  **Horizontal Scaling**: Use container orchestration (Kubernetes, ECS) to scale services.
2.  **Database Scaling**: Consider read replicas for the PostgreSQL database.
3.  **Load Balancing**: Use a load balancer to distribute traffic across multiple instances.

## Testing the Deployment

### Health Checks

*   **Backend**: `curl http://localhost:8080/actuator/health` (if Spring Actuator is enabled)
*   **Web Frontend**: `curl http://localhost:3000`

### GraphQL Playground

Access the GraphQL playground at `http://localhost:8080/graphql` to test queries and mutations.

### Example Query

\`\`\`graphql
query {
  itemsByHousehold(householdId: "1") {
    id
    name
    expirationDate
  }
}
\`\`\`

## Troubleshooting

### Backend Won't Start

*   Check database connectivity
*   Verify environment variables
*   Review application logs: `docker logs groceries_backend`

### Web Frontend Can't Connect to Backend

*   Ensure `NEXT_PUBLIC_GRAPHQL_URL` is correctly set
*   Check CORS configuration in the backend
*   Verify network connectivity between services

### Database Connection Issues

*   Confirm PostgreSQL is running: `docker ps`
*   Check database credentials
*   Verify network settings (especially in Docker)

## Maintenance

### Database Backups

\`\`\`bash
docker exec groceries_db pg_dump -U postgres groceries_app_db > backup.sql
\`\`\`

### Restore from Backup

\`\`\`bash
docker exec -i groceries_db psql -U postgres groceries_app_db < backup.sql
\`\`\`

### Updating the Application

1.  Pull the latest code
2.  Rebuild Docker images
3.  Run `docker-compose up -d` to restart services with new images

## Support

For issues or questions, please refer to the main README.md or open an issue in the project repository.
