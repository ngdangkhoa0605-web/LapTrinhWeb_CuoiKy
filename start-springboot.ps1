param([switch]$Restart)
$ErrorActionPreference = 'Stop'
$war = Join-Path $PSScriptRoot 'target\bookstore-chain-management.war'
$state = Join-Path $env:LOCALAPPDATA 'BookstoreChain'
$config = Import-Clixml -LiteralPath (Join-Path $state 'cloudinary.xml')
$java = (Get-Command java.exe -ErrorAction Stop).Source
if (!(Test-Path -LiteralPath $war)) { throw "WAR not found: $war" }
$tools = Join-Path $env:USERPROFILE 'Downloads\bookstore-local-tools'
$mysql = Join-Path $tools 'mysql-8.4.8-winx64\bin\mysqld.exe'
if (!(Get-NetTCPConnection -LocalPort 3307 -State Listen -ErrorAction SilentlyContinue)) {
    if (!(Test-Path -LiteralPath $mysql)) { throw 'Install MySQL 8.4.8 in bookstore-local-tools first.' }
    Start-Process $mysql -ArgumentList @('--no-defaults', ('--basedir="' + "$tools\mysql-8.4.8-winx64" + '"'), ('--datadir="' + "$tools\mysql-data" + '"'), '--port=3307', '--bind-address=127.0.0.1', '--mysqlx=OFF', '--console') -RedirectStandardOutput "$tools\mysql.log" -RedirectStandardError "$tools\mysql-error.log" -WindowStyle Hidden
    Start-Sleep -Seconds 5
}
$listeners = @(Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue)
foreach ($owner in ($listeners.OwningProcess | Select-Object -Unique)) {
    $process = Get-CimInstance Win32_Process -Filter "ProcessId=$owner"
    if ($process.Name -ne 'java.exe' -or !$process.CommandLine.Contains($war)) {
        throw 'Port 8080 belongs to another application; nothing was stopped.'
    }
    if (!$Restart) { throw 'Bookstore is already running. Use -Restart to restart it.' }
    Stop-Process -Id $owner -ErrorAction Stop
    Wait-Process -Id $owner -Timeout 30 -ErrorAction SilentlyContinue
}
$names = @('CLOUDINARY_CLOUD_NAME','CLOUDINARY_API_KEY','CLOUDINARY_API_SECRET','SPRING_DATASOURCE_URL','SPRING_DATASOURCE_USERNAME','SPRING_DATASOURCE_PASSWORD','SPRING_PROFILES_ACTIVE')
$previous = @{}
foreach ($name in $names) { $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
try {
    $env:SPRING_PROFILES_ACTIVE = 'mysql'
    $env:SPRING_DATASOURCE_URL = 'jdbc:mysql://127.0.0.1:3307/bookstore?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8'
    $database = Import-Clixml -LiteralPath (Join-Path $state 'mysql.xml')
    $env:SPRING_DATASOURCE_USERNAME = $database.Username
    $env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', $database.Password).Password
    $env:CLOUDINARY_CLOUD_NAME = $config.CloudName
    $env:CLOUDINARY_API_KEY = [System.Net.NetworkCredential]::new('', $config.ApiKey).Password
    $env:CLOUDINARY_API_SECRET = [System.Net.NetworkCredential]::new('', $config.ApiSecret).Password
    $stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
    $stdout = Join-Path $state "springboot-$stamp.log"
    $stderr = Join-Path $state "springboot-$stamp.err.log"
    $app = Start-Process -FilePath $java -ArgumentList @('-jar', ('"' + $war + '"')) -WorkingDirectory $PSScriptRoot -RedirectStandardOutput $stdout -RedirectStandardError $stderr -WindowStyle Hidden -PassThru
    Write-Host "Spring Boot PID: $($app.Id)"
    Write-Host "Log: $stdout"
    for ($i = 0; $i -lt 60; $i++) {
        Start-Sleep -Seconds 2
        $app.Refresh()
        if ($app.HasExited) { throw "Spring Boot exited. Check $stdout and $stderr" }
        try {
            $response = Invoke-WebRequest 'http://localhost:8080/' -UseBasicParsing -TimeoutSec 3
            if ($response.StatusCode -eq 200) {
                Write-Host 'READY: http://localhost:8080/ (HTTP 200)'
                return
            }
        } catch { }
    }
    throw "Startup timed out. Check $stdout"
} finally {
    foreach ($name in $names) { [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process') }
}