# Balance Service

## Overview
The Balance Service is a microservice that manages account creation and balance updates as part of the Eventuate Tram Microservices Demo. It provides REST APIs for account management and listens to transaction events from the Transaction Service to update account balances accordingly.

## Features
- Create new accounts with initial balances
- Retrieve account information
- Validate account balances for transaction processing
- React to transaction events (PAYMENT/DEPOSIT) to update balances
- Transactional messaging using Eventuate Tram

## Technical Stack
- Java 17
- Spring Boot 3.4.4
- Spring Data JPA
- Eventuate Tram messaging
- PostgreSQL database
- Kafka for messaging

## API Endpoints

### Create Account
```
POST /accounts
```
**Request Body:**
```json
{
  "ownerName": "John Doe",
  "initialBalance": 1000.00
}
```
**Response:** The created account object with generated ID

### Get Account
```
GET /accounts/{accountId}
```
**Response:** Account details or 404 if not found

### Validate Account
```
GET /accounts/{accountId}/validate?amount=500.00
```
**Response:**
```json
{
  "valid": true
}
```

## Event Handling
The Balance Service consumes `TransactionCreatedEvent` messages from Kafka:
- For DEPOSIT events, it credits the account
- For PAYMENT events, it debits the account

## Configuration
The main configuration is in `application.properties`:
```properties
# Application settings
spring.application.name=balance-service
server.port=8081

# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/balance_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Eventuate Tram settings
eventuatelocal.kafka.bootstrap.servers=localhost:9092
```

## Building and Running
You can run the service in multiple ways:

### Using Maven
```bash
cd balance-service
mvn spring-boot:run
```

### Using Docker
```bash
# Build the Docker image
docker build -t balance-service .

# Run the container
docker run -p 8081:8081 balance-service
```

### Using the build-and-run.sh Script
The repository includes a `build-and-run.sh` script that builds and runs all services:
```bash
chmod +x build-and-run.sh
./build-and-run.sh
```

## Docker Compose
The service is also configured in `docker-compose.yml` to run as part of the complete microservices demo:
```bash
docker compose up -d
```

## Testing
You can test the service using curl:
```bash
# Create an account
curl -X POST http://localhost:8081/accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerName":"John Doe","initialBalance":1000.00}'

# Get the account
curl -X GET http://localhost:8081/accounts/{accountId}
```

## Troubleshooting
- If the service fails to connect to PostgreSQL, ensure the database is running and accessible
- If event handling isn't working, check that Kafka is running and the topics are created
- Check logs with `docker compose logs balance-service` when running with Docker Compose