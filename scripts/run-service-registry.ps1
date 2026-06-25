# Starts the Service Registry (Eureka Server) on port 8761

Set-Location "$PSScriptRoot\..\service-registry"
Write-Host "Starting Service Registry on port 8761..." -ForegroundColor Cyan
mvn spring-boot:run
