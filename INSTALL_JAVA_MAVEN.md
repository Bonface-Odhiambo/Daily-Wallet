# Install Java 21 and Maven using Chocolatey

## Step 1: Install Chocolatey (if not already installed)

Open **PowerShell as Administrator** and run:

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

Close and reopen PowerShell as Administrator after installation.

## Step 2: Install Java 21

```powershell
choco install openjdk21 -y
```

**Verify installation:**
```powershell
java -version
```

You should see something like: `openjdk version "21.0.x"`

## Step 3: Install Maven

```powershell
choco install maven -y
```

**Verify installation:**
```powershell
mvn -version
```

You should see Maven version and Java 21 information.

## Step 4: Install Docker Desktop

```powershell
choco install docker-desktop -y
```

**After installation:**
1. Restart your computer
2. Open Docker Desktop
3. Wait for it to fully start (whale icon in system tray)

**Verify Docker:**
```powershell
docker --version
docker compose version
```

## All-in-One Installation Command

If you want to install everything at once:

```powershell
choco install openjdk21 maven docker-desktop -y
```

Then restart your computer.

## Next Steps

After installation is complete:

1. **Navigate to backend:**
   ```powershell
   cd "C:\Users\Administrator\Desktop\daily flow\dailywallet-backend"
   ```

2. **Start the backend:**
   ```powershell
   docker compose up -d
   ```

3. **Check if it's running:**
   ```powershell
   docker compose logs -f backend
   ```

4. **Test the API:**
   Open browser: http://localhost:8080/actuator/health

You should see: `{"status":"UP"}`

## Troubleshooting

### "choco not recognized"
- Make sure you ran the Chocolatey installation as Administrator
- Close and reopen PowerShell
- Try: `refreshenv` or restart PowerShell

### "java not recognized" after installation
- Close and reopen PowerShell/Terminal
- Run: `refreshenv`
- Verify PATH: `$env:JAVA_HOME`

### Docker Desktop won't start
- Ensure WSL 2 is installed: `wsl --install`
- Restart computer
- Check Windows features: Hyper-V and WSL must be enabled

### Port 8080 already in use
```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace <PID> with actual number)
taskkill /PID <PID> /F
```
