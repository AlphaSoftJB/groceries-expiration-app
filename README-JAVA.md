# Groceries Expiration Tracking App - Java/Spring Boot Version

This is the complete Groceries Expiration Tracking App with a **Java/Spring Boot backend using Maven and MySQL**.

## Architecture

The application consists of three main components:

1.  **Backend**: Java/Spring Boot with GraphQL API (Port 8080)
2.  **Database**: MySQL 8.0 (Port 3306)
3.  **Web Frontend**: React with Apollo Client (Port 3000)
4.  **Mobile Frontend**: React Native (iOS/Android)

## Quick Start with Docker Compose

The fastest way to run the entire stack:

```bash
docker-compose -f docker-compose-java.yml up -d
```

This will start:
*   MySQL database (port 3306)
*   Java/Spring Boot backend API (port 8080)
*   React web frontend (port 3000)

Access the web app at **http://localhost:3000**

Access the GraphQL playground at **http://localhost:8080/graphiql**

## Manual Setup

### 1. Backend Setup (Java/Spring Boot)

See detailed instructions in `backend-java/README.md`.

**Prerequisites:**
*   Java 17+
*   Maven 3.6+
*   MySQL 8.0+

**Quick Start:**
```bash
cd backend-java
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`.

### 2. Database Setup (MySQL)

**Option 1: Using Docker**
```bash
docker run -d \
  --name groceries_mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=groceries_app_db \
  -p 3306:3306 \
  mysql:8.0
```

**Option 2: Local MySQL**
1.  Install MySQL 8.0+
2.  Create database: `CREATE DATABASE groceries_app_db;`
3.  Update `backend-java/src/main/resources/application.properties` with your credentials

### 3. Web Frontend Setup

See detailed instructions in `web/README.md` (Manus project).

**Quick Start:**
```bash
cd web
pnpm install
pnpm dev
```

The web app will start on `http://localhost:3000`.

### 4. Mobile Frontend Setup (React Native)

See detailed instructions in `frontend/README.md`.

## Tech Stack

### Backend
*   **Language**: Java 17
*   **Framework**: Spring Boot 3.2.0
*   **Build Tool**: Maven
*   **Database**: MySQL 8.0
*   **API**: GraphQL (Spring for GraphQL)
*   **ORM**: Spring Data JPA with Hibernate

### Web Frontend
*   **Framework**: React 19
*   **Build Tool**: Vite
*   **GraphQL Client**: Apollo Client
*   **Styling**: Tailwind CSS 4
*   **UI Components**: shadcn/ui

### Mobile Frontend
*   **Framework**: React Native
*   **GraphQL Client**: Apollo Client
*   **Navigation**: React Navigation

## Features

### Core Features
*   ✅ User and Household Management
*   ✅ Inventory Management (Add, Edit, Delete items)
*   ✅ Expiration Date Tracking
*   ✅ AI-Powered Predictive Expiration
*   ✅ Sustainability Tracking (CO₂ savings)
*   ✅ Smart Shopping Lists
*   ✅ GraphQL API

### Advanced Features (Conceptual/Placeholder)
*   🔄 OCR for Receipt/Label Scanning
*   🔄 AR-Powered Fridge Navigation
*   🔄 IoT Smart Appliance Integration
*   🔄 Gamification & Rewards
*   🔄 Blockchain Integration for Rewards

## GraphQL API Examples

### Create a User
```graphql
mutation {
  createUser(input: {
    email: "john@example.com"
    password: "password123"
    name: "John Doe"
    householdName: "Doe Family"
  }) {
    id
    email
    name
  }
}
```

### Create an Item
```graphql
mutation {
  createItem(input: {
    name: "Milk"
    quantity: 2
    expirationDate: "2025-12-01"
    storageLocation: "Fridge"
    householdId: 1
  }) {
    id
    name
    expirationDate
    predictedExpirationDate
  }
}
```

### Get Items by Household
```graphql
query {
  itemsByHousehold(householdId: 1) {
    id
    name
    quantity
    expirationDate
    predictedExpirationDate
    storageLocation
  }
}
```

### Get Sustainability Metrics
```graphql
query {
  sustainabilityMetrics {
    totalCo2SavedKg
  }
}
```

## Project Structure

```
GroceriesExpirationApp/
├── backend-java/              # Java/Spring Boot backend (NEW)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/groceriesapp/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── graphql/schema.graphqls
│   │   └── test/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
├── web/                       # React web frontend (Manus project)
├── frontend/                  # React Native mobile app
├── docker-compose-java.yml    # Docker Compose for Java stack
├── DEPLOYMENT.md              # Deployment guide
└── README-JAVA.md             # This file
```

## Deployment

For comprehensive deployment instructions, see [DEPLOYMENT.md](./DEPLOYMENT.md).

### Quick Deploy with Docker Compose

```bash
docker-compose -f docker-compose-java.yml up -d
```

### Cloud Deployment Options

*   **AWS**: ECS + RDS MySQL
*   **Google Cloud**: Cloud Run + Cloud SQL
*   **Azure**: App Service + Azure Database for MySQL

## Development

### Running Tests

**Backend:**
```bash
cd backend-java
mvn test
```

**Web Frontend:**
```bash
cd web
pnpm test
```

### Building for Production

**Backend:**
```bash
cd backend-java
mvn clean package
java -jar target/groceries-expiration-tracker-1.0.0.jar
```

**Web Frontend:**
```bash
cd web
pnpm build
pnpm start
```

## Environment Variables

### Backend (`application.properties`)
*   `spring.datasource.url`: MySQL connection URL
*   `spring.datasource.username`: MySQL username
*   `spring.datasource.password`: MySQL password

### Web Frontend (`.env`)
*   `VITE_GRAPHQL_URL`: Backend GraphQL endpoint

## Troubleshooting

### Backend Won't Start
*   Verify MySQL is running: `mysql -u root -p`
*   Check database connection in `application.properties`
*   Review logs: `mvn spring-boot:run`

### Database Connection Issues
*   Ensure MySQL is running on port 3306
*   Verify credentials match `application.properties`
*   Check firewall settings

### Web Frontend Can't Connect
*   Verify backend is running on port 8080
*   Check `VITE_GRAPHQL_URL` environment variable
*   Test GraphQL endpoint: `curl http://localhost:8080/graphql`

## Contributing

Contributions are welcome! Please follow these steps:

1.  Fork the repository
2.  Create a feature branch
3.  Make your changes
4.  Add tests
5.  Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues or questions, please open an issue in the project repository.
