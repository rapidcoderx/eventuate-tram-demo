#!/bin/bash
set -e  # Exit on error
clear

export COMPOSE_BAKE=true

echo "=== Building Eventuate Tram Microservices Demo ==="

# Step 1: Build JAR files locally
echo "Building JAR files using Maven..."
mvn clean package -DskipTests

# Step 2: Remove existing balance and transaction service image
echo "Remove the existing images..."
docker rmi tram/balance-service:latest -f
docker rmi tram/transaction-service:latest -f

# Step 3: Build Docker images with no-cache
echo "Building Docker images with no-cache..."
docker build -t tram/balance-service:latest --no-cache -f ./balance-service/Dockerfile ./balance-service/target
docker build -t tram/transaction-service:latest --no-cache -f ./transaction-service/Dockerfile ./transaction-service/target
