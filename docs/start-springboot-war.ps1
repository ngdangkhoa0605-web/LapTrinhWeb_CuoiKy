param([switch]$Restart)
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$tools = Join-Path $env:USERPROFILE 'Downloads\bookstore-local-tools'
$data = Join-Path $tools 'mariadb-data'
$server = 'C:\Program Files\MariaDB 12.2\bin\mariadbd.exe'
$java = 'C:\Program Files\Java\jdk-21\bin\java.exe'
$war = Join-Path $root 'target\bookstore-chain-management.war'
$config = Join-Path $env:APPDATA 'BookstoreChainLocal'

function Test-Port([int]$port) {
    $client = New-Object Net.Sockets.TcpClient
    try { $client.Connect('127.0.0.1', $port); return $true }
    catch { return $false }
    finally { $client.Dispose() }
}
if (-not (Test-Path $war)) { throw 'WAR not found. Build first: mvn clean package.' }
if (-not (Test-Path $server) -or -not (Test-Path (Join-Path $data 'mysql'))) {
    throw 'Local MariaDB was not installed at the expected path.'
}
if (-not (Test-Path $java)) { throw 'Java 21 not found at the expected path.' }

if ($Restart -and (Test-Port 8080)) {
    $owned = Get-CimInstance Win32_Process -Filter "name = 'java.exe'" | Where-Object {
        $_.CommandLine -like '*-jar*bookstore-chain-management.war*'
    }
    if (-not $owned) { throw 'Port 8080 is in use by another program; refusing to stop it.' }
    foreach ($process in $owned) { Stop-Process -Id $process.ProcessId -ErrorAction Stop }
    Start-Sleep -Seconds 3
}
if (-not (Test-Port 3306)) {
    Start-Process -FilePath $server -ArgumentList @('--no-defaults', "--datadir=`"$data`"", '--port=3306', '--bind-address=127.0.0.1') -WindowStyle Hidden -RedirectStandardError (Join-Path $tools 'mariadb.log') | Out-Null
    for ($i = 0; $i -lt 30 -and -not (Test-Port 3306); $i++) { Start-Sleep -Seconds 1 }
    if (-not (Test-Port 3306)) { throw 'MariaDB did not start. Check bookstore-local-tools/mariadb.log.' }
}
if (-not (Test-Port 8080)) {
    $nameFile = Join-Path $config 'cloud-name.txt'
    $keyFile = Join-Path $config 'api-key.dpapi'
    $secretFile = Join-Path $config 'api-secret.dpapi'
    if ((Test-Path $nameFile) -and (Test-Path $keyFile) -and (Test-Path $secretFile)) {
        $env:CLOUDINARY_CLOUD_NAME = (Get-Content -LiteralPath $nameFile -Raw).Trim()
        $key = Get-Content -LiteralPath $keyFile -Raw | ConvertTo-SecureString
        $secret = Get-Content -LiteralPath $secretFile -Raw | ConvertTo-SecureString
        $env:CLOUDINARY_API_KEY = [System.Net.NetworkCredential]::new('', $key).Password
        $env:CLOUDINARY_API_SECRET = [System.Net.NetworkCredential]::new('', $secret).Password
    } else {
        Write-Warning 'Cloudinary is not configured; run docs/configure-cloudinary-local.ps1 first if uploads are needed.'
    }
    $env:DEMO_SEED_BOOKS = 'true'
    try {
        # Environment variables are inherited by the Java process; credentials are never command-line arguments.
        $process = Start-Process -FilePath $java -ArgumentList @('-jar', "`"$war`"") -WorkingDirectory $root -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $tools 'war.log') -RedirectStandardError (Join-Path $tools 'war-error.log')
    } finally {
        Remove-Item Env:\CLOUDINARY_API_KEY, Env:\CLOUDINARY_API_SECRET, Env:\CLOUDINARY_CLOUD_NAME -ErrorAction SilentlyContinue
    }
    Write-Output "Started standalone Spring Boot WAR (PID $($process.Id))."
} else {
    Write-Output 'Port 8080 is already in use; no restart performed. Use -Restart to restart this application.'
}
for ($i = 0; $i -lt 60; $i++) {
    try {
        $response = Invoke-WebRequest -Uri 'http://localhost:8080/home' -UseBasicParsing -TimeoutSec 4
        if ($response.StatusCode -eq 200) { Write-Output 'READY: http://localhost:8080/home'; exit 0 }
    } catch { Start-Sleep -Seconds 2 }
}
throw 'Spring Boot did not become ready. Check bookstore-local-tools/war.log and war-error.log.'