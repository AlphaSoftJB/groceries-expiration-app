# Groceries Expiration Tracker - Java/Spring Boot Backend

This is the Java/Spring Boot backend for the Groceries Expiration Tracking App, using Maven and MySQL.

## Tech Stack

*   **Language**: Java 17
*   **Framework**: Spring Boot 3.2.0
*   **Build Tool**: Maven
*   **Database**: MySQL 8.0
*   **API**: GraphQL (Spring for GraphQL)
*   **ORM**: Spring Data JPA with Hibernate

## Prerequisites

*   Java 17 or higher
*   Maven 3.6+
*   MySQL 8.0+

## Local Development Setup

### 1. Install MySQL

Make sure MySQL is running on your local machine. The default configuration expects:
*   Host: `localhost`
*   Port: `3306`
*   Database: `groceries_app_db` (will be created automatically)
*   Username: `root`
*   Password: `root`

You can modify these settings in `src/main/resources/application.properties`.

### 2. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### 3. Access GraphQL Playground

Once the application is running, you can access the GraphiQL interface at:

```
http://localhost:8080/graphiql
```

## GraphQL API

The GraphQL endpoint is available at `/graphql`.

### Example Queries

**Create a User:**
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

**Create an Item:**
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

**Get Items by Household:**
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

**Get Sustainability Metrics:**
```graphql
query {
  sustainabilityMetrics {
    totalCo2SavedKg
  }
}
```

## Docker Deployment

### Build Docker Image

```bash
docker build -t groceries-backend-java:latest .
```

### Run with Docker

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/groceries_app_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  groceries-backend-java:latest
```

## Project Structure

```
src/
├── main/
│   ├── java/com/groceriesapp/
│   │   ├── config/          # Security and other configurations
│   │   ├── controller/      # GraphQL controllers
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── service/         # Business logic
│   │   └── GroceriesAppApplication.java
│   └── resources/
│       ├── application.properties
│       └── graphql/
│           └── schema.graphqls
└── test/
    └── java/com/groceriesapp/
```

## Configuration

All configuration is in `src/main/resources/application.properties`. Key settings:

*   **Database**: MySQL connection details
*   **JPA**: Hibernate DDL auto-update (set to `update` for development)
*   **GraphQL**: GraphiQL enabled for development
*   **Logging**: Debug level for application and SQL

## Testing

Run tests with:

```bash
mvn test
```

## Features Implemented

*   ✅ User and Household Management
*   ✅ Inventory Management (CRUD operations)
*   ✅ AI-Powered Predictive Expiration (placeholder)
*   ✅ Sustainability Tracking (CO₂ savings)
*   ✅ Smart Shopping Lists
*   ✅ GraphQL API

## Next Steps

*   Implement actual AI model integration for predictive expiration
*   Add authentication and authorization
*   Implement OCR service integration
*   Add comprehensive unit and integration tests
*   Set up CI/CD pipeline
