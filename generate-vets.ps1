# Script to generate 5000 vets for Gatling load testing
$firstNames = @("James","Mary","John","Patricia","Robert","Jennifer","Michael","Linda","William","Elizabeth","David","Barbara","Richard","Susan","Joseph","Jessica","Thomas","Sarah","Charles","Karen","Christopher","Nancy","Daniel","Lisa","Matthew","Betty","Anthony","Margaret","Mark","Sandra","Donald","Ashley","Steven","Kimberly","Paul","Emily","Andrew","Donna","Joshua","Michelle","Kenneth","Carol","Kevin","Amanda","Brian","Dorothy","George","Melissa","Edward","Deborah","Jason","Stephanie","Ryan","Rebecca","Gary","Laura","Nicholas","Sharon","Eric","Cynthia","Stephen","Kathleen","Jacob","Amy","Larry","Angela","Jonathan","Helen","Frank","Brenda","Scott","Pamela","Justin","Nicole","Brandon","Samantha","Raymond","Katherine","Gregory","Christine","Samuel","Debra","Patrick","Rachel","Benjamin","Catherine","Jack","Carolyn","Dennis","Janet","Jerry","Maria","Tyler","Heather","Aaron","Diane","Henry","Julie","Douglas","Joyce","Peter","Victoria","Adam","Kelly","Nathan","Christina","Zachary","Lauren","Walter","Joan")
$lastNames = @("Smith","Johnson","Williams","Brown","Jones","Garcia","Miller","Davis","Rodriguez","Martinez","Hernandez","Lopez","Gonzalez","Wilson","Anderson","Thomas","Taylor","Moore","Jackson","Martin","Lee","Perez","Thompson","White","Harris","Sanchez","Clark","Ramirez","Lewis","Robinson","Walker","Young","Allen","King","Wright","Scott","Torres","Nguyen","Hill","Flores","Green","Adams","Nelson","Baker","Hall","Rivera","Campbell","Mitchell","Carter","Roberts","Gomez","Phillips","Evans","Turner","Diaz","Parker","Cruz","Edwards","Collins","Reyes","Stewart","Morris","Morales","Murphy","Cook","Rogers","Gutierrez","Ortiz","Morgan","Cooper","Peterson","Bailey","Reed","Kelly","Howard","Ramos","Kim","Cox","Ward","Richardson","Watson","Brooks","Chavez","Wood","James","Bennett","Gray","Mendoza","Ruiz")
$specialties = @(1,2,3) # radiology, surgery, dentistry

# Create CSV header
$csv = "firstName,lastName,specialtyIds`n"

# Generate 5000 unique vets
for ($i = 1; $i -le 5000; $i++) {
    $firstName = $firstNames | Get-Random
    $lastName = $lastNames | Get-Random
    
    # 60% have 1 specialty, 30% have 2, 10% have 3
    $rand = Get-Random -Minimum 1 -Maximum 100
    if ($rand -le 60) {
        # Single specialty
        $specialtyIds = ($specialties | Get-Random).ToString()
    }
    elseif ($rand -le 90) {
        # Two specialties
        $spec1 = $specialties | Get-Random
        $spec2 = $specialties | Where-Object { $_ -ne $spec1 } | Get-Random
        $specialtyIds = "$spec1,$spec2"
    }
    else {
        # All three specialties
        $specialtyIds = "1,2,3"
    }
    
    $csv += "$firstName,$lastName,`"$specialtyIds`"`n"
}

# Save to file
$outputPath = "C:\Users\PC\Documents\GitHub\kiemthugatling\src\test\resources\data\vets.csv"
$csv | Out-File -FilePath $outputPath -Encoding UTF8 -NoNewline

Write-Host "Generated 5000 vets in $outputPath"
$fileSize = (Get-Item $outputPath).Length / 1KB
Write-Host "File size: $fileSize KB"
