#!/bin/bash
set -e  # Exit on error
export COMPOSE_BAKE=true
clear
echo "=== Building Eventuate Tram Microservices Demo ==="

# Step 1: Make database initialization script executable
echo "Making database initialization script executable..."
chmod +x create-multiple-dbs.sh

# Step 2: Build JAR files locally
echo "Building JAR files using Maven..."
mvn clean package -DskipTests

# Step 2.1: Build Docker images with no-cache
echo "Building Docker images with no-cache..."
docker build -t tram/balance-service:latest --no-cache -f ./balance-service/Dockerfile ./balance-service/target
docker build -t tram/transaction-service:latest --no-cache -f ./transaction-service/Dockerfile ./transaction-service/target

# Step 3: Bring everything down
echo "Stopping any running containers..."
docker compose down

# Step 4: Start infrastructure services first
echo "Starting infrastructure services (Postgres, Kafka)..."
docker compose up -d postgres kafka

# Step 5: Wait for infrastructure to be ready
echo "Waiting for infrastructure to be ready (30 seconds)..."
sleep 30

# Step 6: Start CDC service
echo "Starting CDC service for event publishing..."
docker compose up -d eventuate-cdc-service

# Step 7: Wait for CDC service to be ready
echo "Waiting for CDC service to be ready (10 seconds)..."
sleep 10

# Step 8: Start application services
echo "Starting application services..."
docker compose up -d balance-service transaction-service

# Step 9: Wait for application services to be ready
echo "Waiting for application services to be ready (30 seconds)..."
sleep 30

# Step 10: Start monitoring services
echo "Starting monitoring services..."
docker compose up -d prometheus kafka-ui kafka-exporter

echo "=== Deployment Complete ==="
echo "Services should now be up and running:"
echo "- Balance Service: http://localhost:8081"
echo "- Transaction Service: http://localhost:8082"
echo "- Kafka UI: http://localhost:9191"
echo "- Prometheus: http://localhost:9090"
echo "- CDC Service: http://localhost:8099"

echo ""
echo "You can check service status with: docker compose ps"
echo "You can check service logs with: docker compose logs -f [service-name]"
echo "To check CDC service logs: docker compose logs -f eventuate-cdc-service"