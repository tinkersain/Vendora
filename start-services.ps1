<#
.SYNOPSIS
    Starts all Vendora microservices in background processes with clean logging and port validation.
#>

[CmdletBinding()]
param()

$RootDir = $PSScriptRoot
$LogDir = Join-Path $RootDir ".service-logs"
if (-not (Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
}

$services = @(
    @{ Name = "vendora-eureka-server";       Port = 8761; Folder = "vendora-eureka-server\vendora-eureka-server" },
    @{ Name = "vendora-user-service";         Port = 8081; Folder = "vendora-user-service\vendora-user-service" },
    @{ Name = "vendora-auth-service";         Port = 8082; Folder = "vendora-auth-service\vendora-auth-service" },
    @{ Name = "vendora-marketplace-service";  Port = 8083; Folder = "vendora-marketplace-service\vendora-marketplace-service" },
    @{ Name = "vendora-helpdesk-service";     Port = 8084; Folder = "vendora-helpdesk-service\vendora-helpdesk-service" },
    @{ Name = "vendora-scrap-service";        Port = 8085; Folder = "vendora-scrap-service\vendora-scrap-service" },
    @{ Name = "vendora-payment-service";      Port = 8086; Folder = "vendora-payment-service\vendora-payment-service" },
    @{ Name = "vendora-api-gateway";          Port = 8080; Folder = "vendora-api-gateway\vendora-api-gateway" }
)

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "                 STARTING VENDORA MICROSERVICES                                 " -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan

$childProcs = @()

foreach ($svc in $services) {
    $serviceName = $svc.Name
    $port = $svc.Port
    $folder = Join-Path $RootDir $svc.Folder
    $outFile = Join-Path $LogDir "$serviceName.log"
    $errFile = Join-Path $LogDir "$serviceName.err.log"

    Write-Host "[STARTING] Launching $serviceName on port $port..." -ForegroundColor Yellow

    $mvnCmd = Join-Path $folder "mvnw.cmd"
    $proc = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/c `"$mvnCmd spring-boot:run`"" `
        -WorkingDirectory $folder `
        -RedirectStandardOutput $outFile `
        -RedirectStandardError $errFile `
        -WindowStyle Hidden `
        -PassThru

    $childProcs += $proc
    Write-Host "  -> Process PID: $($proc.Id), Logging to: $outFile" -ForegroundColor Gray

    if ($serviceName -eq "vendora-eureka-server") {
        Write-Host "  -> Waiting for Eureka Server to bind port 8761..." -ForegroundColor Yellow
        $ready = $false
        for ($i = 0; $i -lt 30; $i++) {
            Start-Sleep -Seconds 1
            $test = Get-NetTCPConnection -LocalPort 8761 -State Listen -ErrorAction SilentlyContinue
            if ($test) {
                $ready = $true
                break
            }
        }
        if ($ready) {
            Write-Host "  -> Eureka Server is UP on port 8761!" -ForegroundColor Green
        } else {
            Write-Host "  -> Eureka Server still booting, proceeding with remaining services..." -ForegroundColor DarkYellow
        }
    } else {
        Start-Sleep -Seconds 2
    }
}

Write-Host "`nAll 8 Vendora microservices have been dispatched!" -ForegroundColor Green
Write-Host "Holding runner process active to maintain microservices..." -ForegroundColor Gray

while ($true) {
    Start-Sleep -Seconds 30
}
