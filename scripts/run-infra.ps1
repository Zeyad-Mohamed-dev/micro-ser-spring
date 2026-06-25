# Starts PostgreSQL and Kafka as Docker containers for local development
# Run this before starting individual services

Write-Host "=== Starting Infrastructure ===" -ForegroundColor Cyan

Write-Host "Starting PostgreSQL on port 5432..." -ForegroundColor Yellow
docker rm -f micro-postgres 2>$null
docker run -d `
  --name micro-postgres `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -p 5432:5432 `
  -v "$(Resolve-Path "$PSScriptRoot\..\initdb"):/docker-entrypoint-initdb.d" `
  postgres:16-alpine

if ($LASTEXITCODE -ne 0) {
  Write-Host "Failed to start PostgreSQL!" -ForegroundColor Red
  exit 1
}

Write-Host "Starting Kafka on port 9092..." -ForegroundColor Yellow
docker rm -f kafka 2>$null
docker run -d `
  --name kafka `
  -p 9092:9092 `
  -e KAFKA_NODE_ID=1 `
  -e KAFKA_PROCESS_ROLES=broker,controller `
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 `
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER `
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM1Tk `
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT `
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093 `
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 `
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 `
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 `
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 `
  -e KAFKA_AUTO_CREATE_TOPICS_ENABLE=true `
  -e KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=0 `
  confluentinc/cp-kafka:7.6.0

if ($LASTEXITCODE -ne 0) {
  Write-Host "Failed to start Kafka!" -ForegroundColor Red
  exit 1
}

Write-Host "Waiting for PostgreSQL to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

Write-Host "=== Infrastructure is running ===" -ForegroundColor Green
Write-Host "  PostgreSQL: localhost:5432 (user=postgres, pass=postgres)" -ForegroundColor Green
Write-Host "  Kafka:      localhost:9092" -ForegroundColor Green
