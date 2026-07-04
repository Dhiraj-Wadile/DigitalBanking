# Digital Banking & Payment Platform

Enterprise-grade Digital Banking & Payment Platform built with Spring Boot 3 Microservices, Angular 17, PostgreSQL, Redis, and RabbitMQ.

---

## Architecture

### Tech Stack
- **Backend**: Java 21, Spring Boot 3.3, Spring Security 6, JWT
- **Frontend**: Angular 17, NgRx, PrimeNG, TailwindCSS, Angular Material
- **Database**: PostgreSQL 15 with Flyway migrations
- **Cache**: Redis 7
- **Messaging**: RabbitMQ 3
- **Microservices**: Spring Cloud Gateway, Eureka Discovery
- **Monitoring**: Prometheus, Grafana
- **Deployment**: Docker Compose

### Microservices
| Service | Port | Description |
|---------|------|-------------|
| Auth Service | 8081 | Authentication & Authorization |
| Account Service | 8082 | Account Management |
| Transaction Service | 8083 | Transaction Processing |
| Payment Service | 8084 | UPI/NEFT/RTGS/IMPS Payments |
| Notification Service | 8085 | Email/SMS/Push Notifications |
| API Gateway | 8080 | Service Routing & Load Balancing |
| Frontend | 4200 | Angular UI |

---

## Quick Start

### Option 1: Automated Setup (Recommended)

```bat
setup.bat
```

This will:
1. Build the backend
2. Install frontend dependencies
3. Start infrastructure (PostgreSQL, Redis, RabbitMQ)
4. Run database migrations

### Option 2: Manual Setup

#### Prerequisites
- Java 21
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

#### Step 1: Start Infrastructure
```bat
start-infra.bat
```

#### Step 2: Build & Run Backend
```bat
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

#### Step 3: Run Frontend
```bat
cd frontend
npm install
ng serve --open
```

### Option 3: Docker Only
```bat
docker-up.bat
```

---

## Database

### Tables (16)
- `users` - User accounts with KYC
- `accounts` - Bank accounts with balances
- `transactions` - Transaction history
- `payments` - UPI/NEFT/RTGS/IMPS
- `beneficiaries` - Saved payees
- `cards` - Credit/Debit cards
- `loans` - Loan accounts
- `notifications` - User notifications
- `audit_logs` - Security audit trail
- `fraud_alerts` - Fraud detection
- `settlement_batches` - Payment settlement
- `reconciliation_records` - Payment reconciliation
- `payment_retry_configs` - Retry policies
- `account_statements` - Monthly statements
- `kyc_documents` - KYC verification
- `otp_verification` - OTP codes

### Stored Procedures (7)
1. `sp_transfer_funds` - Account-to-account transfers
2. `sp_process_payment` - Payment processing
3. `sp_settle_batch` - Settlement processing
4. `sp_generate_statement` - Statement generation
5. `sp_check_fraud` - Fraud detection
6. `sp_process_refund` - Refund processing
7. `sp_reconcile_payments` - Payment reconciliation

### Triggers (10)
- `trg_update_balance_on_transaction` - Balance updates
- `trg_audit_transaction` - Transaction audit
- `trg_check_fraud` - Real-time fraud check
- `trg_generate_otp` - OTP generation
- And 6 more...

### Complex Queries (9)
- Monthly transaction summary (CTE + Window functions)
- Account balance reconciliation (Subqueries)
- Payment success rate analysis (Aggregation)
- Customer spending patterns (GROUP BY CUBE)
- RFM analysis (Correlated subqueries)
- Recursive CTE for transaction hierarchy
- And 3 more...

---

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/verify-otp` - Verify OTP

### Accounts
- `GET /api/accounts` - List accounts
- `GET /api/accounts/{id}` - Get account details
- `POST /api/accounts` - Create account
- `GET /api/accounts/{id}/balance` - Get balance

### Transactions
- `GET /api/transactions` - List transactions
- `POST /api/transactions/transfer` - Fund transfer
- `GET /api/transactions/{id}` - Get transaction

### Payments
- `POST /api/payments/upi` - UPI payment
- `POST /api/payments/neft` - NEFT transfer
- `POST /api/payments/rtgs` - RTGS transfer
- `POST /api/payments/imps` - IMPS transfer

### Beneficiaries
- `GET /api/beneficiaries` - List beneficiaries
- `POST /api/beneficiaries` - Add beneficiary
- `DELETE /api/beneficiaries/{id}` - Remove beneficiary

### Cards
- `GET /api/cards` - List cards
- `POST /api/cards` - Request new card
- `PUT /api/cards/{id}/block` - Block card

### Loans
- `GET /api/loans` - List loans
- `POST /api/loans/apply` - Apply for loan
- `POST /api/loans/{id}/repay` - Make repayment

---

## Features

### Payment Methods
- **UPI** - Unified Payments Interface with FSM state machine
- **NEFT** - National Electronic Funds Transfer (batch processing)
- **RTGS** - Real Time Gross Settlement (real-time)
- **IMPS** - Immediate Payment Service (24/7)

### Security
- JWT Authentication
- AES Encryption for sensitive data
- RBAC (Role-Based Access Control)
- Rate Limiting
- Audit Logging
- Fraud Detection (7 methods)

### Saga Pattern
Distributed transactions with compensating transactions for payment processing across microservices.

---

## Useful Commands

```bat
# Start everything
start-all.bat

# Stop everything
stop-all.bat

# Docker only
docker-up.bat
docker-down.bat

# View logs
docker-compose logs -f

# Database migration
cd backend && mvn flyway:migrate

# Frontend build
cd frontend && ng build --configuration production
```

---

## Project Structure

```
DigitalBanking/
├── backend/                    # Spring Boot Monolith
│   ├── src/main/java/
│   │   ├── config/            # Security, Redis, CORS, Swagger
│   │   ├── controller/        # REST Controllers
│   │   ├── dto/               # Request/Response DTOs
│   │   ├── entity/            # JPA Entities
│   │   ├── event/             # Event-Driven Architecture
│   │   ├── exception/         # Custom Exceptions
│   │   ├── mapper/            # Entity-DTO Mappers
│   │   ├── repository/        # JPA Repositories
│   │   ├── service/           # Business Logic
│   │   └── utility/           # Utility Classes
│   └── src/main/resources/
│       └── db/
│           ├── migration/     # Flyway Migrations
│           ├── procedures/    # Stored Procedures
│           ├── triggers/      # Database Triggers
│           ├── indexes/       # Advanced Indexing
│           └── complex-queries/ # Complex SQL Queries
├── frontend/                   # Angular 17
│   ├── src/app/
│   │   ├── auth/              # Login, Register, Forgot Password
│   │   ├── core/              # Services, Interceptors, Guards
│   │   ├── dashboard/         # Main Dashboard
│   │   ├── accounts/          # Account Management
│   │   ├── transactions/      # Transaction History
│   │   ├── payments/          # UPI, NEFT, RTGS, IMPS
│   │   ├── beneficiaries/     # Beneficiary Management
│   │   ├── cards/             # Card Management
│   │   ├── loans/             # Loan Management
│   │   ├── notifications/     # Notification Center
│   │   └── store/             # NgRx State Management
│   └── src/assets/
│       └── scss/              # Global Styles
├── microservices/              # Microservices Architecture
│   ├── auth-service/          # Authentication Service
│   ├── account-service/       # Account Service
│   ├── transaction-service/   # Transaction Service
│   ├── payment-service/       # Payment Service
│   ├── notification-service/  # Notification Service
│   ├── gateway/               # API Gateway
│   └── common/                # Shared Library
├── docker-compose.yml          # Docker Infrastructure
├── monitoring/                 # Prometheus & Grafana
├── .github/workflows/          # CI/CD Pipeline
├── setup.bat                   # Full Setup
├── start-infra.bat             # Start Infrastructure
├── start-backend.bat           # Start Backend
├── start-frontend.bat          # Start Frontend
├── start-all.bat               # Start Everything
├── stop-all.bat                # Stop Everything
├── docker-up.bat               # Docker Compose Up
└── docker-down.bat             # Docker Compose Down
```

---

## License

MIT
