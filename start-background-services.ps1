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

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Starting Missing Vendora Services via WMI Detached Process" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

foreach ($svc in $services) {
    $name = $svc.Name
    $port = $svc.Port
    $folder = Join-Path $RootDir $svc.Folder
    $outFile = Join-Path $LogDir "$name.log"
    $errFile = Join-Path $LogDir "$name.err.log"

    $conn = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($null -ne $conn) {
        Write-Host "[ALREADY RUNNING] $name on port $port (PID: $($conn.OwningProcess | Select-Object -First 1))" -ForegroundColor Green
        continue
    }

    Write-Host "[DISPATCHING] $name on port $port..." -ForegroundColor Yellow
    $mvnCmd = Join-Path $folder "mvnw.cmd"
    # Execute detached via WMI Win32_Process
    $cmdLine = "cmd.exe /c `"`"$mvnCmd`" spring-boot:run > `"$outFile`" 2> `"$errFile`"`""
    
    $procRes = Invoke-CimMethod -ClassName Win32_Process -MethodName Create -Arguments @{
        CommandLine = $cmdLine
        CurrentDirectory = $folder
    }

    if ($procRes.ReturnValue -eq 0) {
        Write-Host "  -> Spawned ProcessId: $($procRes.ProcessId)" -ForegroundColor Gray
    } else {
        Write-Host "  -> Failed to spawn $name. Return code: $($procRes.ReturnValue)" -ForegroundColor Red
    }
}

Write-Host "`nWaiting for services to bind ports (up to 45 seconds)..." -ForegroundColor Cyan
for ($s = 0; $s -lt 40; $s++) {
    Start-Sleep -Seconds 1
    $listeningCount = 0
    foreach ($svc in $services) {
        $test = Get-NetTCPConnection -LocalPort $svc.Port -State Listen -ErrorAction SilentlyContinue
        if ($null -ne $test) {
            $listeningCount++
        }
    }
    if ($listeningCount -eq $services.Count) {
        Write-Host "All $($services.Count) services successfully bound their ports in $s seconds!" -ForegroundColor Green
        break
    }
}

Write-Host "`n--- FINAL PORT CHECK ---" -ForegroundColor Cyan
$allPorts = @(8761, 8080, 8081, 8082, 8083, 8084, 8085, 8086, 3306)
foreach ($p in $allPorts) {
    $c = Get-NetTCPConnection -LocalPort $p -State Listen -ErrorAction SilentlyContinue
    if ($null -ne $c) {
        Write-Host "Port $p : UP (PID: $($c.OwningProcess | Select-Object -First 1))" -ForegroundColor Green
    } else {
        Write-Host "Port $p : DOWN" -ForegroundColor Red
    }
}
