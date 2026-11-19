<#
.SYNOPSIS
    Stop all running Vendora microservices by terminating processes bound to their ports.
#>

[CmdletBinding()]
param()

$ports = @(8761, 8080, 8081, 8082, 8083, 8084, 8085, 8086)

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "                 STOPPING VENDORA MICROSERVICES                                 " -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan

foreach ($port in $ports) {
    try {
        $conns = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
        if ($conns) {
            foreach ($conn in $conns) {
                $pidToKill = $conn.OwningProcess
                if ($pidToKill -gt 0) {
                    Write-Host "[KILL] Terminating process PID $pidToKill listening on port $port..." -ForegroundColor Yellow
                    Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
                }
            }
        } else {
            Write-Host "[OK] Port $port is already free." -ForegroundColor Green
        }
    } catch {
        Write-Host "[WARN] Could not inspect port ${port}: $_" -ForegroundColor DarkYellow
    }
}

Start-Sleep -Seconds 2
Write-Host "`nAll target microservice ports are cleared!" -ForegroundColor Green
