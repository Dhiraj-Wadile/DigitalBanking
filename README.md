# Digital Banking & Payment Platform

Full-stack banking application built with Spring Boot 3.4.5, Angular 17, and H2/PostgreSQL.

---

## How to Run (Fastest Way)

### Step 1: Double-click `start.bat`

That's it. It builds the backend, starts the server on port 8080, and seeds demo data automatically.

### Step 2: Open Browser

- **Frontend (Angular)**: `http://localhost:4200`
- **Backend API**: `http://localhost:8080`
- **H2 Console**: `http://localhost:8080/h2-console`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### Step 3: Stop Server

Double-click `stop.bat` or press `Ctrl+C` in the terminal.

---

## Demo Credentials

### Login via Frontend or API

| Username | Password | Role | Full Name |
|----------|----------|------|-----------|
| `admin` | `admin123` | Super Admin | System Administrator |
| `employee` | `employee123` | Employee | Test Employee |
| `customer` | `customer123` | Customer | Rajesh Kumar |
| `priya` | `customer123` | Customer | Priya Sharma |

### H2 Console (Database)

- **JDBC URL**: `jdbc:h2:mem:digitalbanking`
- **Username**: `sa`
- **Password**: *(leave empty)*

---

## Prerequisites

| Software | Version | Notes |
|----------|---------|-------|
| Java (JDK) | 24.0.1 | Set `JAVA_HOME` properly |
| Node.js | 18+ | For Angular frontend |
| npm | 9+ | Comes with Node.js |

No Docker, Redis, RabbitMQ, or PostgreSQL needed for local development. The `local` profile uses H2 in-memory database.

---

## Manual Setup (Without start.bat)

### Backend

```bat
cd backend
.\mvnw.cmd clean package -DskipTests
java -jar target\digital-banking-api-1.0.0.jar --spring.profiles.active=local
```

### Frontend

```bat
cd frontend
npm install
ng serve
```

Open `http://localhost:4200` in your browser.

---

## Application Flow

### 1. Registration Flow
```
User opens /register
  -> Fills: firstName, lastName, email, username, phone, password
  -> POST /api/auth/register
  -> Backend creates User (ROLE_CUSTOMER) + encodes password with BCrypt
  -> Returns JWT access token + refresh token
  -> User is auto-logged in and redirected to /dashboard
```

### 2. Login Flow
```
User opens /login
  -> Enters username + password
  -> POST /api/auth/login
  -> Spring Security AuthenticationManager validates credentials
  -> If account locked (5+ failed attempts), rejects
  -> If valid: resets failed attempts, updates lastLoginAt
  -> Returns JWT access token (1 hour) + refresh token (7 days)
  -> Frontend stores tokens in localStorage
  -> All subsequent requests include Authorization: Bearer <token> header
```

### 3. Account Creation Flow
```
Customer clicks "Create Account" on /accounts page
  -> POST /api/accounts { accountType: "SAVINGS", branchId: 1 }
  -> Backend resolves customer from JWT token
  -> Generates account number: DB + timestamp + random
  -> Sets IFSC code, balance = 0, status = ACTIVE
  -> Returns AccountResponse with account details
```

### 4. Fund Transfer Flow
```
Customer goes to /transactions page
  -> Fills: fromAccount, toAccount, amount, description
  -> POST /api/transactions/transfer
  -> Backend validates:
     1. Both accounts exist
     2. Source account is ACTIVE
     3. Source balance >= amount
  -> Debits source account
  -> Credits destination account
  -> Creates 2 Transaction records (TRANSFER_OUT + TRANSFER_IN)
  -> Returns transaction reference numbers
```

### 5. Payment Flow
```
Customer goes to /payments page
  -> Selects: paymentType (P2P/P2M/BILL), method (UPI/NEFT/RTGS/IMPS)
  -> Fills: accountNumber, amount, beneficiary details
  -> POST /api/payments
  -> Backend validates account is ACTIVE and has sufficient balance
  -> Debits account
  -> Creates Payment record with status COMPLETED
  -> Returns payment reference
```

### 6. Card Flow
```
Customer goes to /cards page
  -> Clicks "Issue Card"
  -> POST /api/cards/issue { accountNumber, cardType: "DEBIT", cardNetwork: "VISA" }
  -> Backend generates 16-digit card number, sets expiry = 3 years
  -> Card created with status PENDING_ACTIVATION
  -> Customer can block/unblock card from the UI
```

### 7. Loan Flow
```
Customer goes to /loans page
  -> Clicks "Apply for Loan"
  -> POST /api/loans/apply { loanType: "PERSONAL", accountNumber, requestedAmount, tenureMonths }
  -> Backend calculates:
     - Interest rate based on type (PERSONAL=12%, HOME=8.5%, CAR=9%, EDUCATION=7.5%)
     - EMI using standard formula: P * r * (1+r)^n / ((1+r)^n - 1)
     - Processing fee
  -> Loan created with status PENDING_APPROVAL
```

---

## All API Endpoints

### Authentication (Public - No Token Required)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|-------------|
| `POST` | `/api/auth/register` | Register new user | `{ firstName, lastName, email, username, password, phone }` |
| `POST` | `/api/auth/login` | Login | `{ username, password }` |
| `POST` | `/api/auth/refresh-token` | Refresh access token | `{ refreshToken }` |
| `POST` | `/api/auth/logout` | Logout (revoke refresh token) | `{ refreshToken }` |
| `POST` | `/api/auth/forgot-password` | Request password reset | `{ email }` |
| `POST` | `/api/auth/reset-password` | Reset password | `{ resetToken, newPassword, confirmPassword }` |

**Login Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
    "refreshToken": "eyJhbGciOiJIUzM4NCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "username": "admin",
    "role": "ROLE_SUPER_ADMIN",
    "fullName": "System Administrator",
    "userId": 1
  }
}
```

### Accounts (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/accounts` | Create account |
| `GET` | `/api/accounts` | Get all accounts for current user |
| `GET` | `/api/accounts/{accountNumber}` | Get account by number |
| `GET` | `/api/accounts/{accountNumber}/balance` | Get account balance |
| `POST` | `/api/accounts/{accountNumber}/freeze` | Freeze account |
| `POST` | `/api/accounts/{accountNumber}/unfreeze` | Unfreeze account |

### Transactions (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/transactions/transfer` | Transfer funds |
| `POST` | `/api/transactions/deposit` | Deposit money |
| `POST` | `/api/transactions/withdraw` | Withdraw money |
| `GET` | `/api/transactions/account/{accountNumber}` | Get transactions (paginated) |
| `GET` | `/api/transactions/{referenceNumber}` | Get transaction by reference |

**Transfer Request:**
```json
{
  "fromAccountNumber": "DB0000000001",
  "toAccountNumber": "DB0000000003",
  "amount": 5000.00,
  "transferType": "INTERNAL",
  "description": "Monthly rent"
}
```

### Payments (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/payments` | Process payment |
| `GET` | `/api/payments` | Get payment history (paginated) |
| `GET` | `/api/payments/{reference}` | Get payment by reference |

**Payment Request:**
```json
{
  "accountNumber": "DB0000000001",
  "amount": 2000.00,
  "paymentType": "P2P",
  "paymentMethod": "UPI",
  "beneficiaryName": "Rahul Verma",
  "beneficiaryAccount": "DB0000000003",
  "beneficiaryIfsc": "DBIN0002",
  "upiId": "rahul@upi"
}
```

### Cards (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/cards/issue` | Issue new card |
| `GET` | `/api/cards` | Get all cards |
| `POST` | `/api/cards/{cardId}/block` | Block card |
| `POST` | `/api/cards/{cardId}/unblock` | Unblock card |
| `PUT` | `/api/cards/{cardId}/controls` | Update card controls |

**Issue Card Request:**
```json
{
  "accountNumber": "DB0000000001",
  "cardType": "DEBIT",
  "cardNetwork": "VISA",
  "isVirtual": false
}
```

### Beneficiaries (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/beneficiaries` | Add beneficiary |
| `GET` | `/api/beneficiaries` | Get all active beneficiaries |
| `DELETE` | `/api/beneficiaries/{id}` | Remove beneficiary |

### Loans (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/loans/apply` | Apply for loan |
| `GET` | `/api/loans` | Get loans (paginated) |
| `GET` | `/api/loans/{loanNumber}` | Get loan details |

**Loan Application Request:**
```json
{
  "loanType": "PERSONAL",
  "accountNumber": "DB0000000001",
  "requestedAmount": 500000,
  "tenureMonths": 36
}
```

### Customer (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/customer/register` | Register customer profile |
| `GET` | `/api/customer/profile` | Get current customer profile |
| `PUT` | `/api/customer/profile` | Update profile |

### Dashboard (Token Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/dashboard` | Get aggregated dashboard data |

---

## Pre-seeded Demo Data

### Branches

| Code | Name | City | IFSC |
|------|------|------|------|
| BR001 | Main Branch - Mumbai | Mumbai | DBIN0001 |
| BR002 | Delhi Branch | New Delhi | DBIN0002 |
| BR003 | Bangalore Branch | Bangalore | DBIN0003 |

### Customer Accounts

| Account Number | Customer | Type | Balance | Branch |
|----------------|----------|------|---------|--------|
| DB0000000001 | Rajesh Kumar | SAVINGS | 50,000.00 | Mumbai |
| DB0000000002 | Rajesh Kumar | CURRENT | 150,000.00 | Mumbai |
| DB0000000003 | Priya Sharma | SAVINGS | 75,000.00 | Delhi |

---

## Security

| Feature | Details |
|---------|---------|
| Authentication | JWT (1 hour access + 7 days refresh) |
| Password Hashing | BCrypt |
| Session | Stateless (no HTTP session) |
| CORS | `localhost:4200`, `localhost:4201` |
| CSRF | Disabled (stateless API) |
| Account Lockout | After 5 failed login attempts |
| Role-Based Access | Customer, Employee, Admin, Super Admin |

### Role Permissions

| Role | Access |
|------|--------|
| `ROLE_CUSTOMER` | Own accounts, transactions, payments, cards, loans |
| `ROLE_EMPLOYEE` | `/api/employee/**` + customer features |
| `ROLE_ADMIN` | `/api/admin/**` + employee features |
| `ROLE_SUPER_ADMIN` | Full access to everything |

### JWT Configuration
```
Secret:     myUltraSecureJwtKey_1234567890!@#$%^&*()_+abc123
Issuer:     DigitalBankingApp
Audience:   DigitalBankingUsers
Access TTL: 1 hour (3,600,000 ms)
Refresh TTL: 7 days (604,800,000 ms)
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.4.5, Java 24, Spring Security 6, Spring Data JPA |
| Frontend | Angular 17.3, Tailwind CSS 3.4, TypeScript 5.4 |
| Database (Local) | H2 In-Memory (no setup needed) |
| Database (Production) | PostgreSQL 15 with Flyway migrations |
| Build (Backend) | Maven 3.9.9 (wrapper included) |
| Build (Frontend) | npm + Angular CLI |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| JWT Library | JJWT 0.12.5 |
| ORM | Hibernate 6.6.13 |

---

## Project Structure

```
DigitalBanking/
├── start.bat                          # Start the application
├── stop.bat                           # Stop the application
├── backend/
│   ├── mvnw / mvnw.cmd               # Maven wrapper
│   ├── pom.xml                        # Maven dependencies
│   └── src/main/java/com/digitalbanking/
│       ├── DigitalBankingApplication.java   # Entry point
│       ├── config/
│       │   ├── SecurityConfig.java          # JWT + role-based security
│       │   ├── CorsConfig.java              # CORS settings
│       │   ├── DataSeeder.java              # Demo data (local profile)
│       │   └── OpenApiConfig.java           # Swagger config
│       ├── controller/
│       │   ├── auth/AuthController.java     # Login, register, etc.
│       │   ├── account/AccountController.java
│       │   ├── transaction/TransactionController.java
│       │   ├── payment/PaymentController.java
│       │   ├── card/CardController.java
│       │   ├── loan/LoanController.java
│       │   ├── beneficiary/BeneficiaryController.java
│       │   ├── customer/CustomerController.java
│       │   └── dashboard/DashboardController.java
│       ├── entity/
│       │   ├── auth/User.java, RefreshToken.java
│       │   ├── account/Account.java, Branch.java
│       │   ├── transaction/Transaction.java
│       │   ├── payment/Payment.java
│       │   ├── card/Card.java
│       │   ├── loan/Loan.java
│       │   ├── beneficiary/Beneficiary.java
│       │   ├── customer/Customer.java
│       │   └── audit/AuditLog.java
│       ├── service/          # Business logic
│       ├── repository/       # JPA repositories
│       ├── dto/              # Request/Response DTOs
│       ├── mapper/           # Entity-DTO mappers
│       ├── security/         # JWT filter, UserDetailsService
│       └── exception/        # Global exception handler
│   └── src/main/resources/
│       ├── application.yml           # Default config (PostgreSQL)
│       └── application-local.yml     # Local config (H2 in-memory)
├── frontend/
│   ├── package.json
│   └── src/app/
│       ├── auth/             # Login, Register, Forgot Password
│       ├── shared/layout/    # Navigation bar
│       ├── dashboard/        # Dashboard overview
│       ├── accounts/         # Account cards
│       ├── transactions/     # Transfer + history
│       ├── payments/         # Payment form + history
│       ├── cards/            # Card management
│       ├── loans/            # Loan application + list
│       ├── beneficiaries/    # Beneficiary management
│       ├── notifications/    # Notifications placeholder
│       └── profile/          # User profile
└── .github/workflows/ci-cd.yml  # GitHub Actions CI/CD
```

---

## Useful Commands

```bat
# Run backend only (without frontend)
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"

# Build JAR file
cd backend
.\mvnw.cmd clean package -DskipTests

# Run the JAR directly
java -jar backend\target\digital-banking-api-1.0.0.jar --spring.profiles.active=local

# Run frontend only
cd frontend
npm install
ng serve

# Build frontend for production
cd frontend
ng build --configuration production

# Run all tests
cd backend
.\mvnw.cmd test
```

---

## API Response Format

All API responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-07-04T20:45:01.678"
}
```

Error responses:
```json
{
  "success": false,
  "message": "User not found",
  "timestamp": "2026-07-04T20:45:01.678"
}
```

---

## GitHub Repository

```
https://github.com/Dhiraj-Wadile/DigitalBanking
```

---

## License

MIT
