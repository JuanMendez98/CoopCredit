# CoopCredit - Credit Application & Risk Assessment System

## System Overview

CoopCredit is a comprehensive credit application and risk assessment system designed for financial cooperatives. The system consists of three main components:

1. **Credit Application Service** (Port 8080) - Main backend with authentication, affiliate management, and credit processing
2. **Risk Central Mock Service** (Port 8081) - Risk assessment microservice for credit scoring
3. **CoopCredit Frontend** (Port 3000) - User interface for affiliates, analysts, and administrators

### Architecture

```
┌─────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│   Frontend      │────▶│   Credit Service    │────▶│   Risk Service      │
│   (HTML/JS)     │     │   (Spring Boot)     │     │   (Spring Boot)     │
│   Port: 3000    │     │   Port: 8080        │     │   Port: 8081        │
└─────────────────┘     └─────────────────────┘     └─────────────────────┘
                                │
                                ▼
                        ┌─────────────────┐
                        │   PostgreSQL    │
                        │   Port: 5432    │
                        └─────────────────┘
```

### Key Features

- **User Authentication**: JWT-based authentication with role-based access control
- **Affiliate Management**: Complete CRUD operations for affiliate management
- **Credit Application**: End-to-end credit application workflow
- **Risk Assessment**: Integrated risk scoring and evaluation
- **Role-Based Access**: Three distinct user roles with appropriate permissions
- **API Documentation**: Comprehensive OpenAPI/Swagger documentation
- **Containerized Deployment**: Docker Compose for easy deployment

## User Roles & Workflow

### 1. Affiliate (`ROLE_AFFILIATE`)
- Register and login to the system
- View personal dashboard
- Create and manage credit applications
- View application status and history

### 2. Analyst (`ROLE_ANALYST`)
- Review and evaluate credit applications
- Access risk assessment results
- Approve or reject applications
- View all applications in the system

### 3. Administrator (`ROLE_ADMIN`)
- Full system access
- Manage all users and affiliates
- Oversee all credit applications
- System configuration and monitoring

### Application Workflow
```
1. Affiliate Registration → 2. Login → 3. Create Application →
4. Risk Assessment → 5. Analyst Review → 6. Approval/Rejection →
7. Notification → 8. History Tracking
```

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21+ (for local development)
- Node.js 21+ (for frontend development - optional)

### Option 1: Docker Compose (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd CoopCredit

# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps
```

### Option 2: Local Development
```bash
# 1. Start PostgreSQL
docker run -d --name coopcredit-postgres \
  -e POSTGRES_DB=coopcredit \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# 2. Start Risk Central Service
cd risk-central-mock-service
mvn spring-boot:run

# 3. Start Credit Application Service
cd credit-application-service
mvn spring-boot:run

# 4. Start Frontend
cd coopcredit-frontend
# Using any static server, e.g.:
python3 -m http.server 3000
```

## Access Points

| Service             | URL                                   | Description             |
| ------------------- | ------------------------------------- | ----------------------- |
| Frontend            | http://localhost:3000                 | User interface          |
| Credit API          | http://localhost:8080                 | Main backend service    |
| Risk API            | http://localhost:8081                 | Risk assessment service |
| Swagger UI (Credit) | http://localhost:8080/swagger-ui.html | API Documentation       |
| Swagger UI (Risk)   | http://localhost:8081/swagger-ui.html | Risk API Documentation  |
| API Spec (Credit)   | http://localhost:8080/v3/api-docs     | OpenAPI Specification   |
| PostgreSQL          | localhost:5432                        | Database                |

## Authentication & Authorization

### Default Test Users
```json
{
  "affiliate": {
    "email": "affiliate@coopcredit.com",
    "password": "password123",
    "role": "ROLE_AFFILIATE"
  },
  "analyst": {
    "email": "analyst@coopcredit.com",
    "password": "password123",
    "role": "ROLE_ANALYST"
  },
  "admin": {
    "email": "admin@coopcredit.com",
    "password": "password123",
    "role": "ROLE_ADMIN"
  }
}
```

### JWT Token Usage
```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'

# Login and get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securePassword123"
  }'

# Use token for protected endpoints
curl -X GET http://localhost:8080/api/affiliates \
  -H "Authorization: Bearer <your-jwt-token>"
```

## API Endpoints

### Authentication (`/api/auth`)
| Method | Endpoint    | Description             | Auth Required |
| ------ | ----------- | ----------------------- | ------------- |
| POST   | `/register` | Register new user       | No            |
| POST   | `/login`    | Login and get JWT token | No            |

### Affiliates (`/api/affiliates`)
| Method | Endpoint | Description          | Required Role    |
| ------ | -------- | -------------------- | ---------------- |
| GET    | `/`      | List all affiliates  | AFFILIATE, ADMIN |
| POST   | `/`      | Create new affiliate | AFFILIATE, ADMIN |
| GET    | `/{id}`  | Get affiliate by ID  | AFFILIATE, ADMIN |
| PUT    | `/{id}`  | Update affiliate     | AFFILIATE, ADMIN |
| DELETE | `/{id}`  | Delete affiliate     | ADMIN            |

### Credit Requests (`/api/credit-requests`)
| Method | Endpoint         | Description               | Required Role             |
| ------ | ---------------- | ------------------------- | ------------------------- |
| GET    | `/`              | List all credit requests  | AFFILIATE, ANALYST, ADMIN |
| POST   | `/`              | Create new credit request | AFFILIATE, ADMIN          |
| GET    | `/{id}`          | Get request by ID         | AFFILIATE, ANALYST, ADMIN |
| PUT    | `/{id}`          | Update request            | AFFILIATE, ADMIN          |
| DELETE | `/{id}`          | Delete request            | ADMIN                     |
| POST   | `/{id}/evaluate` | Evaluate request          | ANALYST, ADMIN            |

### Risk Assessment (`/api/risk`)
| Method | Endpoint           | Description          |
| ------ | ------------------ | -------------------- |
| POST   | `/risk-evaluation` | Evaluate credit risk |

## Docker Configuration

### Services in docker-compose.yml

1. **postgres**: PostgreSQL 15 database
2. **risk-central-service**: Risk assessment microservice (Port 8081)
3. **credit-application-service**: Main credit application service (Port 8080)
4. **frontend**: Static HTML/JS frontend (Port 3000)

### Environment Variables
Create `.env` file:
```env
# Database
POSTGRES_DB=coopcredit
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# JWT
JWT_SECRET=mySecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmUseInProductionEnv123456
JWT_EXPIRATION=86400000

# Services
CREDIT_SERVICE_PORT=8080
RISK_SERVICE_PORT=8081
FRONTEND_PORT=3000
```

### Build and Run
```bash
# Build all images
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```


### API Testing with cURL
```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Test User","email":"test@example.com","password":"test123"}'

# Test risk assessment
curl -X POST http://localhost:8081/api/risk/risk-evaluation \
  -H "Content-Type: application/json" \
  -d '{"document":"12345678","amount":5000,"term":12}'
```

## Database Schema

### Key Tables
- `users`: System users with roles
- `affiliates`: Affiliate information
- `credit_applications`: Credit requests
- `risk_evaluations`: Risk assessment results

### Migrations
Managed by Flyway with versioned SQL scripts in `src/main/resources/db/migration/`.

## Development

### Backend Services (Spring Boot)
```bash
# Credit Application Service
cd credit-application-service
mvn clean install
mvn spring-boot:run

# Risk Central Service
cd risk-central-mock-service
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd coopcredit-frontend

# Serve static files
python3 -m http.server 3000

# Or use any static server
npx serve -s . -p 3000
```

## Error Handling

The system uses RFC 7807 Problem Details for error responses:

```json
{
  "type": "https://api.coopcredit.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Invalid input parameters",
  "instance": "/api/affiliates",
  "timestamp": "2025-12-09T21:45:30.123Z",
  "errors": {
    "email": ["must be a valid email address"],
    "income": ["must be positive"]
  }
}
```

## Monitoring & Observability

### Health Checks
- Credit Service: `http://localhost:8080/actuator/health`
- Risk Service: `http://localhost:8081/actuator/health`

### Logging
Structured logging with correlation IDs for request tracing.

## Security

- JWT-based authentication
- Role-based authorization (RBAC)
- Password hashing with BCrypt
- CORS configuration
- Input validation and sanitization
- SQL injection prevention



