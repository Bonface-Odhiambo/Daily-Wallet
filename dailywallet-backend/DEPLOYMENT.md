# DailyWallet Backend - Deployment Guide

## Pre-Deployment Checklist

### 1. Environment Setup
- [ ] PostgreSQL database provisioned
- [ ] M-Pesa production credentials obtained
- [ ] Domain name configured (if applicable)
- [ ] SSL certificate ready
- [ ] Environment variables prepared

### 2. Security Configuration
- [ ] Generate strong JWT secret: `openssl rand -base64 64`
- [ ] Set secure database password
- [ ] Configure CORS for production frontend URL
- [ ] Review security settings in `SecurityConfig.java`

### 3. Database Migration
- [ ] Review `application.yml` - set `ddl-auto: validate` for production
- [ ] Run database migrations if needed
- [ ] Create database backup strategy

## Deployment Options

### Option 1: Railway (Recommended for MVP)

**Pros**: Easy setup, automatic deployments, built-in PostgreSQL
**Cost**: ~$5-20/month

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Initialize project
railway init

# Add PostgreSQL
railway add

# Set environment variables
railway variables set JWT_SECRET="your-secret-here"
railway variables set MPESA_CONSUMER_KEY="your-key"
railway variables set MPESA_CONSUMER_SECRET="your-secret"
railway variables set MPESA_SHORTCODE="your-shortcode"
railway variables set MPESA_PASSKEY="your-passkey"

# Deploy
railway up

# Get URL
railway domain
```

### Option 2: Render

**Pros**: Free tier available, easy setup
**Cost**: Free - $25/month

1. Push code to GitHub
2. Go to [render.com](https://render.com)
3. Create new Web Service
4. Connect GitHub repository
5. Configure:
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/dailywallet-backend-1.0.0.jar`
6. Add PostgreSQL database
7. Set environment variables
8. Deploy

### Option 3: AWS (Production Scale)

**Pros**: Scalable, enterprise-grade
**Cost**: ~$50-200/month

#### Using AWS Elastic Beanstalk

```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init -p docker dailywallet-backend

# Create environment
eb create dailywallet-prod

# Set environment variables
eb setenv JWT_SECRET="your-secret" \
  MPESA_CONSUMER_KEY="your-key" \
  MPESA_CONSUMER_SECRET="your-secret" \
  MPESA_SHORTCODE="your-shortcode" \
  MPESA_PASSKEY="your-passkey"

# Deploy
eb deploy
```

#### Using ECS (Container Service)

1. Build and push Docker image to ECR
2. Create ECS cluster
3. Create task definition
4. Create RDS PostgreSQL instance
5. Configure load balancer
6. Deploy service

### Option 4: DigitalOcean App Platform

**Pros**: Simple, affordable
**Cost**: ~$12-25/month

1. Push to GitHub
2. Create new App in DigitalOcean
3. Connect repository
4. Add PostgreSQL database
5. Set environment variables
6. Deploy

## Post-Deployment Steps

### 1. Verify Deployment

```bash
# Health check
curl https://your-api-url.com/actuator/health

# Test authentication
curl -X POST https://your-api-url.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "254712345678",
    "fullName": "Test User",
    "password": "testpass123"
  }'
```

### 2. Configure Frontend

Update your React frontend's API base URL:

```typescript
// src/config/api.ts
export const API_BASE_URL = 'https://your-api-url.com';
```

### 3. Set Up Monitoring

#### Option A: Spring Boot Actuator + Prometheus
- Already configured in the app
- Expose `/actuator/metrics` endpoint
- Set up Prometheus and Grafana

#### Option B: Application Performance Monitoring
- New Relic
- Datadog
- AWS CloudWatch

### 4. Configure Logging

```yaml
# application-prod.yml
logging:
  level:
    com.dailywallet: INFO
  file:
    name: /var/log/dailywallet/application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### 5. Database Backups

#### Automated Backups (Railway/Render)
- Enabled by default
- Configure retention period

#### Manual Backup Script
```bash
#!/bin/bash
pg_dump -h your-db-host -U dailywallet dailywallet > backup-$(date +%Y%m%d).sql
```

### 6. SSL/TLS Configuration

Most platforms (Railway, Render) provide automatic SSL. For custom domains:

```bash
# Using Let's Encrypt
certbot certonly --standalone -d api.dailywallet.com
```

## M-Pesa Production Configuration

### 1. Go Live Checklist
- [ ] Complete M-Pesa production onboarding
- [ ] Get production credentials
- [ ] Configure production shortcode
- [ ] Set up production callback URLs
- [ ] Test in production sandbox first

### 2. Update Configuration

```yaml
# application-prod.yml
app:
  mpesa:
    api-url: https://api.safaricom.co.ke
    callback-url: https://your-api-url.com/api/mpesa/callback
```

### 3. Callback URL Registration
Register these URLs with Safaricom:
- STK Push: `https://your-api-url.com/api/mpesa/callback/stk`
- B2C Result: `https://your-api-url.com/api/mpesa/callback/b2c/result`
- B2C Timeout: `https://your-api-url.com/api/mpesa/callback/b2c/timeout`

## Scaling Considerations

### Horizontal Scaling
- Use load balancer (AWS ALB, Nginx)
- Enable session management (Redis)
- Configure database connection pooling

### Database Optimization
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

### Caching
Add Redis for caching:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check DATABASE_URL environment variable
   - Verify database is running
   - Check firewall rules

2. **M-Pesa Integration Issues**
   - Verify credentials are correct
   - Check callback URLs are publicly accessible
   - Review M-Pesa API logs

3. **JWT Token Issues**
   - Ensure JWT_SECRET is set and consistent
   - Check token expiration settings

### Logs Access

```bash
# Railway
railway logs

# Render
# View in dashboard

# AWS
aws logs tail /aws/elasticbeanstalk/dailywallet-prod
```

## Cost Estimates

| Platform | Database | Total/Month |
|----------|----------|-------------|
| Railway | Included | $5-20 |
| Render | $7 | $12-32 |
| DigitalOcean | $15 | $27-40 |
| AWS | $15-30 | $50-200 |

## Support & Maintenance

### Regular Tasks
- Monitor error logs daily
- Review transaction failures
- Check database performance
- Update dependencies monthly
- Backup verification weekly

### Emergency Contacts
- Database issues: [DBA contact]
- M-Pesa issues: Safaricom support
- Infrastructure: [DevOps contact]

## Rollback Procedure

```bash
# Railway
railway rollback

# Render
# Use dashboard to rollback

# AWS
eb deploy --version previous-version
```

---

**Ready for Funding Presentation**

Your backend is production-ready with:
✅ Secure authentication
✅ M-Pesa integration
✅ Automated interest calculations
✅ Complete API documentation
✅ Docker deployment
✅ Monitoring and health checks
✅ Scalable architecture
