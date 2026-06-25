# Starts all infrastructure and services for the e-commerce microservices stack

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  E-Commerce Microservices - Full Stack  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Start infrastructure (PostgreSQL, Kafka)
Write-Host "[1/3] Starting infrastructure containers..." -ForegroundColor Cyan
& "$PSScriptRoot\run-infra.ps1"
Write-Host ""

# Step 2: Start Service Registry
Write-Host "[2/3] Starting Service Registry (Eureka)..." -ForegroundColor Cyan
$regJob = Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run-service-registry.ps1" -WindowStyle Normal
Write-Host "  Waiting 20 seconds for Service Registry to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 20
Write-Host ""

# Step 3: Start all services
Write-Host "[3/3] Starting all microservices..." -ForegroundColor Cyan

$gwJob = Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run-api-gateway.ps1" -WindowStyle Normal
$psJob = Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run-product-service.ps1" -WindowStyle Normal
$isJob = Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run-inventory-service.ps1" -WindowStyle Normal
$osJob = Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run-order-service.ps1" -WindowStyle Normal
$usJob = Start-Process powershell -ArgumentList "-NoExit", "-File", "$PSScriptRoot\run-user-service.ps1" -WindowStyle Normal

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  All services are launching!             " -ForegroundColor Green
Write-Host "                                        " -ForegroundColor Green
Write-Host "  Service Registry: http://localhost:8761 " -ForegroundColor Green
Write-Host "  API Gateway:      http://localhost:8080 " -ForegroundColor Green
Write-Host "  Product Service:  http://localhost:8081 " -ForegroundColor Green
Write-Host "  Order Service:    http://localhost:8082 " -ForegroundColor Green
Write-Host "  Inventory Service: http://localhost:8083 " -ForegroundColor Green
Write-Host "  User Service:     http://localhost:8084 " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
