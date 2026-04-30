$body = '{"username":"newadmin","password":"admin123","email":"newadmin@example.com"}'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri 'http://localhost:9003/api/auth/register' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing | Select-Object -ExpandProperty Content
