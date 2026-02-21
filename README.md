# Retirement Plan - Auto Investment API

A Spring Boot REST API for automating retirement savings calculations, built for the BlackRock Hackathon Challenge. The system parses expenses, validates transactions, applies temporal constraints, and computes projected returns for NPS and Index Fund investments.

## Tech Stack

- **Java 17** (Eclipse Temurin)
- **Spring Boot 3.2.3**
- **Maven** (with wrapper included)
- **Docker** (multi-stage Alpine build)

## Prerequisites

- Java 17+ (`java -version`)
- Maven 3.6+ (`mvn -version`) — *or use the included Maven wrapper*
- Docker (optional, for containerized deployment)

## Project Structure

```
src/
├── main/java/com/blackrock/retirement/
│   ├── controller/
│   │   ├── TransactionController.java    # /transactions:parse, :validator, :filter
│   │   ├── ReturnsController.java        # /returns:nps, /returns:index
│   │   └── PerformanceController.java    # /performance
│   ├── service/
│   │   ├── TransactionService.java       # Expense parsing & ceiling calculation
│   │   ├── ValidationService.java        # Transaction validation rules
│   │   ├── TemporalFilterService.java    # Q/P/K period temporal constraints
│   │   ├── InvestmentService.java        # NPS & Index Fund return calculations
│   │   └── PerformanceService.java       # JMX-based system metrics
│   ├── model/                            # Domain entities
│   └── dto/                              # Request/Response DTOs
└── test/java/com/blackrock/retirement/
    └── service/                          # 36 unit tests across 5 test classes
```

## Configuration

The application runs on **port 5477** by default. To change it, edit `src/main/resources/application.properties`:

```properties
server.port=5477
spring.application.name=retirement-plan
```

## Build & Run

### Using Maven

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
java -jar target/retirement-plan-1.0.0.jar
```

### Using Maven Wrapper (no Maven installation needed)

```bash
./mvnw clean package -DskipTests
java -jar target/retirement-plan-1.0.0.jar
```

### Using Docker

```bash
# Build the Docker image
docker build -t blk-hacking-ind-abhishek-anand .

# Run the container
docker run -d -p 5477:5477 blk-hacking-ind-abhishek-anand
```

The API will be available at `http://localhost:5477`

## Run Tests

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=TransactionServiceTest

# Run with Maven wrapper
./mvnw test
```

## API Endpoints

All endpoints are prefixed with `/blackrock/challenge/v1`.

### 1. Parse Expenses
**POST** `/blackrock/challenge/v1/transactions:parse`

Converts raw expenses into transactions by rounding amounts up to the next multiple of 100 (ceiling) and calculating the remanent (difference).

```json
// Request
{
  "expenses": [
    { "timestamp": "2024-02-15 12:30:45", "amount": 150.75 }
  ]
}

// Response
{
  "transactions": [
    { "date": "2024-02-15 12:30:00", "amount": 150.75, "ceiling": 200.0, "remanent": 49.25 }
  ]
}
```

### 2. Validate Transactions
**POST** `/blackrock/challenge/v1/transactions:validator`

Validates transactions against business rules: no negative amounts, no duplicates, amount < 500,000.

```json
// Request
{ "wage": 50000, "transactions": [...] }

// Response
{ "valid": [...], "invalid": [...] }
```

### 3. Filter Transactions
**POST** `/blackrock/challenge/v1/transactions:filter`

Applies temporal constraints (Q-periods for fixed override, P-periods for additive extras, K-periods for grouping) to transactions.

```json
// Request
{ "wage": 50000, "q": [...], "p": [...], "k": [...], "transactions": [...] }

// Response
{ "valid": [...], "invalid": [...] }
```

### 4. NPS Returns
**POST** `/blackrock/challenge/v1/returns:nps`

Calculates projected NPS returns at **7.11% annual rate** with inflation adjustment and tax benefit computation using Indian tax slabs.

```json
// Request
{ "age": 30, "wage": 50000, "inflation": 6.0, "q": [...], "p": [...], "k": [...], "transactions": [...] }

// Response
{ "totalTransactionAmount": 150.75, "totalCeiling": 200.0, "savingsByDates": [...] }
```

### 5. Index Fund Returns
**POST** `/blackrock/challenge/v1/returns:index`

Calculates projected NIFTY 50 Index Fund returns at **14.49% annual rate** with inflation adjustment.

### 6. Performance Metrics
**GET** `/blackrock/challenge/v1/performance`

Returns system uptime, heap memory usage (MB), and active thread count.

```json
{ "time": "1970-01-01 00:00:15.234", "memory": "25.50", "threads": 18 }
```

## Docker Image

- **Image name:** `blk-hacking-ind-abhishek-anand`
- **Port:** `5477`
- **Base:** Eclipse Temurin 17 Alpine (JRE)
- **Size:** ~205 MB

## Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| spring-boot-starter-web | 3.2.3 | REST API framework |
| spring-boot-starter-validation | 3.2.3 | Bean validation |
| spring-boot-starter-test | 3.2.3 | Testing (JUnit 5) |

## Author

**Abhishek Anand**
