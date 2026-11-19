[CmdletBinding()]
param()

$RootDir = "c:\Users\tankar\Downloads\Vendora"
$LogDir = Join-Path $RootDir ".service-logs"
if (-not (Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
}

$services = @(
    @{ Name = "vendora-user-service";         Port = 8081; Folder = "vendora-user-service\vendora-user-service" },
    @{ Name = "vendora-marketplace-service";  Port = 8083; Folder = "vendora-marketplace-service\vendora-marketplace-service" },
    @{ Name = "vendora-helpdesk-service";     Port = 8084; Folder = "vendora-helpdesk-service\vendora-helpdesk-service" },
    @{ Name = "vendora-scrap-service";        Port = 8085; Folder = "vendora-scrap-service\vendora-scrap-service" },
    @{ Name = "vendora-payment-service";      Port = 8086; Folder = "vendora-payment-service\vendora-payment-service" },
    @{ Name = "vendora-api-gateway";          Port = 8080; Folder = "vendora-api-gateway\vendora-api-gateway" }
)

Write-Host "Checking services and starting missing ones..." -ForegroundColor Cyan

foreach ($svc in $services) {
    $serviceName = $svc.Name
    $port = $svc.Port
    $folder = Join-Path $RootDir $svc.Folder
    $outFile = Join-Path $LogDir "$serviceName.log"
    $errFile = Join-Path $LogDir "$serviceName.err.log"

    $conn = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($null -ne $conn) {
        Write-Host "[ALREADY RUNNING] $serviceName is already listening on port $port (PID: $($conn.OwningProcess | Select-Object -First 1))" -ForegroundColor Green
        continue
    }

    Write-Host "[STARTING] Launching $serviceName on port $port..." -ForegroundColor Yellow

    $mvnCmd = Join-Path $folder "mvnw.cmd"
    $proc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c `"$mvnCmd spring-boot:run`"" `
        -WorkingDirectory $folder `
        -RedirectStandardOutput $outFile `
        -RedirectStandardError $errFile `
        -WindowStyle Hidden `
        -PassThru

    Write-Host "  -> Dispatched PID: $($proc.Id), Logging to: $outFile" -ForegroundColor Gray
    Start-Sleep -Seconds 2
}

Write-Host "`nWaiting for newly launched services to bind their ports..." -ForegroundColor Cyan
foreach ($svc in $services) {
    $serviceName = $svc.Name
    $port = $svc.Port

    $bound = $false
    for ($i = 0; $i -lt 45; $i++) {
        $conn = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
        if ($null -ne $conn) {
            $bound = $true
            break
        }
        Start-Sleep -Seconds 1
    }

    if ($bound) {
        Write-Host "  -> [UP] $serviceName is listening on port $port!" -ForegroundColor Green
    } else {
        Write-Host "  -> [WAITING/FAILED] $serviceName has not bound port $port yet. Check log: .service-logs\$serviceName.log" -ForegroundColor DarkYellow
    }
}
