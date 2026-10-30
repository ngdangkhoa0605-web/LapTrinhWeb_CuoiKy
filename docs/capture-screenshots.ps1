param(
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$Chrome = 'C:\Program Files\Google\Chrome\Application\chrome.exe'
)

$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $Chrome)) {
    throw "Chrome not found: $Chrome"
}
$out = Join-Path $PSScriptRoot 'screenshots'
New-Item -ItemType Directory -Force -Path $out | Out-Null
$pages = @(
    @{ Name = '01-trang-chu'; Url = '/home' },
    @{ Name = '02-danh-sach-sach'; Url = '/books' },
    @{ Name = '03-dang-nhap'; Url = '/login' },
    @{ Name = '04-dang-ky'; Url = '/register' },
    @{ Name = '05-chi-tiet-sach'; Url = '/books/1' }
    @{ Name = '06-loc-danh-muc'; Url = '/books?categoryId=1' }
)
foreach ($page in $pages) {
    $url = $BaseUrl.TrimEnd('/') + $page.Url
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 15 -MaximumRedirection 5
        if ($response.StatusCode -ne 200 -or $response.Content -notmatch '<html') {
            throw 'Not a valid HTML page.'
        }
    } catch {
        throw "Application not available at $url. $($_.Exception.Message)"
    }
    $file = Join-Path $out ($page.Name + '.png')
    $profile = Join-Path $env:TEMP ('bookstore-capture-' + [guid]::NewGuid().ToString('N'))
    try {
        $process = Start-Process -FilePath $Chrome -ArgumentList @('--headless','--disable-gpu','--hide-scrollbars','--no-first-run','--window-size=1440,1000','--virtual-time-budget=3500',"--user-data-dir=`"$profile`"","--screenshot=`"$file`"",$url) -Wait -PassThru -WindowStyle Hidden
        if ($process.ExitCode -ne 0 -or -not (Test-Path -LiteralPath $file)) {
            throw "Chrome failed to capture $url"
        }
        Write-Output $file
    } finally {
        if (Test-Path -LiteralPath $profile) { Remove-Item -LiteralPath $profile -Recurse -Force -ErrorAction SilentlyContinue }
    }
}