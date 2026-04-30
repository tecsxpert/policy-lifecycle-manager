# Test login with newadmin user
$body = '{"username":"newadmin","password":"admin123"}'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$result = Invoke-WebRequest -Uri 'http://localhost:9003/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
Write-Host "Status Code:" $result.StatusCode
Write-Host "Content:" $result.Content
