# Eventuate Tram Microservices Demo

This project demonstrates the use of Eventuate Tram for implementing transactional messaging between microservices. The application consists of two services that communicate using the Eventuate Tram framework:

1. **Balance Service**: Manages account creation and balances
2. **Transaction Service**: Handles transaction processing and coordinates with the Balance Service

## Project Structure

```
eventuate-tram-demo/
├── pom.xml                   # Parent POM file
├── balance-service/          # Balance Service module
└── transaction-service/      # Transaction Service module
```

## Prerequisites

- JDK 17
- Maven
- Docker and Docker Compose
- PostgreSQL client (optional, for direct DB access)

## How It Works

1. The Balance Service allows creating accounts and maintaining balances.
2. The Transaction Service processes transactions (PAYMENT or DEPOSIT).
3. When a transaction is created:
    - The Transaction Service validates the account by calling the Balance Service via REST API
    - The Transaction Service publishes a TransactionCreatedEvent
    - The Balance Service subscribes to these events and updates account balances

## Getting Started

### 1. Start Infrastructure

Start PostgreSQL, Kafka, and Zookeeper:

```bash
docker-compose up -d
```

### 2. Build the Application

```bash
mvn clean package
```

### 3. Start the Services

Start each service in separate terminal windows:

```bash
# Terminal 1
cd balance-service
mvn spring-boot:run

# Terminal 2
cd transaction-service
mvn spring-boot:run
```

## API Documentation

### Balance Service (PORT 8081)

**Create Account**
```
POST /accounts
{
  "ownerName": "John Doe",
  "initialBalance": 1000.00
}
```

**Get Account**
```
GET /accounts/{accountId}
```

**Validate Account**
```
GET /accounts/{accountId}/validate?amount=500.00
```

### Transaction Service (PORT 8082)

**Create Transaction**
```
POST /transactions
{
  "accountId": "your-account-id",
  "amount": 250.00,
  "type": "DEPOSIT",  // or "PAYMENT"
  "description": "Initial deposit"
}
```

**Get Transaction**
```
GET /transactions/{transactionId}
```

**Get Transactions by Account**
```
GET /transactions/by-account/{accountId}
```

## Testing the Application

Follow these steps to test the complete flow:

1. Create an account in the Balance Service
2. Create a deposit transaction in the Transaction Service
3. Create a payment transaction in the Transaction Service
4. Verify the balance is updated correctly

Example test sequence with curl:

```bash
# Create account
curl -X POST http://localhost:8081/accounts \
  -H "Content-Type: application/json" \
  -d '{"ownerName":"John Doe","initialBalance":1000.00}'

# Note the accountId returned, and use it in subsequent requests

# Create deposit
curl -X POST http://localhost:8082/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "your-account-id",
    "amount": 250.00,
    "type": "DEPOSIT",
    "description": "Initial deposit"
  }'

# Create payment
curl -X POST http://localhost:8082/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "your-account-id",
    "amount": 150.00,
    "type": "PAYMENT",
    "description": "Online purchase"
  }'

# Check balance
curl -X GET http://localhost:8081/accounts/your-account-id
```

## Technical Details

The application uses:

- Spring Boot 3.4.4
- Eventuate Tram for messaging between services
- PostgreSQL for data storage
- Kafka as the messaging broker

## Database Schema

Two separate PostgreSQL databases are used:

1. **balance_db**: Stores account information
    - Account table
    - Eventuate messaging tables

2. **transaction_db**: Stores transaction data
    - Transaction table
    - Eventuate messaging tables

## Troubleshooting

- If you encounter database connection issues, verify that PostgreSQL is running and accessible
- If messaging is not working, check that Kafka and Zookeeper are running
- Verify that both services can connect to the messaging infrastructure
