# Starts the API Gateway on port 8080
# Requires: Service Registry (run-service-registry.ps1)

Set-Location "$PSScriptRoot\..\api-gateway"
Write-Host "Starting API Gateway on port 8080..." -ForegroundColor Cyan
Write-Host "Ensure Service Registry is running on port 8761" -ForegroundColor Yellow
mvn spring-boot:run
