# Retirement Plan - Auto-Investment Platform

A production-grade REST API that helps users automate retirement savings by analyzing spending patterns, applying investment rules, and recommending the optimal NPS vs Index Fund strategy.

Built for the BlackRock Hackathon Challenge.

---

## Problem Statement

Most people know they should save for retirement but lack clarity on:
- How much they can realistically save from daily expenses
- Where to invest -- NPS (stable + tax benefit) or Index Fund (higher growth)
- What temporal rules affect their savings
- Whether they are financially ready to start investing

This platform answers all of these by analyzing real transaction data and providing actionable, personalized recommendations.

---

## Architecture

```
+----------------------------------------------------------+
|                   REST API (Port 5477)                   |
|              /blackrock/challenge/v1/*                    |
+----------+----------+-----------+-----------+------------+
|  Parse   | Validate |  Filter   |  Returns  |   System   |
| Expenses |  Rules   | Temporal  | NPS/Index |   Health   |
|          |          | Q / P / K | + Compare |            |
+----------+----------+-----------+-----------+------------+
|              Service Layer (Business Logic)               |
|  Transaction | Validation | Filter | Investment | Summary |
+----------------------------------------------------------+
|                   Java 17 + Spring Boot 3.2               |
+----------------------------------------------------------+
```

## Tech Stack

| Technology   | Version              | Purpose           |
|--------------|----------------------|-------------------|
| Java         | 17 (Eclipse Temurin) | Runtime           |
| Spring Boot  | 3.2.3                | REST Framework    |
| Maven        | 3.9+ (wrapper incl.) | Build Tool        |
| Docker       | Multi-stage Alpine   | Containerization  |
| JUnit 5      | 5.10+                | Testing           |

---

## Quick Start

### Prerequisites
- Java 17+ or Docker

### Option 1: Run with Java
```bash
# Clone
git clone https://github.com/Abhi7033/Retirement-plan.git
cd Retirement-plan

# Build and Run
./mvnw clean package -DskipTests
java -jar target/retirement-plan-1.0.0.jar
```

### Option 2: Run with Docker
```bash
# Build
docker build -t blk-hacking-ind-abhishek-anand .

# Run
docker run -d -p 5477:5477 blk-hacking-ind-abhishek-anand
```

The API is available at **http://localhost:5477**

### Verify
```bash
curl http://localhost:5477/blackrock/challenge/v1/health
```

---

## Running Tests

```bash
# Run all 55 unit tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=InvestmentServiceTest
```

Test coverage includes:
- **TransactionServiceTest** -- Parsing, ceiling rounding, timestamp truncation (7 tests)
- **ValidationServiceTest** -- Negative amounts, duplicates, max limits, ceiling/remanent consistency (11 tests)
- **TemporalFilterServiceTest** -- Q/P/K period logic, edge cases (9 tests)
- **InvestmentServiceTest** -- NPS/Index returns, profit, tax benefit (9 tests)
- **PerformanceServiceTest** -- Uptime format, memory, threads (4 tests)
- **SummaryServiceTest** -- Spending analysis, readiness scoring (8 tests)
- **CompareServiceTest** -- NPS vs Index comparison, risk profiling (7 tests)

---

## API Reference

Base URL: `http://localhost:5477/blackrock/challenge/v1`

### Core Endpoints

#### 1. Parse Expenses -- POST /transactions:parse

Converts raw expenses into investment-ready transactions. Rounds each expense up to the next 100 (ceiling) and calculates the spare change (remanent) that can be auto-invested.

```json
// Request
{
  "expenses": [
    { "timestamp": "2024-02-15 12:30:45", "amount": 150.75 },
    { "timestamp": "2024-03-10 09:00:00", "amount": 620.00 }
  ]
}

// Response -- seconds truncated, ceiling = next 100 multiple
{
  "transactions": [
    { "date": "2024-02-15 12:30:00", "amount": 150.75, "ceiling": 200.0, "remanent": 49.25 },
    { "date": "2024-03-10 09:00:00", "amount": 620.0, "ceiling": 700.0, "remanent": 80.0 }
  ]
}
```

---

#### 2. Validate Transactions -- POST /transactions:validator

Applies real-world business rules to ensure data integrity:
- Negative amounts are rejected
- Duplicate timestamps -- second occurrence is rejected
- Amount >= 5,00,000 -- rejected (unrealistic for daily expense)
- Ceiling < amount -- rejected (cannot round down)
- Ceiling not a multiple of 100 -- rejected
- Remanent != ceiling - amount -- rejected (data inconsistency)

```json
// Request
{ "wage": 50000, "transactions": [...] }

// Response
{ "valid": [...], "invalid": [{ "date": "...", "amount": -50, "message": "Negative amounts are not allowed" }] }
```

---

#### 3. Filter with Temporal Rules -- POST /transactions:filter

Applies time-based investment rules:
- **Q-periods** (fixed override): Replace remanent with a fixed amount during specific periods
- **P-periods** (extra boost): Add bonus savings during promotional windows
- **K-periods** (grouping): Group transactions into investment windows

Transactions with `remanent = 0` (e.g., from a q-period with `fixed: 0`) are kept as valid -- they represent real transactions with zero savings.

```json
// Request
{
  "wage": 50000,
  "q": [{ "fixed": 0, "start": "2024-02-01 00:00", "end": "2024-03-01 00:00" }],
  "p": [{ "extra": 25, "start": "2024-01-01 00:00", "end": "2024-04-01 00:00" }],
  "k": [{ "start": "2024-01-01 00:00", "end": "2024-06-01 00:00" }],
  "transactions": [...]
}

// Response -- includes inKPeriod flag for grouping
{
  "valid": [
    { "date": "...", "amount": 150.75, "ceiling": 200.0, "remanent": 74.25, "inKPeriod": true }
  ],
  "invalid": [...]
}
```

---

#### 4. NPS Returns -- POST /returns:nps

Calculates projected National Pension Scheme returns:
- Rate: 7.11% annual compound interest
- Inflation adjusted: Returns divided by (1 + inflation)^years
- Tax benefit: Deduction under Section 80CCD (min of invested, 10% income, 2L)
- Tax slabs: 0-7L: 0%, 7-10L: 10%, 10-12L: 15%, 12-15L: 20%, 15L+: 30%
- Investment horizon: max(60 - age, 5) years

```json
// Request
{ "age": 30, "wage": 50000, "inflation": 6.0, "q": [...], "p": [...], "k": [...], "transactions": [...] }

// Response
{
  "totalTransactionAmount": 150.75,
  "totalCeiling": 200.0,
  "savingsByDates": [
    { "start": "...", "end": "...", "amount": 49.25, "profit": 18.07, "taxBenefit": 4.93 }
  ]
}
```

---

#### 5. Index Fund Returns -- POST /returns:index

Calculates projected NIFTY 50 Index Fund returns:
- Rate: 14.49% annual compound interest
- Inflation adjusted: Same as NPS
- No tax benefit (but higher market returns)

Same request/response format as NPS.

---

### Innovation Endpoints

#### 6. Compare NPS vs Index -- POST /returns:compare

Side-by-side comparison with personalized recommendation based on:
- **Age** -- determines risk profile (Aggressive / Moderate / Conservative)
- **Income** -- higher earners benefit more from NPS tax deductions
- **Returns** -- actual projected numbers for both options

```json
// Response (abbreviated)
{
  "totalTransactionAmount": 1246.25,
  "totalInvestable": 148.75,
  "npsTotalProfit": 54.57,
  "npsTotalTaxBenefit": 0.0,
  "npsEffectiveGain": 54.57,
  "indexTotalProfit": 1352.0,
  "indexEffectiveGain": 1352.0,
  "recommendation": "Index Fund generates higher returns for your profile...",
  "riskProfile": "Aggressive",
  "suggestedNpsPercent": 30,
  "suggestedIndexPercent": 70,
  "reasoning": "At 30, you have 30 years till retirement..."
}
```

---

#### 7. Transaction Summary -- POST /transactions:summary

Analyzes spending behavior and calculates an Investment Readiness Score (0-100):

```json
// Response
{
  "totalTransactions": 5,
  "validTransactions": 4,
  "totalSpent": 1246.25,
  "averageSpend": 311.56,
  "totalSavingsPotential": 153.75,
  "annualSavingsProjection": 13837.56,
  "investmentReadinessScore": 70,
  "investmentReadinessLabel": "Good - Can start regular investments",
  "tips": [
    "Consider splitting investments between NPS and Index Funds."
  ]
}
```

---

#### 8. Health Check -- GET /health

Liveness probe for monitoring and container orchestration.

```json
{ "status": "UP", "service": "retirement-plan", "version": "1.0.0", "timestamp": 1771671086359 }
```

---

#### 9. Performance Metrics -- GET /performance

System metrics: uptime, heap memory (MB), active threads.

```json
{ "time": "1970-01-01 00:01:11.716", "memory": "31.89", "threads": 16 }
```

---

## Project Structure

```
src/
├── main/java/com/blackrock/retirement/
│   ├── controller/
│   │   ├── TransactionController.java    # parse, validator, filter, summary
│   │   ├── ReturnsController.java        # nps, index, compare
│   │   └── PerformanceController.java    # performance, health
│   ├── service/
│   │   ├── TransactionService.java       # Expense to Transaction conversion
│   │   ├── ValidationService.java        # Business rule validation
│   │   ├── TemporalFilterService.java    # Q/P/K temporal constraint engine
│   │   ├── InvestmentService.java        # NPS, Index, Compare calculations
│   │   ├── SummaryService.java           # Spending insights and readiness score
│   │   └── PerformanceService.java       # JMX system metrics
│   ├── model/                            # Domain entities
│   └── dto/                              # Request/Response DTOs
└── test/java/                            # 55 unit tests
```

## Docker

- **Image:** `blk-hacking-ind-abhishek-anand`
- **Port:** `5477`
- **Base:** Eclipse Temurin 17 Alpine (JRE)
- **Size:** ~205 MB

```bash
docker run -d -p 5477:5477 blk-hacking-ind-abhishek-anand
```

---

## Author

**Abhishek Anand**
GitHub: [@Abhi7033](https://github.com/Abhi7033)
