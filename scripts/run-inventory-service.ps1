# Starts the Inventory Service on port 8083
# Requires: Service Registry, PostgreSQL, Kafka

Set-Location "$PSScriptRoot\..\inventory-service"
Write-Host "Starting Inventory Service on port 8083..." -ForegroundColor Cyan
Write-Host "Ensure Service Registry, PostgreSQL, and Kafka are running" -ForegroundColor Yellow
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
mvn spring-boot:run
