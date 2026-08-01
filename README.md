# Expense Tracker API

A REST API for managing personal expenses, built with Spring Boot. Data is persisted to a local JSON file (`expenses.json`).

## Prerequisites

- Java 21
- Maven (or use the included wrapper — no local install needed)

## Installation

Clone the repo, then from the project root:

```bash
./mvnw clean install
```

(Windows: `.\mvnw.cmd clean install`)

This downloads all dependencies and compiles the project.

## Running the server

```bash
./mvnw spring-boot:run
```

(Windows: `.\mvnw.cmd spring-boot:run`)

Server starts on `http://localhost:8080`.

## Running tests

```bash
./mvnw test
```

(Windows: `.\mvnw.cmd test`)

## API Endpoints

| Method | Path | Description |
|---|---|---|
| POST | /expenses | Create an expense |
| GET | /expenses | List all expenses |
| GET | /expenses?category={category} | Filter by category |
| GET | /expenses/{id} | Get a single expense |
| DELETE | /expenses/{id} | Delete an expense |
| GET | /expenses/summary | Total + per-category breakdown |
| GET | /expenses/summary/monthly | Totals grouped by month (YYYY-MM) |

### Example request

```bash
curl -X POST http://localhost:8080/expenses \
  -H "Content-Type: application/json" \
  -d '{"title":"Groceries","amount":45.30,"category":"Food","date":"2026-06-05"}'
```

## Data storage

Expenses are stored in `expenses.json` in the project root, created automatically on first write. No database required.
