# Jimudu Wallet Backend API

Production-ready Spring Boot backend for Jimudu Wallet - Discipline-enforced digital financial platform for building financial health in Kenya.

## Features

- **Discipline Buckets**: Spending Wallet, Emergency Savings, MMF Growth Bucket, Overdraft Facility
- **Daily Allowance Engine**: Controlled release of funds to prevent overspending
- **Behavior-Linked Overdraft**: Responsible credit with visible pricing and discipline scoring
- **MMF Sweep Integration**: Automatic investment of surplus balances into money market funds
- **Transparency Engine**: Complete fee breakdown before every transaction
- **Financial Health Scoring**: Discipline score, emergency readiness, and wealth building metrics
- **M-Pesa Integration**: Deposits via STK Push, withdrawals via B2C
- **Automated Interest**: MMF yield calculations and emergency savings growth
- **JWT Authentication**: Secure phone-based authentication
- **OTP Verification**: SMS-based phone verification
- **Transaction History**: Complete audit trail with behavioral insights
- **Financial Nudges**: AI-powered recommendations for better financial habits

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

#### Set Daily Allowance
```http
POST /api/discipline/daily-allowance
Authorization: Bearer {token}
Content-Type: application/json

{
  "dailyAmount": 600,
  "emergencySavingsTarget": 3000,
  "mmfSweepEnabled": true
}
```

#### Get Financial Health Score
```http
GET /api/discipline/health-score
Authorization: Bearer {token}
```

#### Reallocate Funds
```http
POST /api/discipline/reallocate
Authorization: Bearer {token}
Content-Type: application/json

{
  "fromBucket": "EMERGENCY_SAVINGS",
  "toBucket": "SPENDING_WALLET",
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
  "bucketType": "SPENDING_WALLET",
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

- **Daily Allowance Release**: Runs at 6:00 AM EAT - releases daily spending allowance
- **MMF Nightly Sweep**: Runs at midnight (00:00 EAT) - invests surplus into MMF
- **Discipline Score Calculation**: Runs at 1:00 AM EAT - updates financial health metrics
- **Emergency Fund Progress**: Runs daily at 2:00 AM EAT - tracks savings goals
- **Overdraft Interest Calculation**: Runs daily at 3:00 AM EAT - calculates behavior-linked rates
- **Financial Nudges Generation**: Runs at 8:00 AM EAT - creates personalized recommendations

## Database Schema

Key entities:
- `users` - User accounts with discipline profiles
- `discipline_buckets` - Jimudu Wallet buckets (SPENDING_WALLET, EMERGENCY_SAVINGS, MMF_GROWTH, OVERDRAFT_FACILITY)
- `transactions` - All financial transactions with behavioral tags
- `daily_allowance_rules` - Daily spending limits and release schedules
- `discipline_scores` - Financial health metrics and streaks
- `mmf_investments` - Money market fund sweep records
- `overdraft_facilities` - Behavior-linked credit facilities
- `financial_nudges` - AI-powered recommendations
- `fee_transparency_logs` - Complete cost breakdown records
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
docker build -t jimudu-wallet-backend .

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

Proprietary - Jimudu Wallet © 2026
