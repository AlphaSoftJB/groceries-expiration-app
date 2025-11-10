# Groceries Expiration Tracking App

This is a comprehensive, full-stack application designed to minimize food waste by tracking grocery expiration dates, providing smart shopping suggestions, and leveraging modern technologies like AI, AR, and Gamification.

## 🚀 Key Features

*   **Core Inventory Management**: CRUD operations for tracking food items, expiration dates, and storage locations.
*   **AI-Powered Insights**: Predictive Expiration Date calculation and OCR-based item scanning.
*   **AR-Powered Navigation**: Conceptual AR view to quickly locate and identify expiring items in the fridge.
*   **Sustainability & Gamification**: CO₂ savings tracking, gamified rewards, and blockchain-ready achievement system.
*   **IoT Integration**: Simulated smart appliance data sync for automated inventory updates.
*   **Smart Shopping List**: Automatically suggests items based on low stock and consumption history.

## 🛠️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Backend** | Kotlin, Spring Boot, Spring Data JPA | Robust, scalable REST/GraphQL API server. |
| **Database** | PostgreSQL (via JPA) | Primary data store for all application data. |
| **API** | GraphQL | Efficient data fetching and manipulation. |
| **Frontend** | React Native (TypeScript) | Cross-platform mobile application (iOS/Android). |
| **AI/ML** | Placeholder Services | Simulates external services for Predictive Expiration and OCR. |
| **CI/CD** | Jenkinsfile, Docker | Automated build, test, and deployment pipeline. |

## 📦 Project Structure

\`\`\`
GroceriesExpirationApp/
├── backend/                  # Kotlin/Spring Boot Backend
│   ├── src/main/kotlin/      # Source code
│   ├── src/main/resources/   # Config, GraphQL schema
│   ├── src/test/kotlin/      # Unit and Integration Tests
│   ├── build.gradle.kts      # Gradle build file
│   └── Dockerfile.ci         # Dockerfile for CI/CD build
├── frontend/                 # React Native Frontend
│   ├── src/components/       # Reusable UI components
│   ├── src/screens/          # Main application screens
│   ├── src/services/         # Apollo Client configuration
│   ├── src/tests/            # Frontend tests
│   └── package.json          # Node dependencies
├── ai/                       # Placeholder for AI/ML models
├── blockchain/               # Placeholder for Smart Contracts
├── infra/                    # Infrastructure as Code (e.g., Terraform)
├── docs/                     # Documentation and Specifications
├── tests/                    # End-to-End Tests (e.g., Cypress, Detox)
└── Jenkinsfile               # CI/CD Pipeline definition
\`\`\`

## ⚙️ Local Development Setup

### 1. Backend Setup (Kotlin/Spring Boot)

1.  **Prerequisites**: Java 17+, Docker (for PostgreSQL).
2.  **Database**: Start a local PostgreSQL instance (e.g., using Docker Compose).
3.  **Build**: Navigate to the \`backend/\` directory and run:
    \`\`\`bash
    ./gradlew bootJar
    \`\`\`
4.  **Run**: Execute the built JAR file:
    \`\`\`bash
    java -jar build/libs/groceries-app-0.0.1-SNAPSHOT.jar
    \`\`\`
    *The application will start on \`http://localhost:8080\` with the GraphQL endpoint at \`http://localhost:8080/graphql\`.*

### 2. Frontend Setup (React Native)

See the detailed instructions in \`frontend/README.md\`.

## 🐳 Deployment

For comprehensive deployment instructions, including cloud deployment options, see [DEPLOYMENT.md](./DEPLOYMENT.md).

### Quick Start with Docker Compose

The fastest way to run the entire stack locally:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database (port 5432)
- Backend API (port 8080)
- Web frontend (port 3000)

Access the web app at http://localhost:3000

### Individual Service Deployment

The backend is containerized for easy deployment.

1.  **Build Docker Image**:
    \`\`\`bash
    cd backend
    docker build -f Dockerfile.ci -t groceries-app-backend:latest .
    \`\`\`
2.  **Run Container**:
    \`\`\`bash
    docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=... groceries-app-backend:latest
    \`\`\`

## ✅ Testing

*   **Backend Tests**: Run unit and integration tests with Gradle:
    \`\`\`bash
    cd backend
    ./gradlew test
    \`\`\`
*   **Frontend Tests**: Run component tests with Jest (requires setup):
    \`\`\`bash
    cd frontend
    npm test
    \`\`\`
*   **Manual Testing**: Use **Postman** or a GraphQL client to test the \`/graphql\` endpoint. Initial setup requires creating a User and Household before adding Items.
\`\`\`
