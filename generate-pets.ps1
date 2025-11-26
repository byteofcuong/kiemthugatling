# Script to generate 5000 pets for Gatling load testing
$petNames = @("Fluffy","Max","Bella","Charlie","Luna","Cooper","Daisy","Milo","Lucy","Bailey","Rocky","Molly","Buddy","Sadie","Bear","Maggie","Duke","Sophie","Jack","Chloe","Oliver","Lola","Toby","Zoe","Tucker","Lily","Oscar","Penny","Leo","Zoey","Zeus","Coco","Bentley","Nala","Harley","Gracie","Murphy","Ruby","Winston","Stella","Teddy","Rosie","Simba","Ellie","Sam","Abby","Shadow","Angel","Blue","Princess","Diesel","Misty","Bandit","Ginger","Rusty","Precious","Scout","Cookie","King","Jasmine","Buster","Peanut","Riley","Pepper","Sammy","Lady","Thor","Sasha","Duke","Emma","Romeo","Oreo")
$petTypes = @(1,2,3,4,5,6) # cat, dog, lizard, snake, bird, hamster

# Years for birth dates (pets from 1-15 years old)
$currentYear = [DateTime]::Now.Year
$birthYears = ($currentYear - 15)..($currentYear - 1)

# Create CSV header
$csv = "name,birthDate,typeId`n"

# Generate 5000 unique pets
for ($i = 1; $i -le 5000; $i++) {
    $name = $petNames | Get-Random
    # Add number suffix for uniqueness
    $uniqueName = "$name$i"
    
    $year = $birthYears | Get-Random
    $month = Get-Random -Minimum 1 -Maximum 12
    $day = Get-Random -Minimum 1 -Maximum 28 # Safe day for all months
    $birthDate = "{0:D4}-{1:D2}-{2:D2}" -f $year, $month, $day
    
    $typeId = $petTypes | Get-Random
    
    $csv += "$uniqueName,$birthDate,$typeId`n"
}

# Save to file
$outputPath = "C:\Users\PC\Documents\GitHub\kiemthugatling\src\test\resources\data\pets.csv"
$csv | Out-File -FilePath $outputPath -Encoding UTF8 -NoNewline

Write-Host "Generated 5000 pets in $outputPath"
$fileSize = (Get-Item $outputPath).Length / 1KB
Write-Host "File size: $fileSize KB"
