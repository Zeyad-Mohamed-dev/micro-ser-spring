# Starts the Order Service on port 8082
# Requires: Service Registry, PostgreSQL, Kafka

Set-Location "$PSScriptRoot\..\order-service"
Write-Host "Starting Order Service on port 8082..." -ForegroundColor Cyan
Write-Host "Ensure Service Registry, PostgreSQL, and Kafka are running" -ForegroundColor Yellow
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"
mvn spring-boot:run
