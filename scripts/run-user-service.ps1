# Starts the User Service on port 8084
# Requires: Service Registry, PostgreSQL, Kafka

Set-Location "$PSScriptRoot\..\user-service"
Write-Host "Starting User Service on port 8084..." -ForegroundColor Cyan
Write-Host "Ensure Service Registry, PostgreSQL, and Kafka are running" -ForegroundColor Yellow
mvn spring-boot:run
