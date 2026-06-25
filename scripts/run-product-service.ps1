# Starts the Product Service on port 8081
# Requires: Service Registry, PostgreSQL, Kafka

Set-Location "$PSScriptRoot\..\product-service"
Write-Host "Starting Product Service on port 8081..." -ForegroundColor Cyan
Write-Host "Ensure Service Registry, PostgreSQL, and Kafka are running" -ForegroundColor Yellow
mvn spring-boot:run
