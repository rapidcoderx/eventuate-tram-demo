# Eventuate Tram Microservices Demo

This project demonstrates the use of Eventuate Tram for implementing transactional messaging between microservices.

## Project Structure

- **Balance Service**: Manages account creation and balances
- **Transaction Service**: Handles transaction processing with coordination with Balance Service

## Prerequisites

- JDK 17
- Maven
- Docker and Docker Compose

## Infrastructure Setup

1. Create the necessary directory for PostgreSQL initialization script:

```bash
mkdir -p docker/postgres
```

2. Make the initialization script executable:

```bash
cp postgres-init-script.sh docker/postgres/create-multiple-dbs.sh
chmod +x docker/postgres/create-multiple-dbs.sh
```

3. Start the infrastructure (PostgreSQL, Kafka, Zookeeper):

```bash
docker-compose up -d
```

## Running the Balance Service

```bash
cd balance-service
mvn spring-boot:run
```

## Testing the Balance Service

1. Create a new account:

```bash
curl -X POST http://localhost:8081/accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerName":"John Doe","initialBalance":1000.00}'
```

2. Get account details:

```bash
curl -X GET http://localhost:8081/accounts/{accountId}
```

3. Validate account balance:

```bash
curl -X GET "http://localhost:8081/accounts/{accountId}/validate?amount=500.00"
```

## Database Schema

The PostgreSQL databases will have the following schemas:

1. **balance_db**: Stores account information
    - Account table
    - Eventuate messaging tables

2. **transaction_db**: Will store transaction data (to be implemented)
    - Transaction table
    - Eventuate messaging tables

## Next Steps

- Implementation of the Transaction Service
- Testing the communication between services using Eventuate Tram
- Adding unit and integration tests