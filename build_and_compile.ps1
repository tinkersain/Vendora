# Master PDF Build Script for Vendora Project Documentation
$ErrorActionPreference = "Stop"

Write-Host "=========================================================="
Write-Host "VENDORA MASTER DOCUMENTATION PDF GENERATOR"
Write-Host "=========================================================="

# 1. Execute HTML Generation Parts
Write-Host "Step 1: Generating HTML Structure..."
& .\build_html.ps1
& .\part1_overview_arch.ps1
& .\part2_status_db.ps1
& .\part3_api_pending.ps1
& .\part4_features_test_viva.ps1

$htmlFile = "C:\Users\tankar\Downloads\Vendora\vendora_documentation.html"
$pdfFile = "C:\Users\tankar\Downloads\Vendora\Vendora_Master_Project_Documentation.pdf"
$edge = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
$tempProfile = "C:\Users\tankar\Downloads\Vendora\.edge_pdf_profile"

if (Test-Path $htmlFile) {
    $size = (Get-Item $htmlFile).Length
    Write-Host "HTML Generation Successful. Size: $size bytes."
} else {
    Write-Error "HTML Generation Failed!"
}

# 2. Compile to PDF using Microsoft Edge Headless with --no-pdf-header-footer
Write-Host "Step 2: Compiling to PDF via Microsoft Edge Headless..."
Remove-Item $pdfFile -ErrorAction SilentlyContinue
Remove-Item $tempProfile -Recurse -Force -ErrorAction SilentlyContinue

$args = @(
    "--headless=new",
    "--disable-gpu",
    "--no-pdf-header-footer",
    "--allow-file-access-from-files",
    "--virtual-time-budget=9000",
    "--run-all-compositor-stages-before-draw",
    "--user-data-dir=`"$tempProfile`"",
    "--print-to-pdf=`"$pdfFile`"",
    "`"file:///$htmlFile`""
)

$process = Start-Process -FilePath $edge -ArgumentList $args -PassThru -NoNewWindow
$process.WaitForExit(25000)

Remove-Item $tempProfile -Recurse -Force -ErrorAction SilentlyContinue

# 3. Verification
if (Test-Path $pdfFile) {
    $pdfItem = Get-Item $pdfFile
    Write-Host "SUCCESS! Generated PDF: $pdfFile"
    Write-Host "PDF Size: $($pdfItem.Length) bytes ($([Math]::Round($pdfItem.Length/1MB, 2)) MB)"
} else {
    Write-Error "PDF generation failed. Output file does not exist."
}
