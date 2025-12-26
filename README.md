# Investment Banking Deal Pipeline Management Portal

## Project Overview

A production-grade full-stack application for managing investment banking deal pipelines with enterprise-level security, role-based access control, JWT authentication, and comprehensive deal lifecycle tracking. This system enables investment banking teams to efficiently manage their deal flow from initial prospect through closure.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Quick Start](#quick-start)
- [Environment Configuration](#environment-configuration)
- [API Documentation](#api-documentation)
- [Testing with Postman](#testing-with-postman)
- [Database Schema](#database-schema)
- [Authentication and Authorization](#authentication-and-authorization)
- [Testing](#testing)
- [Local Development](#local-development)
- [Project Structure](#project-structure)
- [Security](#security)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

## Technology Stack

### Backend
- **Framework:** Spring Boot 3.2.1
- **Language:** Java 17 (Amazon Corretto)
- **Database:** MongoDB 7.0
- **Security:** Spring Security with JWT (JSON Web Tokens)
- **Data Access:** Spring Data MongoDB
- **Validation:** Hibernate Validator
- **Testing:** JUnit 5, Mockito
- **Build Tool:** Maven 3.9
- **Test Coverage:** 97% (exceeds 80% requirement)

### Frontend
- **Framework:** Angular 19.0.0
- **UI Library:** Angular Material 19
- **Forms:** Reactive Forms
- **State Management:** RxJS
- **HTTP Client:** Angular HttpClient
- **Testing:** Jasmine, Karma
- **Build Tool:** Angular CLI
- **Test Coverage:** 75% (meets requirement)

### Infrastructure
- **Containerization:** Docker, Docker Compose
- **Web Server:** Nginx (for Angular production build)
- **Database:** MongoDB with persistent volumes
- **Orchestration:** Docker Compose (3-service architecture)

## Architecture

### System Architecture

This application follows a modern three-tier architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Layer                             │
│  Angular 19 SPA with Angular Material (Port 80)             │
│  - AuthGuard & RoleGuard for route protection               │
│  - JWT Interceptor for automatic token attachment           │
│  - Reactive Forms with validation                           │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST + JWT Bearer Token
┌────────────────────────▼────────────────────────────────────┐
│                  Application Layer                           │
│  Spring Boot 3.2.1 REST API (Port 8080)                     │
│  - JWT Authentication & Authorization                        │
│  - Role-Based Access Control (@PreAuthorize)                │
│  - DTO/Mapper pattern with sensitive data filtering         │
│  - Global Exception Handling                                │
└────────────────────────┬────────────────────────────────────┘
                         │ Spring Data MongoDB
┌────────────────────────▼────────────────────────────────────┐
│                   Data Layer                                 │
│  MongoDB 7.0 (Port 27017)                                   │
│  - users collection (authentication & authorization)         │
│  - deals collection (business data)                         │
│  - Indexed fields for performance                           │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns

- **Repository Pattern:** Spring Data MongoDB repositories
- **DTO Pattern:** Separation between entities and API contracts
- **Mapper Pattern:** Role-based data transformation
- **Interceptor Pattern:** JWT token injection, error handling
- **Guard Pattern:** Route protection based on authentication and roles
- **Service Layer Pattern:** Business logic encapsulation

## Features

### Core Functionality

**Deal Management:**
- Create, read, update, and delete deals
- Track deal lifecycle through five stages
- Add timestamped notes to deals
- Filter and search deals by various criteria
- Update sensitive deal value (admin-only)

**User Management:**
- JWT-based authentication
- Role-based access control (USER and ADMIN)
- User creation and management (admin-only)
- Activate/deactivate user accounts

**Security:**
- BCrypt password hashing (strength 10)
- JWT token-based authentication (24-hour expiration)
- Role-based endpoint protection
- CORS configuration
- Input validation on all forms

**Deal Stages:**
1. **Prospect** - Initial contact or interest identified
2. **Under Evaluation** - Due diligence and assessment phase
3. **Term Sheet Submitted** - Formal proposal provided
4. **Closed** - Deal successfully completed
5. **Lost** - Deal did not proceed

### Role-Based Permissions

#### USER Role (Banker)
- Login to the system
- View all deals (deal value hidden)
- Create new deals
- Edit basic deal information (summary, sector, type)
- Update deal stage
- Add notes to deals
- **Cannot** view or modify deal value
- **Cannot** delete deals
- **Cannot** manage users

#### ADMIN Role (Administrator)
- All USER role permissions
- View and edit deal value (sensitive field)
- Delete deals
- Create new users
- Activate/deactivate user accounts
- Full access to user management module

## Quick Start

### Prerequisites

Before running the application, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Available Ports:** 80, 8080, and 27017

### Running the Application with Docker

1. **Clone the Repository**
   ```bash
   git clone https://github.com/akhil033/Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal.git
   cd Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal
   ```

2. **(Optional) Configure JWT Secret**
   
   For production or custom configuration:
   ```bash
   # Copy the example environment file
   cp .env.example .env
   
   # Generate a secure JWT secret
   openssl rand -base64 64
   
   # Edit .env file and paste the generated secret
   # JWT_SECRET=your_generated_secure_key_here
   ```

3. **Start All Services**
   ```bash
   # With default configuration (development)
   docker-compose up --build
   
   # Or with custom .env file
   docker-compose --env-file .env up --build
   ```

   This command will:
   - Build the Angular frontend
   - Build the Spring Boot backend
   - Pull and start MongoDB
   - Configure networking between services
   - Seed initial test data

4. **Access the Application**
   - **Frontend:** http://localhost
   - **Backend API:** http://localhost:8080/api
   - **MongoDB:** localhost:27017

5. **Stop the Application**
   ```bash
   docker-compose down
   ```

6. **Stop and Remove Volumes (Clean Slate)**
   ```bash
   docker-compose down -v
   ```

### Default Test Users

The application automatically seeds test users on first startup:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | ADMIN | Full system access |
| `user1` | `user123` | USER | Standard banker account |
| `user2` | `user123` | USER | Standard banker account |

**Recommended First Login:** Use `admin` / `admin123` for full feature access.

## Environment Configuration

### JWT Secret Setup

The application is **already configured** to use environment variables for the JWT secret key. This is a security best practice that prevents hardcoding sensitive values.

### Quick Configuration Steps

1. **Copy the environment template:**
   ```bash
   cp .env.example .env
   ```

2. **Generate a secure JWT secret (minimum 256 bits):**
   
   Using OpenSSL:
   ```bash
   openssl rand -base64 64
   ```
   
   Using Node.js:
   ```bash
   node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
   ```
   
   Using Python:
   ```bash
   python -c "import secrets; print(secrets.token_urlsafe(64))"
   ```

3. **Update your `.env` file:**
   ```env
   JWT_SECRET=paste_your_generated_secure_key_here
   ```

4. **Run the application:**
   ```bash
   # With .env file
   docker-compose --env-file .env up --build
   
   # Or with inline environment variable
   JWT_SECRET="your_secret" docker-compose up --build
   ```

### Default Configuration

If no custom JWT secret is provided, the application uses a default development key. 

**WARNING:** The default key is for development/testing only. **Never use it in production.**

### Environment Variables Reference

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `JWT_SECRET` | JWT signing key (min 256 bits) | No | Development key |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | No | 86400000 (24h) |
| `SPRING_PROFILES_ACTIVE` | Spring profile (docker/dev/prod) | No | default |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | No | mongodb://mongodb:27017/dealdb |
| `SPRING_DATA_MONGODB_USERNAME` | MongoDB username | No | - |
| `SPRING_DATA_MONGODB_PASSWORD` | MongoDB password | No | - |

### How It Works

The JWT secret is configured using environment variables throughout the application:

**Backend Configuration:**
- `backend/src/main/resources/application.yml`:
  ```yaml
  jwt:
    secret: ${JWT_SECRET:default_value}
    expiration: 86400000
  ```

- `backend/src/main/resources/application-docker.yml`:
  ```yaml
  jwt:
    secret: ${JWT_SECRET:default_value}
  ```

**Docker Configuration:**
- `docker-compose.yml`:
  ```yaml
  backend:
    environment:
      JWT_SECRET: ${JWT_SECRET:-default_value}
  ```

**Environment File:**
- `.env.example` - Template file with instructions
- `.env` - Your actual configuration (gitignored)
- `.gitignore` - Ensures `.env` is never committed

### Verification

To verify your JWT secret is being used correctly:

```bash
# Check environment variable is set in container
docker exec deal-pipeline-backend env | grep JWT_SECRET

# View backend startup logs
docker logs deal-pipeline-backend

# View effective docker-compose configuration
docker-compose config

# Test authentication to ensure JWT is working
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Production Deployment Examples

**AWS (using Secrets Manager):**
```bash
# Store secret in AWS Secrets Manager
aws secretsmanager create-secret \
  --name prod/jwt-secret \
  --secret-string "$(openssl rand -base64 64)"

# Retrieve and deploy
JWT_SECRET=$(aws secretsmanager get-secret-value \
  --secret-id prod/jwt-secret \
  --query SecretString \
  --output text) docker-compose up -d
```

**Azure (using Key Vault):**
```bash
# Store secret in Azure Key Vault
az keyvault secret set \
  --vault-name mykeyvault \
  --name jwt-secret \
  --value "$(openssl rand -base64 64)"

# Retrieve and deploy
JWT_SECRET=$(az keyvault secret show \
  --vault-name mykeyvault \
  --name jwt-secret \
  --query value -o tsv) docker-compose up -d
```

**Google Cloud (using Secret Manager):**
```bash
# Store secret in GCP Secret Manager
echo -n "$(openssl rand -base64 64)" | \
  gcloud secrets create jwt-secret --data-file=-

# Retrieve and deploy
JWT_SECRET=$(gcloud secrets versions access latest \
  --secret=jwt-secret) docker-compose up -d
```

**Kubernetes:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  JWT_SECRET: <base64-encoded-secret>
---
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: backend
        env:
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: JWT_SECRET
```

### Security Best Practices

1. **Generate Strong Secrets:**
   - Minimum 256 bits (32 bytes) for HS512 algorithm
   - Use cryptographically secure random generation
   - Never use predictable patterns or dictionary words

2. **Protect Your Secrets:**
   - Never commit `.env` files to version control
   - Use different secrets for each environment
   - Store production secrets in secure vaults
   - Rotate secrets periodically

3. **Monitor and Audit:**
   - Log authentication attempts
   - Monitor for invalid token signatures
   - Set up alerts for suspicious activity
   - Track secret rotation dates

### Security Notes

- `.env` file is automatically ignored by Git (configured in `.gitignore`)
- Default key is only for development/testing - never use in production
- Changing JWT_SECRET will invalidate all existing tokens (users must re-login)
- Use separate secrets for development, staging, and production environments
- Consider implementing secret rotation strategy for production

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### API Endpoint Summary

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | Public | User authentication |
| GET | `/api/users/me` | USER/ADMIN | Get current user profile |
| GET | `/api/admin/users` | ADMIN | Get all users |
| POST | `/api/admin/users` | ADMIN | Create new user |
| PUT | `/api/admin/users/{id}/status` | ADMIN | Update user status |
| POST | `/api/deals` | USER/ADMIN | Create deal |
| GET | `/api/deals` | USER/ADMIN | Get all deals |
| GET | `/api/deals/{id}` | USER/ADMIN | Get deal by ID |
| PUT | `/api/deals/{id}` | USER/ADMIN | Update deal |
| PATCH | `/api/deals/{id}/stage` | USER/ADMIN | Update deal stage |
| PATCH | `/api/deals/{id}/value` | ADMIN | Update deal value |
| POST | `/api/deals/{id}/notes` | USER/ADMIN | Add note to deal |
| DELETE | `/api/deals/{id}` | ADMIN | Delete deal |

### Detailed Endpoint Documentation

#### Authentication

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "email": "admin@investbank.com",
  "role": "ADMIN"
}
```

#### User Management

**Get Current User**
```http
GET /api/users/me
Authorization: Bearer {token}

Response (200 OK):
{
  "id": "507f1f77bcf86cd799439011",
  "username": "admin",
  "email": "admin@investbank.com",
  "role": "ADMIN",
  "active": true,
  "createdAt": "2024-01-15T10:30:00"
}
```

**Create User (Admin Only)**
```http
POST /api/admin/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@investbank.com",
  "password": "password123",
  "role": "USER"
}

Response (201 Created):
{
  "id": "507f1f77bcf86cd799439012",
  "username": "newuser",
  "email": "newuser@investbank.com",
  "role": "USER",
  "active": true,
  "createdAt": "2024-01-16T14:20:00"
}
```

#### Deal Management

**Create Deal**
```http
POST /api/deals
Authorization: Bearer {token}
Content-Type: application/json

{
  "clientName": "Acme Corporation",
  "dealType": "M&A",
  "sector": "Technology",
  "dealValue": 50000000,
  "currentStage": "Prospect",
  "summary": "Potential acquisition opportunity"
}

Response (201 Created):
{
  "id": "507f1f77bcf86cd799439013",
  "clientName": "Acme Corporation",
  "dealType": "M&A",
  "sector": "Technology",
  "dealValue": 50000000,
  "currentStage": "Prospect",
  "summary": "Potential acquisition opportunity",
  "notes": [],
  "createdAt": "2024-01-16T15:00:00"
}

Note: All users can view dealValue, but only ADMIN can update it
```

**Update Deal Stage**
```http
PATCH /api/deals/{id}/stage
Authorization: Bearer {token}
Content-Type: application/json

{
  "stage": "UnderEvaluation"
}

Response (200 OK):
{
  "id": "507f1f77bcf86cd799439013",
  "currentStage": "UnderEvaluation",
  ...
}
```

**Add Note to Deal**
```http
POST /api/deals/{id}/notes
Authorization: Bearer {token}
Content-Type: application/json

{
  "note": "Initial meeting completed successfully"
}

Response (200 OK):
{
  "id": "507f1f77bcf86cd799439013",
  "notes": [
    {
      "userId": "507f1f77bcf86cd799439011",
      "note": "Initial meeting completed successfully",
      "timestamp": "2024-01-16T16:00:00"
    }
  ],
  ...
}
```

### Error Responses

All endpoints return standardized error responses:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input data",
  "path": "/api/deals",
  "timestamp": "2024-01-16T16:30:00"
}
```

**Common HTTP Status Codes:**
- `200` - OK (successful request)
- `201` - Created (resource created successfully)
- `204` - No Content (successful deletion)
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid or missing token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found (resource doesn't exist)
- `500` - Internal Server Error

## Testing with Postman

### Quick Setup

1. **Import Postman Collection**
   - Open Postman
   - Click **Import** → Select `Deal-Pipeline-API.postman_collection.json`
   - Collection contains all API endpoints with sample requests

2. **Import Environment**
   - Click **Environments** → **Import**
   - Select `Deal-Pipeline-Local.postman_environment.json`
   - Configures base URL and variables

3. **Select Environment**
   - In top-right dropdown, select **Deal Pipeline Local**
   - Base URL: http://localhost:8080/api

### Testing Workflow

#### Step 1: Authentication
1. Open **Authentication** folder
2. Run **Login as Admin** request
   - Username: `admin`
   - Password: `admin123`
   - JWT token automatically saved to environment

#### Step 2: User Management (Admin)
1. Open **User Management** folder
2. Run requests:
   - **Get Current User** - Verify token works
   - **Get All Users** - List all users
   - **Create User** - Add new user
   - **Update User Status** - Activate/deactivate

#### Step 3: Deal Management
1. Open **Deal Management** folder
2. Run requests in order:
   - **Create Deal** - Add new deal (copy returned `id`)
   - **Get All Deals** - View all deals
   - **Get Deal by ID** - View specific deal
   - **Update Deal** - Modify basic info
   - **Update Deal Stage** - Change stage
   - **Update Deal Value (Admin)** - Modify sensitive field
   - **Add Note** - Add timestamped note
   - **Delete Deal (Admin)** - Remove deal

#### Step 4: Test Role-Based Access
1. Run **Login as User** (username: `user1`, password: `user123`)
2. Retry admin-only requests:
   - **Update Deal Value** → 403 Forbidden
   - **Delete Deal** → 403 Forbidden
   - **Create User** → 403 Forbidden
3. Verify USER can view dealValue but cannot update it

### Environment Variables

Postman environment includes:
- `baseUrl` - http://localhost:8080/api
- `token` - JWT token (auto-set by login)
- `dealId` - Sample deal ID (update after creation)
- `userId` - Sample user ID (update after creation)

### Pre-configured Test Scenarios

1. **Complete Deal Lifecycle** - Create → Update → Add Notes → Close
2. **Role Verification** - Test USER vs ADMIN permissions
3. **Validation Testing** - Invalid inputs and missing fields
4. **Error Handling** - Unauthorized access, not found errors
5. **Sensitive Data** - Deal value update restrictions by role

## Database Schema

### Users Collection

```javascript
{
  "_id": ObjectId,
  "username": String (unique, indexed),
  "email": String (unique, indexed),
  "password": String (BCrypt hashed),
  "role": "USER" | "ADMIN",
  "active": Boolean,
  "createdAt": ISODate
}
```

**Indexes:**
- `username` - Unique index for authentication
- `email` - Unique index for user management

### Deals Collection

```javascript
{
  "_id": ObjectId,
  "clientName": String (indexed),
  "dealType": String,
  "sector": String,
  "dealValue": Long (sensitive - only ADMIN can update),
  "currentStage": "Prospect" | "UnderEvaluation" | "TermSheetSubmitted" | "Closed" | "Lost" (indexed),
  "summary": String,
  "notes": [
    {
      "userId": String,
      "note": String,
      "timestamp": ISODate
    }
  ],
  "createdBy": String,
  "assignedTo": String,
  "createdAt": ISODate,
  "updatedAt": ISODate
}
```

**Indexes:**
- `clientName` - Index for search performance
- `currentStage` - Index for filtering by stage

## Authentication and Authorization

### JWT Token Flow

1. **Login:**
   - User submits username/password
   - Backend validates credentials
   - Backend generates JWT token (HS512 algorithm)
   - Token returned to client with user details

2. **Authenticated Requests:**
   - Client stores token in localStorage
   - JWT Interceptor adds `Authorization: Bearer {token}` header
   - Backend validates token on every request
   - Extracts user info and roles from token

3. **Token Expiration:**
   - Tokens expire after 24 hours
   - Frontend error interceptor detects 401 responses
   - User redirected to login page
   - Token removed from localStorage

### Security Features

- **Password Hashing:** BCrypt with strength 10
- **Token Signing:** HS512 algorithm with secret key
- **Token Expiration:** 24-hour TTL
- **Role-Based Authorization:** `@PreAuthorize` annotations
- **CORS Protection:** Configured for specific origins
- **Input Validation:** Server-side and client-side
- **Sensitive Data Filtering:** DealMapper filters dealValue by role
- **MongoDB Auditing:** Automatic timestamps

## Testing

### Backend Tests (97% Coverage)

**Run Tests:**
```bash
cd backend
mvn test
```

**Generate Coverage Report:**
```bash
mvn test jacoco:report
# Report: target/site/jacoco/index.html
```

**Test Statistics:**
- Total Tests: 96
- Coverage: 97%
- Lines: 1,247/1,286
- Branches: 142/158
- Methods: 289/304

**Test Suites:**
- **Mapper Tests (11):** 100% coverage on DTO mappings
- **Service Tests (27):** Business logic validation
- **Security Tests (19):** JWT, authentication, authorization
- **Controller Tests (39):** REST endpoint validation

### Frontend Tests (75% Coverage)

**Run Tests:**
```bash
cd frontend
npm test
```

**Generate Coverage Report:**
```bash
npm run test:coverage
# Report: coverage/deal-pipeline-frontend/index.html
```

**Test Statistics:**
- Total Tests: 37
- Coverage: 75%
- Statements: 75.45%
- Branches: 70.12%
- Functions: 73.89%
- Lines: 75.12%

**Test Suites:**
- **Component Tests:** Login, Deal List, Deal Detail, User Management
- **Service Tests:** Auth, Deal, Admin services
- **Guard Tests:** AuthGuard, RoleGuard
- **Interceptor Tests:** JWT, Error interceptors

## Local Development

### Backend Development (without Docker)

**Prerequisites:**
- JDK 17 installed
- Maven 3.9+ installed
- MongoDB running on localhost:27017

**Steps:**
```bash
cd backend

# Run application
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Access API
http://localhost:8080/api
```

**Configuration:**
Edit `src/main/resources/application.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/dealdb
jwt:
  secret: your_secret_key_minimum_256_bits
  expiration: 86400000
```

### Frontend Development (without Docker)

**Prerequisites:**
- Node.js 20+ installed
- npm installed

**Steps:**
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Access application
http://localhost:4200
```

**Configuration:**
Edit `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Development Tools

**Backend:**
- IDE: IntelliJ IDEA, Eclipse, VS Code
- Database Client: MongoDB Compass, Studio 3T
- API Testing: Postman, Insomnia

**Frontend:**
- IDE: VS Code, WebStorm
- Browser DevTools: Chrome DevTools, Firefox Developer Tools
- Angular DevTools: Chrome extension

## Project Structure

```
Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal/
│
├── backend/                              # Spring Boot Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/investbank/dealpipeline/
│   │   │   │   ├── config/              # Configuration classes
│   │   │   │   │   ├── SecurityConfig.java       # Spring Security setup
│   │   │   │   │   ├── MongoConfig.java          # MongoDB configuration
│   │   │   │   │   └── CorsConfig.java           # CORS settings
│   │   │   │   ├── controller/          # REST Controllers
│   │   │   │   │   ├── AuthController.java       # Authentication
│   │   │   │   │   ├── UserController.java       # User operations
│   │   │   │   │   ├── AdminController.java      # Admin operations
│   │   │   │   │   └── DealController.java       # Deal CRUD
│   │   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   │   ├── request/                  # Request DTOs
│   │   │   │   │   └── response/                 # Response DTOs
│   │   │   │   ├── exception/           # Exception Handling
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   └── Custom exceptions
│   │   │   │   ├── mapper/              # Entity-DTO Mappers
│   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   └── DealMapper.java  # Role-based filtering
│   │   │   │   ├── model/               # MongoDB Entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Deal.java
│   │   │   │   │   └── Enums (Role, DealStage)
│   │   │   │   ├── repository/          # Data Access
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   └── DealRepository.java
│   │   │   │   ├── security/            # Security Components
│   │   │   │   │   ├── JwtProvider.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   └── CustomUserDetailsService.java
│   │   │   │   ├── service/             # Business Logic
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   └── DealService.java
│   │   │   │   └── util/                # Utilities
│   │   │   └── resources/
│   │   │       ├── application.yml      # Main config
│   │   │       └── application-docker.yml
│   │   └── test/                        # 96 Unit Tests
│   ├── Dockerfile                       # Multi-stage Maven build
│   └── pom.xml                          # Maven dependencies
│
├── frontend/                             # Angular Application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/                    # Core Module
│   │   │   │   ├── guards/              # Route Guards
│   │   │   │   │   ├── auth.guard.ts            # Authentication guard
│   │   │   │   │   └── role.guard.ts            # Role-based guard
│   │   │   │   ├── interceptors/        # HTTP Interceptors
│   │   │   │   │   ├── jwt.interceptor.ts       # Token attachment
│   │   │   │   │   └── error.interceptor.ts     # Error handling
│   │   │   │   ├── models/              # TypeScript Interfaces
│   │   │   │   └── services/            # Core Services
│   │   │   │       ├── auth.service.ts
│   │   │   │       ├── deal.service.ts
│   │   │   │       ├── user.service.ts
│   │   │   │       └── admin.service.ts
│   │   │   ├── features/                # Feature Modules
│   │   │   │   ├── auth/                # Authentication
│   │   │   │   │   └── login/           # Login component
│   │   │   │   ├── deals/               # Deal Management
│   │   │   │   │   ├── deal-list/       # Deal table
│   │   │   │   │   ├── deal-detail/     # Deal details
│   │   │   │   │   ├── deal-form/       # Create deal
│   │   │   │   │   └── deal-edit-dialog/# Edit dialog
│   │   │   │   └── admin/               # Admin Module
│   │   │   │       ├── user-management/ # User CRUD
│   │   │   │       └── create-user-dialog/
│   │   │   ├── shared/                  # Shared Components
│   │   │   │   └── components/
│   │   │   │       ├── header/          # App header
│   │   │   │       └── delete-confirmation-dialog/
│   │   │   ├── app.component.*          # Main layout
│   │   │   ├── app.config.ts            # App configuration
│   │   │   └── app.routes.ts            # Route definitions
│   │   ├── environments/                # Environment configs
│   │   ├── styles.css                   # Global styles
│   │   └── index.html
│   ├── Dockerfile                       # Angular build + Nginx
│   ├── nginx.conf                       # Nginx configuration
│   ├── package.json                     # npm dependencies
│   ├── tsconfig.json                    # TypeScript config
│   └── karma.conf.js                    # Test configuration
│
├── docker-compose.yml                   # Service orchestration
├── Deal-Pipeline-API.postman_collection.json    # Postman tests
├── Deal-Pipeline-Local.postman_environment.json # Postman env
├── .env.example                         # Environment variables template
├── .gitignore                           # Git ignore rules (.env included)
└── README.md                            # This file (comprehensive documentation)
```

## Security

### Best Practices Implemented

1. **Password Security:**
   - BCrypt hashing with strength 10
   - Passwords never exposed in responses
   - Password validation on registration

2. **Token Security:**
   - JWT signed with HS512 algorithm
   - 256-bit secret key minimum
   - 24-hour expiration
   - Tokens stored in localStorage (client-side)

3. **API Security:**
   - All endpoints require authentication (except login)
   - Role-based authorization with `@PreAuthorize`
   - CORS configured for specific origins
   - Input validation on all endpoints

4. **Data Protection:**
   - Sensitive deal value can only be updated by ADMIN role
   - All users can view deal values for transparency
   - Global exception handler prevents data leakage
   - MongoDB indexes for performance

5. **Frontend Security:**
   - AuthGuard prevents unauthorized route access
   - RoleGuard enforces role-based routing
   - Error interceptor handles 401/403 responses
   - XSS protection via Angular sanitization

### JWT Secret Configuration

The application is **already configured** to use environment variables for the JWT secret. See the [Environment Configuration](#environment-configuration) section for detailed setup instructions.

**Quick Setup:**

1. **Copy the example environment file:**
   ```bash
   cp .env.example .env
   ```

2. **Generate a secure JWT secret:**
   ```bash
   # Using OpenSSL
   openssl rand -base64 64
   
   # Or using Node.js
   node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
   ```

3. **Update the .env file with your generated secret:**
   ```env
   JWT_SECRET=your_generated_secure_key_here
   ```

4. **Run with docker-compose:**
   ```bash
   docker-compose --env-file .env up --build
   ```

**Current Implementation:**
- `application.yml`: `jwt.secret: ${JWT_SECRET:default_value}`
- `docker-compose.yml`: `JWT_SECRET: ${JWT_SECRET:-default_value}`
- `.env.example`: Template for environment variables
- `.gitignore`: Ensures `.env` is never committed

**WARNING:** The default key is for development only. Always generate a secure random key for production.

### Security Recommendations for Production

- [ ] Generate and use a secure random JWT secret (minimum 256 bits)
- [ ] Never commit secrets to version control (`.env` already in `.gitignore`)
- [ ] Enable HTTPS/TLS for all communications
- [ ] Configure MongoDB authentication and encryption
- [ ] Implement rate limiting to prevent brute force attacks
- [ ] Add request logging and monitoring
- [ ] Enable CSRF protection for state-changing operations
- [ ] Use secure session management (AWS Secrets Manager, Azure Key Vault, etc.)
- [ ] Implement password complexity requirements
- [ ] Add account lockout after failed login attempts
- [ ] Regular security audits and penetration testing
- [ ] Rotate JWT secret periodically

## Deployment

### Docker Deployment (Recommended)

**Production deployment using Docker Compose:**

1. **Update Configuration:**
   ```bash
   # Set secure JWT secret
   export JWT_SECRET="your_256_bit_secure_random_key"
   ```

2. **Build and Deploy:**
   ```bash
   docker-compose up -d --build
   ```

3. **Verify Services:**
   ```bash
   docker-compose ps
   docker logs deal-pipeline-backend
   docker logs deal-pipeline-frontend
   ```

### Cloud Deployment Options

**AWS:**
- ECS (Elastic Container Service) with Fargate
- DocumentDB for MongoDB
- ALB (Application Load Balancer)
- Route 53 for DNS
- CloudWatch for monitoring

**Azure:**
- Azure Container Instances
- Azure Cosmos DB (MongoDB API)
- Azure Application Gateway
- Azure Monitor

**Google Cloud:**
- Google Kubernetes Engine (GKE)
- Cloud MongoDB Atlas
- Cloud Load Balancing
- Cloud Monitoring

The application uses environment variables for sensitive configuration. See the [Environment Configuration](#environment-configuration) section for comprehensive setup instructions.

**Quick Reference:**

Create a `.env` file (see `.env.example` for template):

```env
# Required: Generate a secure random key
JWT_SECRET=your_256_bit_secure_random_key

# Optional: MongoDB with authentication
SPRING_DATA_MONGODB_URI=mongodb://username:password@your-server:27017/dealdb
SPRING_DATA_MONGODB_USERNAME=your_username
SPRING_DATA_MONGODB_PASSWORD=your_password

# Optional: Profile selection
SPRING_PROFILES_ACTIVE=prod
```

**Generate Secure JWT Secret:**
```bash
# Using OpenSSL
openssl rand -base64 64

# Using Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

**Deploy with Environment Variables:**
```bash
# Using .env file
docker-compose --env-file .env up -d --build

# Or inline
JWT_SECRET="your_secret" docker-compose up -d --build
```

**Frontend (Production):**

Edit `frontend/src/environments/environment.prod.ts`:

**Frontend (Production):**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com/api'
};
```

## Troubleshooting

### Common Issues

**1. Backend won't start**
```bash
# Check MongoDB connection
docker logs deal-pipeline-mongodb

# Verify backend logs
docker logs deal-pipeline-backend

# Test MongoDB connectivity
docker exec -it deal-pipeline-mongodb mongosh --eval "db.adminCommand('ping')"
```

**2. Frontend can't reach backend**
```bash
# Verify backend is running
curl http://localhost:8080/api/auth/login

# Check CORS configuration in SecurityConfig.java
# Verify nginx configuration
docker exec deal-pipeline-frontend cat /etc/nginx/conf.d/default.conf
```

**3. Authentication issues**
```bash
# Check JWT token in browser localStorage
# Key: 'token'
# Verify token hasn't expired (24-hour TTL)

# Check backend logs for auth errors
docker logs deal-pipeline-backend | findstr /I "jwt auth"
```

**4. Database connection failures**
```bash
# Access MongoDB shell
docker exec -it deal-pipeline-mongodb mongosh

# Verify database exists
show dbs
use dealdb
show collections

# Check data
db.users.find()
db.deals.find()
```

**5. Port conflicts**
```bash
# Check if ports are in use
netstat -ano | findstr "80 8080 27017"

# Stop conflicting services or change ports in docker-compose.yml
```

**6. Test failures**
```bash
# Backend: Run with debug
cd backend
mvn test -X

# Frontend: Run with Chrome headless
cd frontend
npm test -- --browsers=ChromeHeadless
```

### Reset Database

To start with a clean database:
```bash
docker-compose down -v
docker-compose up --build
```

This removes all volumes and recreates the database with seed data.

## Contributors

I Developed this as a part of the UpGrad Full Stack Development Capstone Program.

## License

This project is an educational capstone project demonstrating production-grade full-stack development practices.

---

## Summary of Requirements Met

### Technology Requirements
- **Frontend:** Angular 19.0.0 (exceeds Angular 18 requirement)
- **Backend:** Spring Boot 3.2.1 with Java 17
- **Database:** MongoDB 7.0
- **Security:** JWT Authentication with BCrypt
- **Infrastructure:** Docker + Docker Compose

### Feature Requirements
- **Authentication:** JWT-based login with token expiration
- **Authorization:** Role-based access (USER and ADMIN)
- **Deal Management:** Full CRUD with 8 endpoints
- **User Management:** Admin-only user CRUD
- **Deal Stages:** All 5 stages implemented
- **Notes:** Timestamped collaboration

### Security Requirements
- **Password Hashing:** BCrypt strength 10
- **JWT Tokens:** HS512, 24-hour expiration
- **Role Protection:** @PreAuthorize on all endpoints
- **Guards:** AuthGuard and RoleGuard in frontend
- **Sensitive Data:** Deal value updates restricted to ADMIN role

### Testing Requirements
- **Backend Coverage:** 97% (exceeds 80% requirement)
- **Frontend Coverage:** 75% (meets 75% requirement)
- **Unit Tests:** JUnit 5 + Jasmine
- **Integration Tests:** Service and controller tests

### Containerization Requirements
- **Backend Dockerfile:** Multi-stage Maven build
- **Frontend Dockerfile:** Angular build + Nginx
- **MongoDB:** Persistent volume configuration
- **Docker Compose:** Single-command deployment

---
