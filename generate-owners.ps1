# Script to generate 5000 owners for Gatling load testing
$firstNames = @("James","Mary","John","Patricia","Robert","Jennifer","Michael","Linda","William","Elizabeth","David","Barbara","Richard","Susan","Joseph","Jessica","Thomas","Sarah","Charles","Karen","Christopher","Nancy","Daniel","Lisa","Matthew","Betty","Anthony","Margaret","Mark","Sandra","Donald","Ashley","Steven","Kimberly","Paul","Emily","Andrew","Donna","Joshua","Michelle","Kenneth","Carol","Kevin","Amanda","Brian","Dorothy","George","Melissa","Edward","Deborah")
$lastNames = @("Smith","Johnson","Williams","Brown","Jones","Garcia","Miller","Davis","Rodriguez","Martinez","Hernandez","Lopez","Gonzalez","Wilson","Anderson","Thomas","Taylor","Moore","Jackson","Martin","Lee","Perez","Thompson","White","Harris","Sanchez","Clark","Ramirez","Lewis","Robinson","Walker","Young","Allen","King","Wright","Scott","Torres","Nguyen","Hill","Flores","Green","Adams","Nelson","Baker","Hall","Rivera","Campbell","Mitchell","Carter","Roberts")
$cities = @("Madison","Sun Prairie","McFarland","Windsor","Monona","Waunakee","Middleton","Verona","Fitchburg","Oregon","Stoughton","DeForest","Cottage Grove","Cross Plains","Black Earth","Mount Horeb","New Glarus","Belleville")
$streets = @("Main St","Oak Ave","Maple Dr","Cedar Ln","Pine Rd","Elm St","Washington Ave","Park Blvd","Lake St","River Rd","Hill Dr","Valley Way","Forest Ln","Meadow Dr","Sunset Blvd","Highland Ave","Spring St","Church St","School Rd","Mill St")

# Create CSV header
$csv = "firstName,lastName,address,city,telephone`n"

# Generate 5000 unique owners
for ($i = 1; $i -le 5000; $i++) {
    $firstName = $firstNames | Get-Random
    $lastName = $lastNames | Get-Random
    $streetNumber = Get-Random -Minimum 100 -Maximum 9999
    $street = $streets | Get-Random
    $address = "$streetNumber $street"
    $city = $cities | Get-Random
    $telephone = "608555" + (Get-Random -Minimum 1000 -Maximum 9999)
    
    $csv += "$firstName,$lastName,$address,$city,$telephone`n"
}

# Save to file
$outputPath = "C:\Users\PC\Documents\GitHub\kiemthugatling\src\test\resources\data\owners.csv"
$csv | Out-File -FilePath $outputPath -Encoding UTF8 -NoNewline

Write-Host "Generated 5000 owners in $outputPath"
$fileSize = (Get-Item $outputPath).Length / 1KB
Write-Host "File size: $fileSize KB"
