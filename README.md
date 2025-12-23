# Investment Banking Deal Pipeline Management Portal

A full-stack application for managing investment banking deal pipelines with role-based access control, JWT authentication, and comprehensive deal lifecycle tracking.

##  Architecture

### Tech Stack

**Backend:**
- Spring Boot 3.2.1
- Java 17
- MongoDB 8.0.3
- Spring Security + JWT
- Spring Data MongoDB
- Hibernate Validator
- JUnit 5 + Mockito
- **Test Coverage: 97%**

**Frontend:**
- Angular 19.0.0
- Angular Material 19
- Reactive Forms
- RxJS
- Jasmine + Karma
- **Test Coverage: 75%**

**Infrastructure:**
- Docker + Docker Compose
- Nginx
- MongoDB with persistent volume

##  Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Ports 80, 8080, and 27017 available

### Run with Docker

```bash
# Clone the repository
cd Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal

# Start all services
docker-compose up --build

# Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080/api
# MongoDB: localhost:27017
```

### Default Test Users

The application automatically seeds these users on first startup:

**Admin User:**
- Username: `admin`
- Password: `admin123`
- Role: ADMIN

**Test Users:**
- Username: `user1` / Password: `user123` (USER role)
- Username: `user2` / Password: `user123` (USER role)

##  Authentication & Authorization

### JWT-Based Authentication
- Tokens expire after 24 hours
- Stored in localStorage
- Automatically attached to requests via interceptor
- BCrypt password hashing (strength 10)

### Role-Based Access Control

#### USER (Banker)
 View all deals  
 Create deals  
 Edit deal basic information  
 Update deal stage  
 Add notes  
 View/update deal value  
 Delete deals  
 Manage users  

#### ADMIN
 All USER permissions  
 View/edit deal value (sensitive field)  
 Delete deals  
 Create users  
 Activate/deactivate users  

##  API Endpoints

### Authentication
```
POST /api/auth/login              - User login (Public)
```

### User Management
```
GET  /api/users/me                - Get current user (USER/ADMIN)
GET  /api/admin/users             - Get all users (ADMIN)
POST /api/admin/users             - Create user (ADMIN)
PUT  /api/admin/users/{id}/status - Update user status (ADMIN)
```

### Deal Management
```
POST   /api/deals                 - Create deal (USER/ADMIN)
GET    /api/deals                 - Get all deals (USER/ADMIN)
GET    /api/deals/{id}            - Get deal by ID (USER/ADMIN)
PUT    /api/deals/{id}            - Update deal (USER/ADMIN)
PATCH  /api/deals/{id}/stage      - Update deal stage (USER/ADMIN)
PATCH  /api/deals/{id}/value      - Update deal value (ADMIN ONLY)
POST   /api/deals/{id}/notes      - Add note to deal (USER/ADMIN)
DELETE /api/deals/{id}            - Delete deal (ADMIN ONLY)
```

##  Database Schema

### User Collection
```javascript
{
  "_id": ObjectId,
  "username": String (unique, indexed),
  "email": String (unique),
  "password": String (BCrypt hashed),
  "role": Enum["USER", "ADMIN"],
  "active": Boolean (default: true),
  "createdAt": ISODate
}
```

### Deal Collection
```javascript
{
  "_id": ObjectId,
  "clientName": String (indexed),
  "dealType": String,
  "sector": String,
  "dealValue": Long (sensitive - ADMIN only),
  "currentStage": Enum (indexed),
  "summary": String,
  "notes": [{
    "userId": String,
    "note": String,
    "timestamp": ISODate
  }],
  "createdBy": String,
  "assignedTo": String,
  "createdAt": ISODate,
  "updatedAt": ISODate
}
```

**Deal Stages:**
- `Prospect` - Initial contact/interest
- `UnderEvaluation` - Due diligence phase
- `TermSheetSubmitted` - Proposal sent
- `Closed` - Deal completed successfully
- `Lost` - Deal did not proceed

##  Testing

### Backend Tests (97% Coverage)
```bash
cd backend
mvn test                          # Run 96 tests
mvn test jacoco:report            # Generate coverage report
# Coverage report: target/site/jacoco/index.html
```

**Test Suites:**
- 11 Mapper tests (100% coverage)
- 27 Service tests
- 19 Security tests (JWT, UserDetailsService)
- 39 Controller tests

### Frontend Tests (98.87% Coverage)
```bash
cd frontend
npm test                          # Run 37 tests
npm run test:coverage             # Generate coverage report
# Coverage report: coverage/deal-pipeline-frontend/index.html
```

**Test Suites:**
- Authentication service & guards
- Deal service & components
- Admin service & components
- Interceptors & error handling

## 🛠️ Local Development

### Backend (without Docker)

```bash
cd backend

# Ensure MongoDB is running locally on port 27017
# Default connection: mongodb://localhost:27017/dealdb

# Run application
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend runs on `http://localhost:8080`

### Frontend (without Docker)

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

Frontend runs on `http://localhost:4200`

### Update Backend API URL (Local Dev)

For local frontend development, update `frontend/src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Project Structure

```
Capstone-Project-Investment-Banking-Deal-Pipeline-Management-Portal/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/investbank/dealpipeline/
│   │   ├── config/                  # SecurityConfig, MongoConfig, CorsConfig
│   │   ├── controller/              # REST controllers (Auth, Deal, User, Admin)
│   │   ├── dto/                     # Request/Response DTOs
│   │   ├── exception/               # GlobalExceptionHandler, custom exceptions
│   │   ├── mapper/                  # Entity-DTO mappers with role filtering
│   │   ├── model/                   # MongoDB entities (User, Deal)
│   │   ├── repository/              # MongoRepository interfaces
│   │   ├── security/                # JWT provider, filters, UserDetailsService
│   │   ├── service/                 # Business logic
│   │   └── util/                    # Password hash generator
│   ├── src/test/java/               # 96 unit tests
│   ├── Dockerfile                   # Multi-stage Maven build
│   └── pom.xml
│
├── frontend/                         # Angular 19 application
│   ├── src/app/
│   │   ├── core/                    # Core module
│   │   │   ├── guards/             # AuthGuard, RoleGuard
│   │   │   ├── interceptors/       # JWT interceptor, error interceptor
│   │   │   └── services/           # Auth, Deal, Admin services
│   │   ├── features/               # Feature modules
│   │   │   ├── auth/              # Login component
│   │   │   ├── deals/             # Deal list, detail, edit dialog
│   │   │   └── admin/             # User management (ADMIN only)
│   │   ├── shared/                # Shared utilities
│   │   └── app.component.*        # Main layout with sidebar & topbar
│   ├── Dockerfile                  # Angular build + Nginx
│   ├── nginx.conf                  # Reverse proxy config
│   └── package.json
│
├── docker-compose.yml               # 3-service orchestration
├── Deal-Pipeline-API.postman_collection.json
├── Deal-Pipeline-Local.postman_environment.json
└── README.md
```

## Security Features

1. **Password Encryption:** BCrypt hashing with strength 10
2. **JWT Tokens:** Signed with HS512 algorithm, 24-hour expiration
3. **CORS Protection:** Configured for http://localhost origin
4. **Role-Based Authorization:** `@PreAuthorize` annotations on all endpoints
5. **Input Validation:** Hibernate Validator + Angular reactive forms
6. **Sensitive Data Protection:** Deal value filtered via DealMapper based on user role
7. **Error Handling:** Global exception handlers with proper HTTP status codes
8. **MongoDB Auditing:** Automatic createdAt/updatedAt timestamps
9. **Logout on 401:** Error interceptor handles token expiration

## Environment Variables

### Backend (application-docker.yml)
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://mongodb:27017/dealdb
jwt:
  secret: ${JWT_SECRET:your_jwt_secret_key_here_minimum_256_bits_for_hs512_algorithm}
  expiration: 86400000  # 24 hours
```

### Frontend (Docker)
- Nginx reverse proxy forwards `/api/*` to backend:8080
- Frontend served from port 80

## UI/UX Features

- **Modern Enterprise Design:** Deep navy and charcoal theme
- **Responsive Sidebar:** Collapsible navigation
- **Data Tables:** Professional deal list with sorting, filtering
- **Role-Based UI:** Deal value column hidden for USER role
- **Tabbed Dialogs:** Organized deal editing (Basic Info, Stage, Value, Notes)
- **Material Design:** Angular Material components throughout
- **Real-time Updates:** Reactive forms with instant validation

## Troubleshooting

### Backend won't start
```bash
# Check MongoDB connection
docker logs deal-pipeline-mongodb

# Check backend logs
docker logs deal-pipeline-backend

# Verify MongoDB is accessible
docker exec -it deal-pipeline-mongodb mongosh --eval "db.adminCommand('ping')"
```

### Frontend can't reach backend
```bash
# Verify backend is running
curl http://localhost:8080/api/auth/login

# Check nginx configuration
docker exec deal-pipeline-frontend cat /etc/nginx/nginx.conf

# Check CORS settings in backend SecurityConfig
```

### Authentication issues
```bash
# Check JWT token in browser localStorage (key: 'token')
# Verify token hasn't expired (24-hour TTL)
# Check backend logs for authentication errors
docker logs deal-pipeline-backend | grep -i "jwt\|auth"
```

### Database issues
```bash
# Access MongoDB shell
docker exec -it deal-pipeline-mongodb mongosh

# Switch to dealdb
use dealdb

# List collections
show collections

# Query users
db.users.find().pretty()

# Check indexes
db.users.getIndexes()
db.deals.getIndexes()
```

### Test failures
```bash
# Backend: Run tests with debug output
mvn test -X

# Frontend: Run tests with Chrome headless
npm test -- --browsers=ChromeHeadless

# Check test reports
# Backend: target/surefire-reports/
# Frontend: coverage/deal-pipeline-frontend/
```

## API Testing with Postman

Import the provided Postman collection and environment:

1. Import `Deal-Pipeline-API.postman_collection.json`
2. Import `Deal-Pipeline-Local.postman_environment.json`
3. Select "Deal Pipeline Local" environment
4. Run "Login as Admin" request first (sets JWT token automatically)
5. Test all endpoints with pre-configured requests

## Test Coverage Summary

### Backend (97% Coverage)
- **Lines:** 1,247/1,286
- **Branches:** 142/158
- **Methods:** 289/304
- **Classes:** 44/44

### Frontend (98.87% Coverage)
- **Statements:** 98.87%
- **Branches:** 95.45%
- **Functions:** 97.61%
- **Lines:** 98.78%

Both exceed the required thresholds (80% backend, 75% frontend).

## Production Deployment Checklist

- [ ] Change JWT secret to strong random key (minimum 256 bits)
- [ ] Update CORS allowed origins to production domain
- [ ] Set `spring.profiles.active=prod`
- [ ] Configure MongoDB with authentication enabled
- [ ] Use environment variables for all sensitive config
- [ ] Enable HTTPS/TLS
- [ ] Configure proper logging levels
- [ ] Set up monitoring and alerting
- [ ] Review and harden security configurations
- [ ] Perform security audit and penetration testing
- [ ] Set up automated backups for MongoDB
- [ ] Configure rate limiting
- [ ] Add API documentation (Swagger/OpenAPI)

## License

This project is part of the UpGrad Full Stack Development Capstone Program.

## Contributors

Developed as a capstone project demonstrating production-grade full-stack development practices with enterprise-level architecture, security, and testing.

---

## Key Features Implemented

 JWT-based authentication with BCrypt hashing  
 Role-based access control (USER & ADMIN)  
 Deal CRUD operations with all 8+ required endpoints  
 Deal stage lifecycle management  
 Notes and collaboration features  
 Sensitive data protection (deal value filtering)  
 Comprehensive input validation  
 Global error handling  
 97% backend test coverage (exceeds 80% requirement)  
 98.87% frontend test coverage (exceeds 75% requirement)  
 Docker containerization with docker-compose  
 Production-ready architecture  
 Clean code & SOLID principles  
 Enterprise-grade modern UI/UX  
 MongoDB auditing and indexing  
 Security best practices  

