# Deployment Guide
## Groceries Expiration Tracking App - Production Deployment

This guide provides step-by-step instructions for deploying the complete Groceries Expiration Tracking App with Nutrition & Allergen features to production.

---

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Prerequisites](#prerequisites)
3. [Backend Deployment](#backend-deployment)
4. [Frontend Deployment](#frontend-deployment)
5. [Database Setup](#database-setup)
6. [Environment Configuration](#environment-configuration)
7. [Monitoring & Maintenance](#monitoring--maintenance)

---

## Architecture Overview

### System Components

```
┌─────────────────┐
│   Web Frontend  │  (React + Apollo Client)
│   Port: 3000    │
└────────┬────────┘
         │
         │ GraphQL over HTTP
         │
┌────────▼────────┐
│  Java Backend   │  (Spring Boot + GraphQL)
│   Port: 8080    │
└────────┬────────┘
         │
         │ JDBC
         │
┌────────▼────────┐
│  MySQL Database │  (TiDB Cloud / MySQL 8.0+)
│   Port: 3306    │
└─────────────────┘
```

### Technology Stack

**Backend**:
- Java 17+
- Spring Boot 3.x
- Spring GraphQL
- Spring Data JPA
- MySQL Driver

**Frontend**:
- React 19
- TypeScript
- Apollo Client 3.x
- Tailwind CSS 4
- Wouter (routing)

**Database**:
- MySQL 8.0+ or TiDB Cloud
- 9 nutrition-related tables
- Full-text search support

---

## Prerequisites

### Required Software
- Java JDK 17 or higher
- Node.js 18+ and pnpm
- MySQL 8.0+ or TiDB Cloud account
- Git
- Docker (optional, for containerized deployment)

### Required Accounts
- Cloud hosting provider (AWS, GCP, Azure, or DigitalOcean)
- Database hosting (TiDB Cloud, AWS RDS, or self-hosted MySQL)
- Domain name (optional)
- SSL certificate (Let's Encrypt recommended)

---

## Backend Deployment

### Option 1: JAR Deployment (Recommended)

#### Step 1: Build the Application

```bash
cd backend-java

# Clean and build
mvn clean package -DskipTests

# The JAR will be created at:
# target/groceries-expiration-tracker-1.0.0.jar
```

#### Step 2: Configure Production Properties

Create `application-prod.properties`:

```properties
# Server Configuration
server.port=8080
server.address=0.0.0.0

# Database Configuration
spring.datasource.url=jdbc:mysql://your-db-host:4000/groceries_db?sslMode=VERIFY_IDENTITY
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=false

# GraphQL Configuration
spring.graphql.graphiql.enabled=false
spring.graphql.path=/graphql
spring.graphql.schema.printer.enabled=false

# Logging
logging.level.root=INFO
logging.level.com.groceries=INFO
logging.file.name=/var/log/groceries-app/application.log

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# CORS Configuration
cors.allowed-origins=https://yourdomain.com,https://www.yourdomain.com
```

#### Step 3: Run the Application

```bash
# Set environment variables
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export JWT_SECRET=your_secure_jwt_secret_key_here

# Run with production profile
java -jar -Dspring.profiles.active=prod \
  -Xms512m -Xmx2048m \
  target/groceries-expiration-tracker-1.0.0.jar
```

#### Step 4: Create Systemd Service (Linux)

Create `/etc/systemd/system/groceries-backend.service`:

```ini
[Unit]
Description=Groceries Expiration Tracker Backend
After=network.target

[Service]
Type=simple
User=groceries
WorkingDirectory=/opt/groceries-app
ExecStart=/usr/bin/java -jar \
  -Dspring.profiles.active=prod \
  -Xms512m -Xmx2048m \
  /opt/groceries-app/groceries-expiration-tracker-1.0.0.jar

Environment="DB_USERNAME=your_db_user"
Environment="DB_PASSWORD=your_db_password"
Environment="JWT_SECRET=your_jwt_secret"

Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable groceries-backend
sudo systemctl start groceries-backend
sudo systemctl status groceries-backend
```

---

### Option 2: Docker Deployment

#### Step 1: Create Dockerfile

Create `backend-java/Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/groceries-expiration-tracker-1.0.0.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx2048m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### Step 2: Build and Run

```bash
# Build image
docker build -t groceries-backend:latest .

# Run container
docker run -d \
  --name groceries-backend \
  -p 8080:8080 \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e JWT_SECRET=your_jwt_secret \
  --restart unless-stopped \
  groceries-backend:latest
```

#### Step 3: Docker Compose (Full Stack)

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  backend:
    build: ./backend-java
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - db
    restart: unless-stopped

  frontend:
    build: ./web
    ports:
      - "3000:3000"
    environment:
      - VITE_GRAPHQL_URL=http://backend:8080/graphql
    depends_on:
      - backend
    restart: unless-stopped

  db:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
      - MYSQL_DATABASE=groceries_db
      - MYSQL_USER=${DB_USERNAME}
      - MYSQL_PASSWORD=${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    restart: unless-stopped

volumes:
  mysql_data:
```

Run with:

```bash
docker-compose up -d
```

---

## Frontend Deployment

### Option 1: Static Build + Nginx

#### Step 1: Build the Frontend

```bash
cd web

# Install dependencies
pnpm install

# Build for production
pnpm build

# Output will be in client/dist/
```

#### Step 2: Configure Nginx

Create `/etc/nginx/sites-available/groceries-app`:

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Frontend
    root /var/www/groceries-app;
    index index.html;

    # SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # GraphQL API proxy
    location /graphql {
        proxy_pass http://localhost:8080/graphql;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/json;
}
```

Enable and restart:

```bash
sudo ln -s /etc/nginx/sites-available/groceries-app /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

#### Step 3: Deploy Frontend Files

```bash
# Copy built files to web root
sudo mkdir -p /var/www/groceries-app
sudo cp -r client/dist/* /var/www/groceries-app/
sudo chown -R www-data:www-data /var/www/groceries-app
```

---

### Option 2: Vercel/Netlify Deployment

#### Vercel

```bash
cd web

# Install Vercel CLI
pnpm add -g vercel

# Deploy
vercel --prod
```

Configure `vercel.json`:

```json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/" }
  ],
  "env": {
    "VITE_GRAPHQL_URL": "https://api.yourdomain.com/graphql"
  }
}
```

#### Netlify

Create `netlify.toml`:

```toml
[build]
  command = "pnpm build"
  publish = "client/dist"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200

[build.environment]
  VITE_GRAPHQL_URL = "https://api.yourdomain.com/graphql"
```

Deploy:

```bash
# Install Netlify CLI
pnpm add -g netlify-cli

# Deploy
netlify deploy --prod
```

---

## Database Setup

### Option 1: TiDB Cloud (Recommended)

1. **Create Cluster**
   - Go to [TiDB Cloud](https://tidbcloud.com/)
   - Create a new cluster (Developer Tier is free)
   - Note the connection string

2. **Run Schema Migration**
   ```bash
   mysql -h gateway01.us-west-2.prod.aws.tidbcloud.com \
     -P 4000 \
     -u your_username \
     -p \
     -D groceries_db \
     < database/schema.sql
   ```

3. **Verify Tables**
   ```sql
   USE groceries_db;
   SHOW TABLES;
   -- Should show 9 nutrition-related tables
   ```

---

### Option 2: AWS RDS MySQL

1. **Create RDS Instance**
   - Engine: MySQL 8.0
   - Instance class: db.t3.micro (for testing) or larger
   - Storage: 20 GB minimum
   - Enable automated backups
   - Set master password

2. **Configure Security Group**
   - Allow inbound on port 3306 from your backend server IP

3. **Run Schema**
   ```bash
   mysql -h your-rds-endpoint.rds.amazonaws.com \
     -u admin \
     -p \
     -D groceries_db \
     < database/schema.sql
   ```

---

### Option 3: Self-Hosted MySQL

```bash
# Install MySQL 8.0
sudo apt update
sudo apt install mysql-server

# Secure installation
sudo mysql_secure_installation

# Create database
sudo mysql
```

```sql
CREATE DATABASE groceries_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'groceries_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON groceries_db.* TO 'groceries_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

```bash
# Import schema
mysql -u groceries_user -p groceries_db < database/schema.sql
```

---

## Environment Configuration

### Backend Environment Variables

Create `.env` file or set system environment:

```bash
# Database
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_db_password
DB_HOST=your-db-host.com
DB_PORT=4000
DB_NAME=groceries_db

# JWT
JWT_SECRET=your_very_secure_jwt_secret_key_at_least_256_bits

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

### Frontend Environment Variables

Create `.env.production`:

```bash
VITE_GRAPHQL_URL=https://api.yourdomain.com/graphql
VITE_APP_TITLE=Groceries Expiration Tracker
VITE_APP_LOGO=/logo.png
```

---

## SSL/TLS Setup

### Let's Encrypt (Free)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Obtain certificate
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Auto-renewal is configured automatically
# Test renewal:
sudo certbot renew --dry-run
```

---

## Monitoring & Maintenance

### Health Check Endpoints

**Backend Health Check**:
```bash
curl http://localhost:8080/actuator/health
```

Add to `application-prod.properties`:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### Logging

**Backend Logs**:
```bash
# Systemd service
sudo journalctl -u groceries-backend -f

# Log file
tail -f /var/log/groceries-app/application.log
```

**Nginx Logs**:
```bash
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### Database Backups

**Automated Backup Script** (`/opt/scripts/backup-db.sh`):

```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/mysql"
DB_NAME="groceries_db"

mkdir -p $BACKUP_DIR

mysqldump -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD \
  $DB_NAME | gzip > $BACKUP_DIR/groceries_db_$DATE.sql.gz

# Keep only last 7 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: groceries_db_$DATE.sql.gz"
```

Add to crontab:
```bash
# Daily backup at 2 AM
0 2 * * * /opt/scripts/backup-db.sh
```

### Performance Monitoring

**Application Metrics** (Spring Boot Actuator):
- CPU usage: `/actuator/metrics/system.cpu.usage`
- Memory: `/actuator/metrics/jvm.memory.used`
- HTTP requests: `/actuator/metrics/http.server.requests`

**Database Monitoring**:
```sql
-- Check slow queries
SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;

-- Check table sizes
SELECT 
  table_name,
  ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.TABLES
WHERE table_schema = 'groceries_db'
ORDER BY size_mb DESC;
```

---

## Troubleshooting

### Common Issues

**Issue**: Backend won't start
- Check Java version: `java -version` (must be 17+)
- Check database connectivity: `telnet db-host 3306`
- Review logs: `journalctl -u groceries-backend -n 100`

**Issue**: Frontend can't connect to backend
- Check CORS configuration in backend
- Verify VITE_GRAPHQL_URL is correct
- Check network/firewall rules

**Issue**: Database connection timeout
- Verify database is running
- Check security group/firewall rules
- Test connection: `mysql -h host -u user -p`

**Issue**: High memory usage
- Increase JVM heap: `-Xmx4096m`
- Check for memory leaks in logs
- Monitor with: `jmap -heap <pid>`

---

## Production Checklist

### Before Deployment

- [ ] All tests passing (`mvn test`)
- [ ] Database schema migrated
- [ ] Environment variables configured
- [ ] SSL certificates installed
- [ ] CORS origins configured
- [ ] GraphiQL disabled in production
- [ ] Logging configured
- [ ] Backup strategy in place
- [ ] Monitoring set up
- [ ] Security headers configured

### After Deployment

- [ ] Health check endpoint responding
- [ ] GraphQL API accessible
- [ ] Frontend loads correctly
- [ ] User registration works
- [ ] Allergen detection works
- [ ] Consumption logging works
- [ ] Database queries performing well
- [ ] SSL certificate valid
- [ ] Backups running
- [ ] Monitoring alerts configured

---

## Scaling Considerations

### Horizontal Scaling

**Backend**:
- Deploy multiple instances behind load balancer
- Use sticky sessions or stateless JWT auth
- Configure shared cache (Redis)

**Database**:
- Enable read replicas for read-heavy workloads
- Use connection pooling (HikariCP configured by default)
- Consider sharding for large datasets

**Frontend**:
- Use CDN for static assets
- Enable browser caching
- Implement service workers for offline support

---

## Security Best Practices

1. **Use strong JWT secrets** (256-bit minimum)
2. **Enable HTTPS only** (no HTTP)
3. **Implement rate limiting** on API endpoints
4. **Sanitize user inputs** (prevent SQL injection)
5. **Keep dependencies updated** (`mvn versions:display-dependency-updates`)
6. **Use prepared statements** (JPA does this by default)
7. **Implement CSRF protection** for mutations
8. **Enable database SSL** connections
9. **Regular security audits**
10. **Monitor for suspicious activity**

---

## Support & Resources

- **Documentation**: `/API_DOCUMENTATION.md`
- **Testing Guide**: `/E2E_TESTING_GUIDE.md`
- **Implementation Summary**: `/NUTRITION_IMPLEMENTATION_SUMMARY.md`
- **GitHub Issues**: (your repository URL)
- **Email Support**: support@yourdomain.com

---

*Last Updated: November 9, 2025*
*Version: 1.0.0*
