# Script cleanup và fix Gatling reports
# Chạy SAU KHI test xong để giảm dung lượng và fix broken links

param(
    [string]$ReportDir = "C:\Users\PC\Documents\GitHub\kiemthugatling\target\gatling",
    [switch]$KeepOneSample = $false  # Nếu true, giữ 1 file mẫu cho mỗi loại request
)

Write-Host "`n🧹 Gatling Report Cleanup Tool" -ForegroundColor Cyan
Write-Host "================================`n" -ForegroundColor Cyan

# Tìm thư mục report mới nhất
$latestReport = Get-ChildItem $ReportDir -Directory | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $latestReport) {
    Write-Host "❌ No report found in $ReportDir" -ForegroundColor Red
    exit 1
}

Write-Host "📂 Processing report: $($latestReport.Name)" -ForegroundColor Yellow

# BƯỚC 1: Đếm và xóa req_*.html files
$reqFiles = Get-ChildItem $latestReport.FullName -Filter "req_*.html"
$totalCount = $reqFiles.Count
$totalSizeMB = ($reqFiles | Measure-Object -Property Length -Sum).Sum / 1MB

Write-Host "`n📊 Found $totalCount req_*.html files (~$([math]::Round($totalSizeMB, 2)) MB)" -ForegroundColor Yellow

if ($KeepOneSample) {
    # Giữ lại 1 file mẫu cho mỗi loại request
    Write-Host "🔧 Keeping 1 sample file per request type..." -ForegroundColor Cyan
    $reqFiles | Group-Object { $_.Name -replace '-\d+-\d+\.html$', '' } | ForEach-Object {
        $_.Group | Sort-Object LastWriteTime -Descending | Select-Object -Skip 1 | Remove-Item -Force
    }
    $remaining = (Get-ChildItem $latestReport.FullName -Filter "req_*.html").Count
    Write-Host "✅ Deleted $($totalCount - $remaining) files, kept $remaining samples" -ForegroundColor Green
} else {
    # Xóa toàn bộ
    Write-Host "🗑️  Deleting ALL req_*.html files..." -ForegroundColor Cyan
    $reqFiles | Remove-Item -Force
    Write-Host "✅ Deleted all $totalCount files" -ForegroundColor Green
}

# BƯỚC 2: Tính dung lượng còn lại
$remainingSize = (Get-ChildItem $latestReport.FullName -Recurse -File | Measure-Object -Property Length -Sum).Sum / 1MB
Write-Host "💾 Report size after cleanup: ~$([math]::Round($remainingSize, 2)) MB" -ForegroundColor Green

# BƯỚC 3: Mở report trong browser
$indexFile = Join-Path $latestReport.FullName "index.html"
Write-Host "`n📍 Report location:" -ForegroundColor Cyan
Write-Host "   file:///$($indexFile -replace '\\','/')" -ForegroundColor White

Write-Host "`n✨ Cleanup completed!`n" -ForegroundColor Green

