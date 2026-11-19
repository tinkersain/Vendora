<#
.SYNOPSIS
    Vendora SonarQube & JaCoCo Code Quality and Test Coverage Automation Script.

.DESCRIPTION
    Iterates through all Vendora microservices, executes JUnit 5 test suites,
    generates JaCoCo XML/HTML code coverage reports, and uploads quality metrics
    to SonarQube (configured by default on port 9001 to avoid Zscaler port 9000 conflicts).

.PARAMETER SonarHostUrl
    The SonarQube server URL. Defaults to $env:SONAR_HOST_URL or http://localhost:9002.

.PARAMETER SonarToken
    SonarQube user analysis token. Defaults to $env:SONAR_TOKEN.

.PARAMETER Services
    List of services to analyze. Defaults to all 8 Vendora services.

.PARAMETER ReportOnly
    Generates local JaCoCo test reports only, skipping SonarQube upload.

.EXAMPLE
    .\run-sonar.ps1
    .\run-sonar.ps1 -SonarToken "sqp_xxx..."
    .\run-sonar.ps1 -ReportOnly
    .\run-sonar.ps1 -Services @("vendora-user-service", "vendora-helpdesk-service")
#>

[CmdletBinding()]
param(
    [string]$SonarHostUrl = $(if ($env:SONAR_HOST_URL) { $env:SONAR_HOST_URL } else { "http://localhost:9002" }),
    [string]$SonarToken   = $(if ($env:SONAR_TOKEN) { $env:SONAR_TOKEN } else { "squ_853fc9bea091fb6b98ac36d7f5b2122e9a417819" }),
    [string[]]$Services   = @(
        "vendora-user-service",
        "vendora-auth-service",
        "vendora-marketplace-service",
        "vendora-helpdesk-service",
        "vendora-scrap-service",
        "vendora-payment-service",
        "vendora-api-gateway",
        "vendora-eureka-server"
    ),
    [switch]$ReportOnly
)

$ErrorActionPreference = "Continue"
$RootDir = $PSScriptRoot

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "         VENDORA MICROSERVICES - SONARQUBE & JACOCO QUALITY RUNNER              " -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host " Target SonarQube URL : $SonarHostUrl" -ForegroundColor Gray
Write-Host " Target Services      : $($Services -join ', ')" -ForegroundColor Gray
Write-Host " Report-Only Mode     : $($ReportOnly.IsPresent)" -ForegroundColor Gray
Write-Host "--------------------------------------------------------------------------------" -ForegroundColor Gray

# 1. Check SonarQube Server Availability if not in ReportOnly mode
$sonarAvailable = $false
if (-not $ReportOnly) {
    Write-Host "[INFO] Checking connection to SonarQube server at $SonarHostUrl..." -ForegroundColor Yellow
    try {
        $uri = [System.Uri]$SonarHostUrl
        $testConn = Test-NetConnection -ComputerName $uri.Host -Port $uri.Port -WarningAction SilentlyContinue
        if ($testConn.TcpTestSucceeded) {
            $sonarAvailable = $true
            Write-Host "[SUCCESS] SonarQube server is reachable on port $($uri.Port)!" -ForegroundColor Green
        } else {
            Write-Host "[WARNING] SonarQube server at $SonarHostUrl is NOT reachable." -ForegroundColor DarkYellow
            Write-Host "          Note: Port 9000 is occupied by Zscaler. SonarQube is configured on port 9002." -ForegroundColor DarkYellow
            Write-Host "          To start SonarQube locally on port 9002:" -ForegroundColor DarkYellow
            Write-Host "            Run: cd C:\Users\tankar\Downloads\Vendora\sonarqube-26.11.0.134175\sonarqube-26.11.0.134175\bin\windows-x86-64; .\StartSonar.bat" -ForegroundColor DarkYellow
            Write-Host "          Continuing in Report-Only mode (JaCoCo reports will still be generated)." -ForegroundColor Yellow
        }
    } catch {
        Write-Host "[WARNING] Could not verify SonarQube host: $_. Continuing in Report-Only mode." -ForegroundColor DarkYellow
    }
}

$results = @()
$overallStartTime = Get-Date

foreach ($service in $Services) {
    $serviceFolder = Join-Path $RootDir "$service\$service"
    if (-not (Test-Path $serviceFolder)) {
        # Check flat structure if nested doesn't exist
        $serviceFolder = Join-Path $RootDir $service
    }

    Write-Host "`n>>> Processing: $service <<<" -ForegroundColor Cyan
    if (-not (Test-Path (Join-Path $serviceFolder "pom.xml"))) {
        Write-Host "[SKIP] pom.xml not found in $serviceFolder" -ForegroundColor Red
        $results += [PSCustomObject]@{
            Service = $service
            Status  = "SKIPPED (No POM)"
            Duration = "0s"
            JaCoCoReport = "N/A"
        }
        continue
    }

    $mvnCmd = Join-Path $serviceFolder "mvnw.cmd"
    if (-not (Test-Path $mvnCmd)) {
        $mvnCmd = "mvn"
    }

    $serviceStart = Get-Date
    Push-Location $serviceFolder

    try {
        if ($sonarAvailable -and (-not $ReportOnly)) {
            Write-Host "[STEP 1/2] Running tests, generating JaCoCo coverage, and uploading to SonarQube..." -ForegroundColor Yellow
            $mvnArgs = @("clean", "test", "jacoco:report", "sonar:sonar", "-Dsonar.host.url=$SonarHostUrl")
            if ($SonarToken) {
                $mvnArgs += "-Dsonar.token=$SonarToken"
            }
        } else {
            Write-Host "[STEP 1/1] Running tests and generating JaCoCo coverage report..." -ForegroundColor Yellow
            $mvnArgs = @("clean", "test", "jacoco:report")
        }

        & $mvnCmd $mvnArgs | Out-Host
        $exitCode = $LASTEXITCODE

        $duration = [math]::Round(((Get-Date) - $serviceStart).TotalSeconds, 1)
        $jacocoHtml = Join-Path $serviceFolder "target\site\jacoco\index.html"
        $hasReport = Test-Path $jacocoHtml

        if ($exitCode -eq 0) {
            Write-Host "[SUCCESS] $service completed successfully in ${duration}s" -ForegroundColor Green
            $status = if ($sonarAvailable -and (-not $ReportOnly)) { "ANALYZED & UPLOADED" } else { "JACOCO GENERATED" }
        } else {
            Write-Host "[FAILURE] $service Maven exited with error code $exitCode" -ForegroundColor Red
            $status = "FAILED"
        }

        $results += [PSCustomObject]@{
            Service      = $service
            Status       = $status
            Duration     = "${duration}s"
            JaCoCoReport = if ($hasReport) { $jacocoHtml } else { "No report" }
        }
    } finally {
        Pop-Location
    }
}

$totalDuration = [math]::Round(((Get-Date) - $overallStartTime).TotalSeconds, 1)

Write-Host "`n================================================================================" -ForegroundColor Cyan
Write-Host "                         ANALYSIS & COVERAGE SUMMARY                            " -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
$results | Format-Table -AutoSize -Property Service, Status, Duration, JaCoCoReport

Write-Host "Total Execution Time: ${totalDuration}s" -ForegroundColor Gray
Write-Host "`nJaCoCo Local Reports:" -ForegroundColor Cyan
foreach ($r in $results) {
    if ($r.JaCoCoReport -ne "N/A" -and $r.JaCoCoReport -ne "No report") {
        Write-Host "  - $($r.Service): file:///$($r.JaCoCoReport.Replace('\', '/'))" -ForegroundColor White
    }
}

if ($sonarAvailable) {
    Write-Host "`nSonarQube Dashboard: $SonarHostUrl" -ForegroundColor Green
} else {
    Write-Host "`nNote: Once SonarQube is started on port 9002, rerun: .\run-sonar.ps1" -ForegroundColor Yellow
}
