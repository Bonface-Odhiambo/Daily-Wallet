# Start DailyWallet Backend - Quick Guide

## Step 1: Ensure Docker Desktop is Running

1. Look for the **Docker whale icon** in your system tray (bottom-right corner)
2. If you don't see it, open **Docker Desktop** from Start Menu
3. Wait until the whale icon is **steady** (not animated)
4. Click the whale icon - it should say "Docker Desktop is running"

**This may take 2-3 minutes on first startup.**

## Step 2: Open PowerShell in Backend Directory

1. Open **File Explorer**
2. Navigate to: `C:\Users\Administrator\Desktop\daily flow\dailywallet-backend`
3. Click in the address bar and type: `powershell`
4. Press Enter

## Step 3: Start the Backend

In PowerShell, run:

```powershell
docker compose up -d --build
```

**First time will take 5-10 minutes** as it:
- Downloads Java 21 Alpine image
- Builds the Spring Boot application
- Connects to Neon database

## Step 4: Check Backend Status

```powershell
# View logs
docker compose logs -f backend

# Check if container is running
docker ps
```

## Step 5: Test Backend

Open your browser and go to:

**http://localhost:8080/actuator/health**

You should see:
```json
{"status":"UP"}
```

## Step 6: Start Frontend

Open a **new PowerShell window**:

```powershell
cd "C:\Users\Administrator\Desktop\daily flow"
npm run dev
```

Frontend will be at: **http://localhost:5173**

## Troubleshooting

### "Docker daemon is not running"
- Open Docker Desktop application
- Wait for it to fully start
- Look for steady whale icon in system tray

### "Cannot connect to Docker daemon"
- Restart Docker Desktop
- Wait 2-3 minutes
- Try again

### "Port 8080 already in use"
```powershell
# Stop any existing containers
docker compose down

# Or find and kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Backend won't start
```powershell
# Check logs for errors
docker compose logs backend

# Rebuild from scratch
docker compose down
docker compose up -d --build
```

### Database connection errors
- Check `.env` file has correct Neon credentials
- Verify internet connection
- Check Neon console: https://console.neon.tech

## Quick Commands Reference

```powershell
# Start backend
docker compose up -d

# Stop backend
docker compose down

# View logs
docker compose logs -f backend

# Restart backend
docker compose restart

# Rebuild and start
docker compose up -d --build

# Check running containers
docker ps

# Check all containers
docker ps -a
```

## What's Happening When Backend Starts

1. **Building image** - Compiling Java code with Maven
2. **Starting container** - Running the Spring Boot app
3. **Connecting to database** - Establishing connection to Neon PostgreSQL
4. **Creating tables** - Hibernate auto-creates database schema
5. **Ready** - Backend listening on port 8080

Look for this in logs:
```
Started DailyWalletApplication in X.XXX seconds
```

## Next Steps After Backend is Running

1. Test health endpoint: http://localhost:8080/actuator/health
2. Start frontend: `npm run dev`
3. Open app: http://localhost:5173
4. Register a new user
5. Test M-Pesa deposit with real phone number

---

**Need help?** Check the logs with `docker compose logs backend`
