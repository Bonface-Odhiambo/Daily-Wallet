# Installation Steps - Run as Administrator

## Step 1: Open PowerShell as Administrator

1. Press **Windows Key**
2. Type: **PowerShell**
3. **Right-click** on "Windows PowerShell"
4. Select **"Run as Administrator"**
5. Click **Yes** when prompted

## Step 2: Install Java 21

Try the correct package name:

```powershell
choco install temurin21 -y
```

**Alternative if that doesn't work:**
```powershell
choco install openjdk -y
```

**Verify:**
```powershell
java -version
```

## Step 3: Install Maven

```powershell
choco install maven -y
```

**Verify:**
```powershell
mvn -version
```

## Step 4: Install Docker Desktop

```powershell
choco install docker-desktop -y
```

**After installation:**
- Restart your computer
- Open Docker Desktop application
- Wait for it to start completely

**Verify:**
```powershell
docker --version
docker compose version
```

## Alternative: Manual Installation (If Chocolatey Issues Persist)

### Java 21:
1. Download from: https://adoptium.net/temurin/releases/?version=21
2. Run installer
3. Check "Set JAVA_HOME" during installation

### Maven:
1. Download from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Search "Environment Variables" in Windows
   - Edit "Path" under System variables
   - Add: `C:\Program Files\Apache\maven\bin`

### Docker Desktop:
1. Download from: https://www.docker.com/products/docker-desktop/
2. Run installer
3. Enable WSL 2 when prompted
4. Restart computer

## Step 5: After Installation

Close and reopen PowerShell (as Administrator), then verify:

```powershell
java -version
mvn -version
docker --version
```

## Step 6: Start Backend

```powershell
cd "C:\Users\Administrator\Desktop\daily flow\dailywallet-backend"
docker compose up -d
```

## Troubleshooting Chocolatey Permission Issues

If you continue to get permission errors:

1. **Delete lock files:**
   ```powershell
   Remove-Item "C:\ProgramData\chocolatey\lib\195dd1adac72bd9b98ef8ba45defcd98c8ca7173" -Force -ErrorAction SilentlyContinue
   Remove-Item "C:\ProgramData\chocolatey\lib-bad" -Recurse -Force -ErrorAction SilentlyContinue
   ```

2. **Try installing one at a time:**
   ```powershell
   choco install temurin21 -y
   choco install maven -y
   choco install docker-desktop -y
   ```

3. **Or use manual installation** (links above)
