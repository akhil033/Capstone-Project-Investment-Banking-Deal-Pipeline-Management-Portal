# Investment Banking Deal Pipeline Management Portal

## Project Overview

A production-grade full-stack application for managing investment banking deal pipelines with enterprise-level security, role-based access control, JWT authentication, and comprehensive deal lifecycle tracking. This system enables investment banking teams to efficiently manage their deal flow from initial prospect through closure.

**Production Deployment:** AWS EC2 with Application Load Balancer, Jenkins CI/CD automation, and GitHub webhook integration for continuous deployment.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [AWS Deployment Architecture](#aws-deployment-architecture)
- [Features](#features)
- [Quick Start](#quick-start)
- [Environment Configuration](#environment-configuration)
- [Authentication Credentials](#authentication-credentials)
- [API Documentation](#api-documentation)
- [CI/CD Pipeline](#cicd-pipeline)
- [Testing with Postman](#testing-with-postman)
- [Database Schema](#database-schema)
- [Security](#security)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

## Technology Stack

### Backend
- **Framework:** Spring Boot 3.2.1
- **Language:** Java 17 (Amazon Corretto)
- **Database:** MongoDB 7.0
- **Message Broker:** Apache Kafka 7.5.0
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
- **Cloud Platform:** AWS (EC2, ALB, ECR)
- **Containerization:** Docker, Docker Compose
- **CI/CD:** Jenkins with GitHub Webhooks
- **Web Server:** Nginx (for Angular production build)
- **Database:** MongoDB with persistent volumes
- **Message Broker:** Apache Kafka with Zookeeper

### DevOps Tools
- **Version Control:** Git, GitHub
- **Container Registry:** Amazon ECR
- **Load Balancer:** Application Load Balancer (ALB)
- **CI/CD Server:** Jenkins 2.x LTS
- **Automation:** GitHub Webhooks for automatic deployments

## Architecture

### Local Development Architecture

```
+--------------------------------------------------+
|              Client Browser                       |
|          http://localhost:4200                    |
+------------------------+-------------------------+
                         |
                         | HTTP/REST + JWT
                         v
+--------------------------------------------------+
|          Angular Frontend (Port 4200)            |
|  - AuthGuard & RoleGuard for route protection    |
|  - JWT Interceptor for token attachment          |
|  - Reactive Forms with validation                |
+------------------------+-------------------------+
                         |
                         | REST API Calls
                         v
+--------------------------------------------------+
|        Spring Boot Backend (Port 8080)           |
|  - JWT Authentication & Authorization            |
|  - Role-Based Access Control                     |
|  - DTO/Mapper pattern                            |
|  - Global Exception Handling                     |
+------------------------+-------------------------+
                         |
          +-------------+-------------+
          |                           |
          v                           v
+------------------+        +-------------------+
|  MongoDB (27017) |        | Kafka (9092)      |
|  - users         |        | - deal-events     |
|  - deals         |        | Zookeeper (2181)  |
+------------------+        +-------------------+
```

## AWS Deployment Architecture

### Complete Production Architecture

```
                        +----------------------------------+
                        |       GitHub Repository          |
                        |   (Source Code + Webhook)        |
                        +----------------+-----------------+
                                         |
                                         | Push Event
                                         v
+-------------------------------------------------------------------------+
|                          AWS EC2 Instance (m7i-flex.large)              |
|                          Public IP: 18.206.89.182                        |
|                                                                          |
|  +------------------------------------------------------------------+   |
|  |                    Jenkins CI/CD Server (8090)                   |   |
|  |  Build -> Test -> Push to ECR -> Deploy -> Health Check          |   |
|  +------------------------------------------------------------------+   |
|                                                                          |
|  +------------------------------------------------------------------+   |
|  |                    Application Containers                         |   |
|  |                                                                   |   |
|  |  +-----------------------+      +-----------------------+        |   |
|  |  | Backend (Port 8080)   |      | Frontend (Port 80)    |        |   |
|  |  | Spring Boot + Custom  |      | Angular + Nginx       |        |   |
|  |  | Health Endpoint       |      |                       |        |   |
|  |  +-----------------------+      +-----------------------+        |   |
|  +------------------------------------------------------------------+   |
|                                                                          |
|  +------------------------------------------------------------------+   |
|  |                Infrastructure Services                            |   |
|  |                                                                   |   |
|  |  +-----------+    +-----------+    +------------------------+    |   |
|  |  | MongoDB   |    | Kafka     |    | Zookeeper              |    |   |
|  |  | (27017)   |    | (9092)    |    | (2181)                 |    |   |
|  |  +-----------+    +-----------+    +------------------------+    |   |
|  +------------------------------------------------------------------+   |
+-------------------------------------------------------------------------+
                                         |
                                         | Target Registration
                                         v
                        +----------------------------------+
                        |  Application Load Balancer       |
                        |  deal-pipeline-alb-*.elb.aws...  |
                        +----------------------------------+
                                         |
                    +-------------------+-------------------+
                    |                                       |
                    v                                       v
        +------------------------+            +------------------------+
        | Backend Target Group   |            | Frontend Target Group  |
        | Path: /api/*           |            | Path: /                |
        | Health: /health (200)  |            | Health: / (200)        |
        +------------------------+            +------------------------+
                                         |
                                         v
                        +----------------------------------+
                        |        Public Internet           |
                        |   Users Access Application       |
                        +----------------------------------+
```

### CI/CD Pipeline Flow

```
+------------------+
| Developer        |
| Commits Code     |
+--------+---------+
         |
         | git push
         v
+------------------+
| GitHub Repo      |
| Triggers Webhook |
+--------+---------+
         |
         | HTTP POST
         v
+------------------+
| Jenkins Server   |
| Receives Trigger |
+--------+---------+
         |
         +---> Backend Pipeline              +---> Frontend Pipeline
         |                                   |
         v                                   v
+------------------+                +------------------+
| 1. Checkout Code |                | 1. Checkout Code |
| 2. Build Docker  |                | 2. Build Docker  |
|    (Maven inside)|                |    (npm inside)  |
| 3. Tag Image     |                | 3. Tag Image     |
+--------+---------+                +--------+---------+
         |                                   |
         v                                   v
+------------------+                +------------------+
| AWS ECR Login    |                | AWS ECR Login    |
| Push Image       |                | Push Image       |
+--------+---------+                +--------+---------+
         |                                   |
         v                                   v
+------------------+                +------------------+
| Pull from ECR    |                | Pull from ECR    |
| Stop Old         |                | Stop Old         |
| Start New        |                | Start New        |
+--------+---------+                +--------+---------+
         |                                   |
         v                                   v
+------------------+                +------------------+
| Health Check     |                | Health Check     |
| Verify Running   |                | Verify Running   |
+------------------+                +------------------+
         |                                   |
         +-----------------------------------+
                         |
                         v
                +------------------+
                | Deployment Done  |
                | (2-3 minutes)    |
                +------------------+
```

### Container Network Architecture

```
+----------------------------------------------------------------+
|                   deal-pipeline-network (Docker Bridge)         |
|                                                                 |
|  +----------------+    +----------------+    +----------------+|
|  | Backend        |    | Frontend       |    | Jenkins        ||
|  | (8080)         |<-->| (80)           |<-->| (8090)         ||
|  | Spring Boot    |    | Nginx          |    | CI/CD          ||
|  +-------+--------+    +-------+--------+    +----------------+|
|          |                     |                                |
|          |    +----------------+                                |
|          |    |                                                 |
|          v    v                                                 |
|  +----------------+    +----------------+    +----------------+|
|  | MongoDB        |    | Kafka          |    | Zookeeper      ||
|  | (27017)        |<-->| (9092/29092)   |<-->| (2181)         ||
|  | Database       |    | Message Queue  |    | Coordination   ||
|  +----------------+    +----------------+    +----------------+|
+----------------------------------------------------------------+
```

## Features

### Core Functionality

**Deal Management:**
- Create, view, update, and close deals
- Track deal stages (Prospect, Pitch, Due Diligence, Negotiation, Closed Won/Lost)
- Associate deals with clients and owners
- Financial metrics tracking (deal value, close probability)
- Expected close date management

**User Management (Admin Only):**
- Create new user accounts with username, email, password, and role
- View all users in the system
- Activate/deactivate user accounts
- Enforce password complexity requirements

**Authentication & Authorization:**
- JWT-based authentication
- Role-based access control (ADMIN, USER)
- Secure password hashing with BCrypt
- Token expiration and refresh handling
- Protected routes and API endpoints

### Deal Lifecycle States

```
Prospect -> Pitch -> Due Diligence -> Negotiation -> Closed Won/Lost
```

Each deal transitions through these stages with complete audit trail.

## Quick Start

### Prerequisites

- **Java 17** (Amazon Corretto recommended)
- **Node.js 20+** and npm
- **Docker** and Docker Compose
- **MongoDB 7.0** (via Docker)
- **Git**

### Local Development Setup

1. **Clone the Repository**
```bash
git clone https://github.com/akhil033/Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal.git
cd Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal
```

2. **Start Infrastructure Services**
```bash
docker-compose up -d mongodb kafka zookeeper
```

3. **Start Backend**
```bash
cd backend
./mvnw spring-boot:run
```
Backend will be available at http://localhost:8080

4. **Start Frontend**
```bash
cd frontend
npm install
npm start
```
Frontend will be available at http://localhost:4200

5. **Access Application**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- API Documentation: See Postman collection

## Environment Configuration

### Backend Environment Variables

```properties
# MongoDB Configuration
MONGODB_URI=mongodb://localhost:27017/dealdb

# JWT Configuration
JWT_SECRET=your_secret_key_here
JWT_EXPIRATION=86400000

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Server Configuration
SERVER_PORT=8080
```

### Frontend Environment Variables

**Development (environment.ts):**
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

**Production (environment.prod.ts):**
```typescript
export const environment = {
  production: true,
  apiUrl: 'http://your-alb-url.com/api'
};
```

## Authentication Credentials

### Default Test Users

The application automatically seeds test users on first startup via DataInitializer.java:

| Username | Password | Role | Email |
|----------|----------|------|-------|
| admin | admin123 | ADMIN | admin@investbank.com |
| user1 | user123 | USER | user1@investbank.com |
| user2 | user123 | USER | user2@investbank.com |

**IMPORTANT:** Login using the **username** field (e.g., "admin"), NOT the email address.

**Recommended First Login:** Use `admin` / `admin123` for full feature access.

### Password Requirements

For creating new users:
- Minimum 6 characters
- At least one letter
- At least one number

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | /api/auth/login | User login | Public |
| POST | /api/auth/register | User registration | Public |

### Deal Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | /api/deals | List all deals | Authenticated |
| GET | /api/deals/{id} | Get deal by ID | Authenticated |
| POST | /api/deals | Create new deal | Authenticated |
| PUT | /api/deals/{id} | Update deal | Authenticated |
| DELETE | /api/deals/{id} | Delete deal | Authenticated |
| PUT | /api/deals/{id}/stage | Update deal stage | Authenticated |
| PUT | /api/deals/{id}/close | Close deal | Authenticated |

### User Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | /api/users/me | Get current user | Authenticated |
| GET | /api/admin/users | List all users | ADMIN only |
| POST | /api/admin/users | Create user | ADMIN only |
| PUT | /api/admin/users/{id}/status | Update user status | ADMIN only |

### Health Check Endpoint

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | /health | Application health status | Public |

Returns:
```json
{
  "status": "UP",
  "service": "deal-pipeline-backend"
}
```

## CI/CD Pipeline

### GitHub Webhook Configuration

The application uses GitHub webhooks for automatic deployments:

1. **Webhook URL:** `http://18.206.89.182:8090/github-webhook/`
2. **Content Type:** application/json
3. **Events:** Push events
4. **Status:** Active

### Jenkins Pipeline Scripts

**Backend Pipeline:**
```groovy
pipeline {
    agent any
    environment {
        AWS_REGION = 'us-east-1'
        AWS_ACCOUNT_ID = '242874195027'
        ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/deal-pipeline-backend"
        IMAGE_TAG = "latest"
    }
    stages {
        stage('Checkout') { /* ... */ }
        stage('Build Docker Image') { /* ... */ }
        stage('Push to ECR') { /* ... */ }
        stage('Deploy') { /* ... */ }
        stage('Health Check') { /* ... */ }
    }
}
```

**Frontend Pipeline:**
```groovy
pipeline {
    agent any
    environment {
        AWS_REGION = 'us-east-1'
        AWS_ACCOUNT_ID = '242874195027'
        ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/deal-pipeline-frontend"
        IMAGE_TAG = "latest"
    }
    stages {
        stage('Checkout') { /* ... */ }
        stage('Build Docker Image') { /* ... */ }
        stage('Push to ECR') { /* ... */ }
        stage('Deploy') { /* ... */ }
        stage('Health Check') { /* ... */ }
    }
}
```

### Deployment Workflow

1. Developer commits code and pushes to GitHub
2. GitHub webhook triggers Jenkins pipelines (Backend + Frontend)
3. Jenkins builds Docker images using multi-stage builds
4. Images pushed to Amazon ECR
5. Jenkins pulls latest images and redeploys containers
6. Health checks verify successful deployment
7. Total deployment time: 2-3 minutes

## Testing with Postman

### Import Collection

1. Open Postman
2. Import `Deal-Pipeline-API.postman_collection.json`
3. Import `Deal-Pipeline-Local.postman_environment.json`

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
2. Run CRUD operations:
   - **Create Deal** - Add new deal
   - **Get All Deals** - List all deals
   - **Get Deal by ID** - View specific deal
   - **Update Deal** - Modify deal details
   - **Update Deal Stage** - Progress through pipeline
   - **Close Deal** - Mark as won/lost

### Environment Variables in Postman

The collection uses these variables:
- `baseUrl`: Backend API URL (http://localhost:8080/api)
- `token`: JWT token (auto-populated after login)

## Database Schema

### Users Collection

```json
{
  "_id": "ObjectId",
  "username": "string (unique, required)",
  "email": "string (unique, required)",
  "password": "string (BCrypt hashed, required)",
  "role": "enum [USER, ADMIN]",
  "active": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Indexes:**
- username (unique)
- email (unique)

### Deals Collection

```json
{
  "_id": "ObjectId",
  "dealName": "string (required)",
  "client": "string (required)",
  "dealValue": "number (required)",
  "stage": "enum [Prospect, Pitch, Due Diligence, Negotiation, Closed Won, Closed Lost]",
  "owner": "string (required)",
  "closeDate": "datetime",
  "probability": "number (0-100)",
  "status": "enum [Open, Closed]",
  "createdBy": "string (username)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Indexes:**
- dealName
- client
- stage
- owner
- status

## Security

### Implemented Security Measures

**Authentication:**
- JWT-based stateless authentication
- BCrypt password hashing (strength 10)
- Token expiration (24 hours)
- Secure token storage in HTTP-only cookies (recommended)

**Authorization:**
- Role-based access control (RBAC)
- Method-level security with @PreAuthorize
- Route guards in frontend (AuthGuard, RoleGuard)

**API Security:**
- CORS configuration for allowed origins
- Input validation with Hibernate Validator
- DTO pattern prevents sensitive data exposure
- Global exception handling prevents information leakage

**Infrastructure Security:**
- MongoDB authentication enabled
- Docker network isolation
- Environment variables for secrets
- AWS Security Groups restrict access
- Application Load Balancer for SSL termination

### Security Best Practices

**For Production:**
1. Change default passwords immediately
2. Use strong JWT secret keys (minimum 256 bits)
3. Enable HTTPS/TLS on ALB
4. Implement rate limiting
5. Enable MongoDB authentication
6. Use AWS Secrets Manager for credentials
7. Regular security audits and dependency updates
8. Implement request logging and monitoring

## Deployment

### Local Development with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Remove volumes (reset data)
docker-compose down -v
```

### AWS Production Deployment

For complete AWS deployment instructions, see [AWS-DEPLOYMENT.md](AWS-DEPLOYMENT.md)

**Production Infrastructure:**
- **Instance Type:** m7i-flex.large (2 vCPU, 8GB RAM)
- **Region:** us-east-1
- **Load Balancer:** Application Load Balancer
- **Container Registry:** Amazon ECR
- **CI/CD:** Jenkins with GitHub webhooks
- **Deployment Time:** 2-3 minutes (automatic)

**Production URLs:**
- Application: http://deal-pipeline-alb-445487266.us-east-1.elb.amazonaws.com
- Jenkins: http://18.206.89.182:8090

### Docker Build Commands

**Backend:**
```bash
cd backend
docker build -t deal-pipeline-backend:latest .
docker run -d -p 8080:8080 \
  -e MONGODB_URI=mongodb://mongodb:27017/dealdb \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  deal-pipeline-backend:latest
```

**Frontend:**
```bash
cd frontend
docker build -t deal-pipeline-frontend:latest .
docker run -d -p 80:80 deal-pipeline-frontend:latest
```

## Troubleshooting

### Common Issues and Solutions

#### Backend Not Starting

**Issue:** Backend container exits immediately
```bash
# Check logs
docker logs backend

# Common causes:
# 1. MongoDB not running
docker ps | grep mongodb

# 2. Port 8080 already in use
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # Linux/Mac

# 3. Environment variables not set
docker inspect backend | grep -A 10 Env
```

#### Frontend Not Loading

**Issue:** Frontend shows blank page or errors
```bash
# Check frontend logs
docker logs frontend

# Verify backend connectivity
curl http://localhost:8080/health

# Check browser console for CORS errors
# Update environment.ts with correct API URL
```

#### Authentication Fails

**Issue:** Login returns 401 Unauthorized

**Solutions:**
1. Verify you're using **username** not email
   - Correct: `admin` / `admin123`
   - Wrong: `admin@investbank.com` / `admin123`

2. Check backend logs for errors:
```bash
docker logs backend | grep -i "authentication\|login\|error"
```

3. Verify users exist in database:
```bash
docker exec -it mongodb mongosh
use dealdb
db.users.find({}, {username: 1, email: 1, role: 1, active: 1})
```

4. Re-initialize users if needed:
```bash
docker restart backend
# DataInitializer will run if users collection is empty
```

#### MongoDB Connection Issues

**Issue:** Backend logs show "MongoTimeoutException"
```bash
# Verify MongoDB is running
docker ps | grep mongodb

# Check MongoDB logs
docker logs mongodb

# Restart MongoDB
docker restart mongodb

# Verify connection from backend
docker exec -it backend curl mongodb:27017
```

#### Jenkins Build Failures

**Issue:** Pipeline fails during build

**Common Causes:**
1. **Maven not found:** Using Docker multi-stage build (Maven inside container)
2. **AWS CLI missing:** Install AWS CLI v2 in Jenkins container
3. **Docker socket permission:** Ensure Jenkins can access Docker socket
4. **ECR authentication:** Verify AWS credentials configured

```bash
# Check Jenkins can access Docker
docker exec jenkins docker ps

# Check AWS CLI
docker exec jenkins aws --version

# View build logs
# Jenkins UI -> Job -> Console Output
```

### Health Check Commands

```bash
# Check all containers
docker ps

# Backend health
curl http://localhost:8080/health

# Frontend health
curl http://localhost:80

# MongoDB health
docker exec -it mongodb mongosh --eval "db.adminCommand('ping')"

# Kafka health
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list

# ALB target health (AWS)
aws elbv2 describe-target-health \
  --target-group-arn <target-group-arn> \
  --region us-east-1
```

### Logging

```bash
# Follow backend logs
docker logs -f backend

# View last 100 lines
docker logs --tail 100 backend

# Filter for errors
docker logs backend 2>&1 | grep -i error

# All container logs
docker-compose logs -f
```

## Project Structure

```
Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal/
|
|-- backend/                           # Spring Boot Backend
|   |-- src/
|   |   |-- main/
|   |   |   |-- java/.../dealpipeline/
|   |   |   |   |-- config/           # Security, CORS, MongoDB config
|   |   |   |   |-- controller/       # REST Controllers
|   |   |   |   |-- dto/              # Data Transfer Objects
|   |   |   |   |-- exception/        # Global exception handling
|   |   |   |   |-- mapper/           # Entity to DTO mappers
|   |   |   |   |-- model/            # MongoDB entities
|   |   |   |   |-- repository/       # Spring Data repositories
|   |   |   |   |-- security/         # JWT, UserDetails, Filters
|   |   |   |   |-- service/          # Business logic
|   |   |   |   |-- util/             # Utility classes
|   |   |   |   |-- DealPipelineApplication.java
|   |   |   |-- resources/
|   |   |       |-- application.yml   # Application configuration
|   |   |-- test/                     # Unit and integration tests
|   |-- Dockerfile                    # Multi-stage Docker build
|   |-- pom.xml                       # Maven dependencies
|
|-- frontend/                         # Angular Frontend
|   |-- src/
|   |   |-- app/
|   |   |   |-- core/
|   |   |   |   |-- guards/          # Auth and role guards
|   |   |   |   |-- interceptors/    # HTTP interceptors
|   |   |   |   |-- models/          # TypeScript interfaces
|   |   |   |   |-- services/        # API services
|   |   |   |-- features/
|   |   |   |   |-- admin/           # Admin-only components
|   |   |   |   |-- auth/            # Login component
|   |   |   |   |-- deals/           # Deal management
|   |   |   |-- app.component.*      # Root component
|   |   |   |-- app.config.ts        # App configuration
|   |   |   |-- app.routes.ts        # Route definitions
|   |   |-- environments/            # Environment configs
|   |-- Dockerfile                   # Multi-stage Docker build
|   |-- angular.json                 # Angular CLI config
|   |-- package.json                 # npm dependencies
|
|-- docs/
|   |-- deployment/                  # Deployment scripts
|       |-- *.sh                     # Shell scripts for EC2
|       |-- ec2-trust-policy.json    # IAM policy
|
|-- docker-compose.yml               # Local development services
|-- AWS-DEPLOYMENT.md                # AWS deployment guide
|-- README.md                        # This file
|-- .gitignore                       # Git ignore rules
|-- .env.example                     # Environment template
```

## License

This project is developed as part of an academic capstone project for educational purposes.

## Contact

For questions or support, please contact the development team.

---

**Version:** 1.0  
**Last Updated:** January 16, 2026  
**Status:** Production Ready
