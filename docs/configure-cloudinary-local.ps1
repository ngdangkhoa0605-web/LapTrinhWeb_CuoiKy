# Run once in a regular PowerShell window on this Windows account.
# SecureString is encrypted with Windows DPAPI for the current user; never copy this file to Git/Drive.
$ErrorActionPreference = 'Stop'
$dir = Join-Path $env:APPDATA 'BookstoreChainLocal'
New-Item -ItemType Directory -Path $dir -Force | Out-Null
$cloud = Read-Host 'Cloudinary cloud name (from your Cloudinary dashboard)'
if ($cloud -notmatch '^[A-Za-z0-9_-]+$') { throw 'Invalid cloud name.' }
$key = Read-Host 'Cloudinary API key' -AsSecureString
$secret = Read-Host 'Cloudinary API secret' -AsSecureString
if ($key.Length -eq 0 -or $secret.Length -eq 0) { throw 'API key and secret cannot be empty.' }
Set-Content -LiteralPath (Join-Path $dir 'cloud-name.txt') -Value $cloud -Encoding ASCII
$key | ConvertFrom-SecureString | Set-Content -LiteralPath (Join-Path $dir 'api-key.dpapi')
$secret | ConvertFrom-SecureString | Set-Content -LiteralPath (Join-Path $dir 'api-secret.dpapi')
Write-Output 'Cloudinary credentials encrypted for this Windows account; no credentials were written into the project.'