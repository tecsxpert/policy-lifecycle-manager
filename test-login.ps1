$body = '{"username":"admin","password":"admin123"}'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri 'http://localhost:9003/api/auth/login' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing | Select-Object -ExpandProperty Content
