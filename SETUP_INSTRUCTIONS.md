# DailyWallet - Complete Setup Instructions

## Prerequisites Installation

### 1. Install Docker Desktop (Required)

**Download and Install:**
1. Go to: https://www.docker.com/products/docker-desktop/
2. Download Docker Desktop for Windows
3. Run the installer
4. **Important**: Enable WSL 2 during installation (recommended)
5. Restart your computer after installation
6. Open Docker Desktop and wait for it to start

**Verify Installation:**
```powershell
docker --version
docker compose version
```

You should see version numbers if installed correctly.

### 2. Install Maven (Optional - for local development)

**Option A: Using Chocolatey (Recommended)**
```powershell
# Install Chocolatey if not already installed
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven -y
```

**Option B: Manual Installation**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Open System Properties → Environment Variables
   - Add `C:\Program Files\Apache\maven\bin` to PATH
4. Restart PowerShell

**Verify Installation:**
```powershell
mvn --version
```

### 3. Install Java 17 (Required for Maven)

**Using Chocolatey:**
```powershell
choco install openjdk17 -y
```

**Or download from:**
https://adoptium.net/temurin/releases/?version=17

**Verify:**
```powershell
java -version
```

## Running the Backend

### After Docker is Installed:

```powershell
# Navigate to backend directory
cd "C:\Users\Administrator\Desktop\daily flow\dailywallet-backend"

# Start the backend
docker compose up -d

# View logs
docker compose logs -f backend

# Stop backend
docker compose down
```

The backend will be available at: **http://localhost:8080**

### Health Check:
Open browser and go to: http://localhost:8080/actuator/health

You should see: `{"status":"UP"}`

## Running the Frontend

```powershell
# Navigate to frontend directory
cd "C:\Users\Administrator\Desktop\daily flow"

# Install dependencies (if not already done)
npm install

# Start development server
npm run dev
```

The frontend will be available at: **http://localhost:5173**

## Quick Start After Installation

1. **Start Backend:**
   ```powershell
   cd "C:\Users\Administrator\Desktop\daily flow\dailywallet-backend"
   docker compose up -d
   ```

2. **Start Frontend:**
   ```powershell
   cd "C:\Users\Administrator\Desktop\daily flow"
   npm run dev
   ```

3. **Open in browser:** http://localhost:5173

## Troubleshooting

### Docker Issues

**"Docker daemon not running"**
- Open Docker Desktop application
- Wait for it to fully start (whale icon in system tray should be steady)

**"WSL 2 installation incomplete"**
- Run: `wsl --install`
- Restart computer
- Open Docker Desktop again

**Port 8080 already in use**
```powershell
# Find what's using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Maven Issues

**"mvn not recognized"**
- Restart PowerShell/Terminal after installation
- Verify PATH includes Maven bin directory
- Run: `$env:Path` to check

### Backend Connection Issues

**Database connection failed**
- Check `.env` file has correct Neon credentials
- Verify internet connection
- Check Neon console: https://console.neon.tech

**M-Pesa errors**
- Verify credentials in `.env`
- Check logs: `docker compose logs backend`

## Next Steps

Once both backend and frontend are running:

1. Register a new user
2. Verify phone with OTP
3. Set allocation preferences
4. Test M-Pesa deposit (with real phone number)
5. Check wallet balances
6. Test fund reallocation

## Support

If you encounter issues:
1. Check Docker Desktop is running
2. View backend logs: `docker compose logs backend`
3. Check browser console for frontend errors
4. Verify `.env` file configuration
