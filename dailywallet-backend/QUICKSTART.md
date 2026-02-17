# DailyWallet Backend - Quick Start Guide

## ✅ Production Configuration Complete

Your backend is now configured with:

### Database
- **Provider**: Neon PostgreSQL (Serverless)
- **Location**: AWS US-East-1
- **Status**: Connected and ready

### M-Pesa Credentials
- **Environment**: Production (Live transactions)
- **Shortcode**: 4157883
- **Status**: Ready for live transactions

## 🚀 Quick Start (2 minutes)

### Option 1: Docker (Recommended)

```bash
cd dailywallet-backend

# Start backend (connects to Neon database automatically)
docker-compose up -d

# Check logs
docker-compose logs -f backend
```

**Or use the startup script:**

Windows:
```cmd
start.bat
```

Linux/Mac:
```bash
chmod +x start.sh
./start.sh
```

**Backend will be available at**: `http://localhost:8080`

### Option 2: Local Development (Without Docker)

```bash
cd dailywallet-backend

# Build and run
mvn clean install -DskipTests
mvn spring-boot:run
```

The backend will automatically connect to your Neon PostgreSQL database.

## 🔐 Important Security Notes

### 1. Update JWT Secret (CRITICAL)

Generate a secure JWT secret:

```bash
# On Linux/Mac
openssl rand -base64 64

# On Windows (PowerShell)
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))
```

Update `.env`:
```env
JWT_SECRET=<your-generated-secret>
```

### 2. Update Callback URL

When deploying to production, update the callback URL in `.env`:

```env
MPESA_CALLBACK_URL=https://your-domain.com/api/mpesa/callback
```

**Important**: This URL must be:
- Publicly accessible (not localhost)
- HTTPS enabled
- Registered with Safaricom

## 📱 Testing the API

### 1. Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### 2. Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "254712345678",
    "fullName": "John Doe",
    "password": "securepass123"
  }'
```

### 3. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "254712345678",
    "password": "securepass123"
  }'
```

Save the token from response.

### 4. Get Wallets

```bash
curl http://localhost:8080/api/wallets \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. Set Allocation Rule

```bash
curl -X POST http://localhost:8080/api/wallets/allocation \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "dailyPercentage": 15,
    "weeklyPercentage": 25,
    "monthlyPercentage": 40,
    "savingsPercentage": 20
  }'
```

### 6. Test M-Pesa Deposit (LIVE)

```bash
curl -X POST http://localhost:8080/api/transactions/deposit \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100,
    "phoneNumber": "254712345678"
  }'
```

**⚠️ This will trigger a REAL M-Pesa STK push to the phone number!**

## 🌐 Deploying to Production

### Before Deployment Checklist

- [ ] Generate and set secure JWT_SECRET
- [ ] Update MPESA_CALLBACK_URL to your production domain
- [ ] Register callback URLs with Safaricom
- [ ] Set up SSL/TLS certificate
- [ ] Configure production database
- [ ] Test with small amounts first

### Deploy to Railway

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login and initialize
railway login
railway init

# Add PostgreSQL
railway add

# Set environment variables from .env
railway variables set JWT_SECRET="your-secure-secret"
railway variables set MPESA_CONSUMER_KEY="28dKDuoxsw0TWzJ1mZbWYFE0q8NDFzYCiPkh45ms0v3SQAus"
railway variables set MPESA_CONSUMER_SECRET="oQ8OhrEwGfylRWqwVuw2pPg5Hhzho8jq4IEAAvwsHT3aHvp6uBZSGx4njDruSkId"
railway variables set MPESA_ENVIRONMENT="production"
railway variables set MPESA_SHORTCODE="4157883"
railway variables set MPESA_PASSKEY="b1325bc0114bf8a7cc43efbc257ff727cbcb7da6b8a472f02d53dfaa715d08db"
railway variables set MPESA_CALLBACK_URL="https://your-app.railway.app/api/mpesa/callback"

# Deploy
railway up

# Get your URL
railway domain
```

### Register Callback URLs with Safaricom

Once deployed, register these URLs in your M-Pesa dashboard:

1. **STK Push Callback**: `https://your-domain.com/api/mpesa/callback/stk`
2. **B2C Result URL**: `https://your-domain.com/api/mpesa/callback/b2c/result`
3. **B2C Timeout URL**: `https://your-domain.com/api/mpesa/callback/b2c/timeout`

## 🔍 Monitoring

### View Logs

```bash
# Docker
docker-compose logs -f backend

# Railway
railway logs

# Check specific service
docker logs dailywallet-backend
```

### Database Access (Neon Console)

Access your database via:
1. **Neon Console**: https://console.neon.tech
2. **psql**: 
   ```bash
   psql "postgresql://neondb_owner:npg_WjU8mry4zCSx@ep-polished-silence-aidig5th-pooler.c-4.us-east-1.aws.neon.tech/neondb?sslmode=require"
   ```

Useful queries:
```sql
-- View users
SELECT id, phone_number, full_name, created_at FROM users;

-- View wallets
SELECT u.phone_number, w.wallet_type, w.balance 
FROM wallets w 
JOIN users u ON w.user_id = u.id;

-- View transactions
SELECT u.phone_number, t.transaction_type, t.amount, t.status, t.created_at 
FROM transactions t 
JOIN users u ON t.user_id = u.id 
ORDER BY t.created_at DESC 
LIMIT 10;
```

## 🐛 Troubleshooting

### M-Pesa Authentication Fails

Check logs for authentication errors:
```bash
docker-compose logs backend | grep "M-Pesa"
```

Verify credentials in `.env` are correct.

### Database Connection Issues

Check Neon database status:
1. Visit https://console.neon.tech
2. Verify database is active
3. Check connection string in `.env` is correct
4. Ensure SSL mode is enabled

If connection fails, verify:
```bash
# Test connection
psql "postgresql://neondb_owner:npg_WjU8mry4zCSx@ep-polished-silence-aidig5th-pooler.c-4.us-east-1.aws.neon.tech/neondb?sslmode=require"
```

### STK Push Not Received

1. Verify phone number format: `254XXXXXXXXX`
2. Check callback URL is publicly accessible
3. Ensure shortcode is active
4. Review M-Pesa dashboard for errors

## 📊 API Endpoints Summary

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/auth/register` | POST | No | Register new user |
| `/api/auth/login` | POST | No | Login user |
| `/api/auth/verify-otp` | POST | No | Verify phone OTP |
| `/api/wallets` | GET | Yes | Get all wallets |
| `/api/wallets/allocation` | POST | Yes | Set allocation rule |
| `/api/wallets/reallocate` | POST | Yes | Move funds between wallets |
| `/api/transactions/deposit` | POST | Yes | Initiate M-Pesa deposit |
| `/api/transactions/withdraw` | POST | Yes | Withdraw to M-Pesa |
| `/api/transactions` | GET | Yes | Transaction history |
| `/actuator/health` | GET | No | Health check |

## 🎯 Next Steps

1. **Test locally** with small amounts
2. **Deploy to cloud** (Railway/Render)
3. **Update frontend** API URL
4. **Register callbacks** with Safaricom
5. **Test end-to-end** flow
6. **Monitor transactions** in production

## 📞 Support

For M-Pesa issues, contact Safaricom Developer Support:
- Email: apisupport@safaricom.co.ke
- Portal: https://developer.safaricom.co.ke

---

**🎉 Your backend is ready for live transactions!**

Start with small test amounts before going live with real users.
