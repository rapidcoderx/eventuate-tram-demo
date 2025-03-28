# Transaction Service

## Overview
The Transaction Service is a microservice that handles transaction processing in the Eventuate Tram Microservices Demo. It coordinates with the Balance Service to validate accounts and publishes transaction events for balance updates, demonstrating an event-driven architecture.

## Features
- Create transactions (PAYMENT/DEPOSIT)
- Retrieve transaction details
- Get transaction history for an account
- Validate transactions with the Balance Service via REST API
- Publish transaction events to Kafka using Eventuate Tram

## Technical Stack
- Java 17
- Spring Boot 3.4.4
- Spring Data JPA
- Spring WebFlux for REST client
- Eventuate Tram for event publishing
- PostgreSQL database
- Kafka for messaging

## API Endpoints

### Create Transaction
```
POST /transactions
```
**Request Body:**
```json
{
  "accountId": "your-account-id",
  "amount": 250.00,
  "type": "DEPOSIT",  
  "description": "Initial deposit"
}
```
**Response:** The created transaction object with generated ID

### Get Transaction
```
GET /transactions/{transactionId}
```
**Response:** Transaction details or 404 if not found

### Get Transactions by Account
```
GET /transactions/by-account/{accountId}
```
**Response:** List of transactions for the specified account

## Transaction Flow
1. When a transaction is created, the service validates the account by calling the Balance Service
2. If validation passes, the transaction is saved in the database
3. A `TransactionCreatedEvent` is published to Kafka
4. The Balance Service consumes this event and updates the account balance accordingly

## Event Publishing
The service publishes the following events:
- `TransactionCreatedEvent`: Contains transaction details including accountId, amount, type, etc.

## Configuration
The main configuration is in `application.properties`:
```properties
# Application settings
spring.application.name=transaction-service
server.port=8082

# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/transaction_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Eventuate Tram settings
eventuatelocal.kafka.bootstrap.servers=localhost:9092
```

## Building and Running
You can run the service in multiple ways:

### Using Maven
```bash
cd transaction-service
mvn spring-boot:run
```

### Using Docker
```bash
# Build the Docker image
docker build -t transaction-service .

# Run the container
docker run -p 8082:8082 transaction-service
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
# Create a transaction
curl -X POST http://localhost:8082/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "your-account-id",
    "amount": 250.00,
    "type": "DEPOSIT",
    "description": "Initial deposit"
  }'

# Get transactions by account
curl -X GET http://localhost:8082/transactions/by-account/your-account-id
```

## Troubleshooting
- If the service fails to connect to PostgreSQL, ensure the database is running and accessible
- If the Balance Service validation fails, check that the Balance Service is running
- If event publishing isn't working, check that Kafka is running and the topics are created
- Check logs with `docker compose logs transaction-service` when running with Docker Compose