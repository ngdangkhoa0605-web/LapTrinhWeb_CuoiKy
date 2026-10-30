$ErrorActionPreference = 'Stop'
$env:DEMO_SEED_BOOKS = 'true'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$tools = Join-Path $env:USERPROFILE 'Downloads\bookstore-local-tools'
$data = Join-Path $tools 'mariadb-data'
$maven = Join-Path $tools 'apache-maven-3.9.9\bin\mvn.cmd'
$server = 'C:\Program Files\MariaDB 12.2\bin\mariadbd.exe'
if (-not (Test-Path $maven) -or -not (Test-Path $server) -or -not (Test-Path (Join-Path $data 'mysql'))) {
    throw 'Local Maven or MariaDB data directory is missing. See docs/SETUP.md.'
}
function PortOpen([int]$port) {
    $socket = New-Object System.Net.Sockets.TcpClient
    try { $socket.Connect('127.0.0.1', $port); return $true }
    catch { return $false }
    finally { $socket.Dispose() }
}
if (-not (PortOpen 3306)) {
    Start-Process -FilePath $server -ArgumentList @('--no-defaults',"--datadir=`"$data`"",'--port=3306','--bind-address=127.0.0.1') -WindowStyle Hidden -RedirectStandardError (Join-Path $tools 'mariadb.log') | Out-Null
    for ($i=0; $i -lt 30 -and -not (PortOpen 3306); $i++) { Start-Sleep -Seconds 1 }
    if (-not (PortOpen 3306)) { throw 'MariaDB failed to start. Check bookstore-local-tools/mariadb.log.' }
}
if (-not (PortOpen 8080)) {
    Start-Process -FilePath $maven -ArgumentList @('-B','spring-boot:run') -WorkingDirectory $root -WindowStyle Hidden -RedirectStandardOutput (Join-Path $tools 'spring.log') -RedirectStandardError (Join-Path $tools 'spring-error.log') | Out-Null
}
for ($i=0; $i -lt 90; $i++) {
    try {
        $response = Invoke-WebRequest -Uri 'http://localhost:8080/home' -UseBasicParsing -TimeoutSec 3
        if ($response.StatusCode -eq 200) { Write-Output 'READY: http://localhost:8080/home'; exit 0 }
    } catch { Start-Sleep -Seconds 2 }
}
throw 'Application did not become ready. Check bookstore-local-tools/spring.log and spring-error.log.'