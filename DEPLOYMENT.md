# Jimudu Wallet Deployment Guide

This guide covers how to deploy the Jimudu Wallet application to production with proper CORS configuration and environment setup.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- PostgreSQL database (Neon, AWS RDS, or self-hosted)
- Domain name for your application
- SSL certificate (recommended for production)

### 1. Environment Configuration

Copy the environment template and configure your production values:

```bash
cp .env.example .env
```

Edit `.env` with your production values:

```bash
# Database Configuration (Required)
DATABASE_URL=postgresql://username:password@host:5432/database_name
DB_USER=your_db_user
DB_PASSWORD=your_db_password

# CORS Configuration (Required)
ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# JWT Configuration (Required)
JWT_SECRET=your_super_secret_jwt_key_at_least_32_characters_long
JWT_EXPIRATION=86400000

# M-Pesa Configuration (Required for payments)
MPESA_CONSUMER_KEY=your_mpesa_consumer_key
MPESA_CONSUMER_SECRET=your_mpesa_consumer_secret
MPESA_PASSKEY=your_mpesa_passkey
MPESA_SHORTCODE=your_mpesa_shortcode
```

### 2. Deploy with Docker Compose

Run the deployment script:

```bash
chmod +x deploy.sh
./deploy.sh
```

Or manually:

```bash
docker-compose build
docker-compose up -d
```

### 3. Verify Deployment

Check application health:
```bash
curl http://localhost:8080/api/health
```

Access your application:
- Frontend: `http://localhost:8080`
- Backend API: `http://localhost:8080/api`
- Health Check: `http://localhost:8080/api/health`

## 🔧 CORS Configuration

The application includes flexible CORS configuration that supports both development and production environments.

### Development Origins
By default, the following origins are allowed for development:
- `http://localhost:5173`
- `http://localhost:3000`
- `http://127.0.0.1:63607`
- `http://localhost:63607`

### Production Origins
Set your production frontend URLs using the `ALLOWED_ORIGINS` environment variable:

```bash
ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com,https://app.yourdomain.com
```

### CORS Features
- **Methods**: GET, POST, PUT, DELETE, OPTIONS, PATCH
- **Headers**: All headers allowed
- **Credentials**: Supported for authentication
- **Exposed Headers**: Authorization, Content-Type

## 🌐 Deployment Options

### Option 1: Docker Compose (Recommended)
- Single command deployment
- Includes both frontend and backend
- Automatic health checks
- Volume persistence for logs

### Option 2: Cloud Platforms

#### Vercel + Railway/Heroku
1. **Frontend (Vercel)**:
   ```bash
   npm run build
   # Deploy dist folder to Vercel
   ```

2. **Backend (Railway/Heroku)**:
   ```bash
   # Set environment variables in platform
   # Deploy using Dockerfile or Maven build
   ```

#### AWS
1. **Frontend**: S3 + CloudFront
2. **Backend**: ECS + RDS
3. **Database**: Amazon RDS PostgreSQL

#### DigitalOcean
1. **Frontend**: App Platform
2. **Backend**: App Platform or Droplet
3. **Database**: Managed PostgreSQL

## 📊 Monitoring and Health Checks

### Health Endpoints
- `/api/health` - Basic health check
- `/actuator/health` - Detailed health information
- `/actuator/metrics` - Application metrics

### Logging
Logs are written to `logs/jimudu-backend.log` and include:
- Request/response logging
- Error tracking
- Performance metrics

### Monitoring Commands
```bash
# View logs
docker-compose logs -f jimudu-app

# Check container status
docker-compose ps

# Restart application
docker-compose restart jimudu-app
```

## 🔒 Security Considerations

### Environment Variables
- Never commit `.env` files to version control
- Use strong, unique secrets
- Rotate JWT secrets regularly

### Database Security
- Use SSL connections
- Implement proper user permissions
- Regular backups

### Network Security
- Configure firewall rules
- Use HTTPS in production
- Implement rate limiting

## 🚨 Troubleshooting

### Common Issues

#### CORS Errors
1. Verify `ALLOWED_ORIGINS` includes your frontend domain
2. Check for typos in domain names
3. Ensure HTTPS is used in production

#### Database Connection Issues
1. Verify database URL format
2. Check network connectivity
3. Validate credentials

#### Build Failures
1. Clear Docker cache: `docker system prune -a`
2. Check Maven dependencies
3. Verify Java version compatibility

### Debug Commands
```bash
# Check container logs
docker-compose logs jimudu-app

# Enter container for debugging
docker-compose exec jimudu-app bash

# Test API endpoints
curl -v http://localhost:8080/api/health

# Check environment variables
docker-compose exec jimudu-app env | grep -E "(DATABASE|JWT|ALLOWED)"
```

## 📝 Environment Variables Reference

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `DATABASE_URL` | ✅ | PostgreSQL connection string | `postgresql://user:pass@host:5432/db` |
| `DB_USER` | ✅ | Database username | `jimudu_user` |
| `DB_PASSWORD` | ✅ | Database password | `secure_password` |
| `ALLOWED_ORIGINS` | ✅ | CORS allowed origins | `https://app.jimudu.com` |
| `JWT_SECRET` | ✅ | JWT signing secret | `32_character_minimum_secret` |
| `JWT_EXPIRATION` | ❌ | Token expiration time (ms) | `86400000` |
| `MPESA_CONSUMER_KEY` | ✅ | M-Pesa API consumer key | `your_key` |
| `MPESA_CONSUMER_SECRET` | ✅ | M-Pesa API consumer secret | `your_secret` |
| `MPESA_PASSKEY` | ✅ | M-Pesa passkey | `your_passkey` |
| `MPESA_SHORTCODE` | ✅ | M-Pesa shortcode | `174379` |
| `SERVER_PORT` | ❌ | Server port | `8080` |

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
name: Deploy
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Deploy to production
        run: |
          echo ${{ secrets.DOT_ENV }} > .env
          docker-compose build
          docker-compose up -d
```

## 📞 Support

For deployment issues:
1. Check the troubleshooting section
2. Review application logs
3. Verify environment configuration
4. Test individual components separately

---

**Note**: Always test deployments in a staging environment before deploying to production.
