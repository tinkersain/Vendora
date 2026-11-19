Get-ChildItem -Recurse -Filter "*Test.java" | ForEach-Object {
    $filePath = $_.FullName
    $baseName = $_.BaseName
    $lines = Get-Content $filePath
    $testMethods = @()
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '@Test') {
            # look ahead for method definition
            for ($j = $i + 1; $j -lt [Math]::Min($lines.Count, $i + 5); $j++) {
                if ($lines[$j] -match 'void\s+(\w+)\(') {
                    $testMethods += $matches[1]
                    break
                }
            }
        }
    }
    if ($testMethods.Count -gt 0) {
        $parentDir = Split-Path (Split-Path (Split-Path (Split-Path $filePath))) -Leaf
        Write-Host "[$parentDir] $baseName ($($testMethods.Count) tests)" -ForegroundColor Cyan
        foreach ($t in $testMethods) {
            Write-Host "   * $t" -ForegroundColor Gray
        }
    }
}
