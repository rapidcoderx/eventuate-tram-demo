# Balance Service

## Overview
The Balance Service is a microservice that manages account creation and balance updates as part of the Eventuate Tram Microservices Demo. It provides REST APIs for account management and listens to transaction events from the Transaction Service to update account balances accordingly.

## Features
- Create new accounts with initial balances
- Retrieve account information
- Validate account balances for transaction processing
- React to transaction events (PAYMENT/DEPOSIT) to update balances
- Transactional messaging using Eventuate Tram
## Event Architecture

The Balance Service implements the **Transactional Outbox Pattern** for reliable event publishing:

### How It Works

1. **Transactional Database Operations**:
    - When an account is created, updated, or closed, the service saves both:
        - The account entity in the `account` table
        - The corresponding event in the `eventuate.message` table
    - Both operations occur within the same database transaction
    - If either operation fails, the entire transaction is rolled back

2. **Change Data Capture (CDC)**:
    - A dedicated CDC service polls the `eventuate.message` table for unpublished messages
    - The CDC service publishes these messages to Kafka
    - After successful publishing, the CDC service updates the `published` flag

3. **Event Consumption**:
    - The Transaction Service subscribes to these events
    - It maintains a local cache of account data based on these events
    - This enables the Transaction Service to validate transactions locally when possible

### Benefits of This Approach

- **Data Consistency**: Account state and events are always consistent due to the transactional approach
- **Reliability**: No events are lost, even if Kafka is temporarily unavailable
- **Eventual Consistency**: Services eventually converge to a consistent state
- **Loose Coupling**: Services communicate primarily through events rather than direct API calls
- **Resilience**: Services can continue operating with basic functionality even when other services are down

### Technical Implementation

The implementation uses:
- **Eventuate Tram**: For the transactional messaging infrastructure
- **PostgreSQL**: For storing both domain data and outbox messages
- **CDC Service**: For reliably publishing events from the database to Kafka
- **Kafka**: As the message broker for publishing/subscribing to events

This architecture ensures that all domain events are reliably published and processed, maintaining eventual consistency across the distributed system.

https://github.com/wuyichen24/microservices-patterns/blob/master/docs/services/eventuate_cdc_service.md

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