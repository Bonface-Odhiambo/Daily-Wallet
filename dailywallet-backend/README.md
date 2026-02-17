# DailyWallet Backend API

Production-ready Spring Boot backend for DailyWallet - Smart cash flow management for daily earners.

## Features

- **Multi-Wallet System**: Daily, Weekly, Monthly, and Savings wallets
- **M-Pesa Integration**: Deposits via STK Push, withdrawals via B2C
- **Automated Interest**: 13% annual interest on savings, calculated daily
- **Scheduled Releases**: Time-locked wallet releases
- **JWT Authentication**: Secure phone-based authentication
- **OTP Verification**: SMS-based phone verification
- **Transaction History**: Complete audit trail
- **Fund Reallocation**: Move money between wallets

## Tech Stack

- Java 17
- Spring Boot 3.2.2
- PostgreSQL 16
- Spring Security + JWT
- Spring Data JPA
- Docker & Docker Compose
- M-Pesa Daraja API

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 16 (or use Docker)
- M-Pesa Developer Account

## Quick Start

### 1. Clone and Configure

```bash
cd dailywallet-backend
cp .env.example .env
# Edit .env with your credentials
```

### 2. Run with Docker (Recommended)

```bash
docker-compose up -d
```

The API will be available at `http://localhost:8080`

### 3. Run Locally

```bash
# Start PostgreSQL
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=dailywallet \
  -e POSTGRES_USER=dailywallet \
  -e POSTGRES_PASSWORD=changeme \
  postgres:16-alpine

# Build and run
mvn clean install
mvn spring-boot:run
```

## API Documentation

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "phoneNumber": "254712345678",
  "fullName": "John Doe",
  "password": "securepass123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "phoneNumber": "254712345678",
  "password": "securepass123"
}
```

#### Verify OTP
```http
POST /api/auth/verify-otp
Content-Type: application/json

{
  "phoneNumber": "254712345678",
  "otpCode": "123456"
}
```

### Wallet Endpoints

#### Get All Wallets
```http
GET /api/wallets
Authorization: Bearer {token}
```

#### Set Allocation Rule
```http
POST /api/wallets/allocation
Authorization: Bearer {token}
Content-Type: application/json

{
  "dailyPercentage": 15,
  "weeklyPercentage": 25,
  "monthlyPercentage": 40,
  "savingsPercentage": 20
}
```

#### Reallocate Funds
```http
POST /api/wallets/reallocate
Authorization: Bearer {token}
Content-Type: application/json

{
  "fromWalletType": "SAVINGS",
  "toWalletType": "DAILY",
  "amount": 500
}
```

### Transaction Endpoints

#### Deposit Money
```http
POST /api/transactions/deposit
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 1000,
  "phoneNumber": "254712345678"
}
```

#### Withdraw Money
```http
POST /api/transactions/withdraw
Authorization: Bearer {token}
Content-Type: application/json

{
  "walletType": "DAILY",
  "amount": 500,
  "recipientPhoneNumber": "254712345678"
}
```

#### Get Transaction History
```http
GET /api/transactions?page=0&size=20
Authorization: Bearer {token}
```

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DB_PASSWORD` | PostgreSQL password | Yes |
| `JWT_SECRET` | JWT signing key (min 256 bits) | Yes |
| `MPESA_CONSUMER_KEY` | M-Pesa API consumer key | Yes |
| `MPESA_CONSUMER_SECRET` | M-Pesa API consumer secret | Yes |
| `MPESA_SHORTCODE` | M-Pesa business shortcode | Yes |
| `MPESA_PASSKEY` | M-Pesa Lipa Na M-Pesa passkey | Yes |

## M-Pesa Setup

1. Register at [Safaricom Developer Portal](https://developer.safaricom.co.ke/)
2. Create an app and get credentials
3. Configure STK Push and B2C APIs
4. Update `.env` with your credentials
5. For production, use production credentials and update `application-prod.yml`

## Scheduled Jobs

- **Daily Interest**: Runs at midnight (00:00 EAT)
- **Daily Wallet Release**: Runs at 6:00 AM EAT
- **Weekly Wallet Release**: Runs every Sunday at midnight
- **Monthly Wallet Release**: Runs on 1st of each month at midnight

## Database Schema

Key entities:
- `users` - User accounts
- `wallets` - User wallets (4 per user)
- `transactions` - All financial transactions
- `allocation_rules` - Income allocation preferences
- `interest_records` - Daily interest calculations
- `otp_verifications` - Phone verification OTPs

## Deployment

### Deploy to Railway

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login and deploy
railway login
railway init
railway up
```

### Deploy to Render

1. Connect your GitHub repository
2. Create a new Web Service
3. Set environment variables
4. Deploy

### Deploy to AWS

```bash
# Build Docker image
docker build -t dailywallet-backend .

# Push to ECR and deploy to ECS/EKS
```

## Security

- Passwords hashed with BCrypt
- JWT tokens for authentication
- CORS configured for frontend
- Input validation on all endpoints
- SQL injection prevention via JPA
- Rate limiting recommended for production

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Production Checklist

- [ ] Change all default passwords
- [ ] Generate secure JWT secret (256+ bits)
- [ ] Configure M-Pesa production credentials
- [ ] Set up SSL/TLS certificates
- [ ] Configure production database
- [ ] Set up monitoring and logging
- [ ] Configure backup strategy
- [ ] Set up rate limiting
- [ ] Review and update CORS settings
- [ ] Enable production profile

## Support

For issues and questions, contact the development team.

## License

Proprietary - DailyWallet © 2026
