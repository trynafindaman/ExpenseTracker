# Expense Tracker API

A RESTful API for managing personal expenses, built with **Spring Boot**.

The application uses **file-based JSON persistence**, storing all expenses in a local `expenses.json` file. No database setup is required.

---

## Features

- Create, retrieve, and delete expenses
- Filter expenses by category (case-insensitive)
- View overall expense summaries
- View monthly expense summaries grouped by `YYYY-MM`
- Automatic input validation with consistent error responses
- File-based persistence using JSON
- Unit and integration tests

---

## Tech Stack

- Java 21
- Spring Boot
- Maven
- Jackson (JSON serialization)
- JUnit 5
- MockMvc

---

## Prerequisites

Before running the project, ensure you have:

- Java 21
- Maven *(or use the included Maven Wrapper)*

---

## Installation

Clone the repository and build the project:

```bash
./mvnw clean install
```

**Windows**

```powershell
.\mvnw.cmd clean install
```

This command downloads all dependencies, compiles the project, and runs the tests.

---

## Running the Application

Start the application with:

```bash
./mvnw spring-boot:run
```

**Windows**

```powershell
.\mvnw.cmd spring-boot:run
```

The API will be available at:

```
http://localhost:8080
```

---

## Running Tests

Execute all unit and integration tests:

```bash
./mvnw test
```

**Windows**

```powershell
.\mvnw.cmd test
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/expenses` | Create a new expense |
| GET | `/expenses` | Retrieve all expenses |
| GET | `/expenses?category={category}` | Retrieve expenses by category |
| GET | `/expenses/{id}` | Retrieve a single expense |
| DELETE | `/expenses/{id}` | Delete an expense |
| GET | `/expenses/summary` | Get total expenses and category breakdown |
| GET | `/expenses/summary/monthly` | Get monthly expense totals |

---

## Example Requests

### Create an Expense

```bash
curl -X POST http://localhost:8080/expenses \
  -H "Content-Type: application/json" \
  -d '{
        "title":"Groceries",
        "amount":45.30,
        "category":"Food",
        "date":"2026-06-05"
      }'
```

### Example Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Groceries",
  "amount": 45.30,
  "category": "Food",
  "date": "2026-06-05"
}
```

### Get All Expenses

```bash
curl http://localhost:8080/expenses
```

### Get Expense Summary

```bash
curl http://localhost:8080/expenses/summary
```

---

## Validation Rules

Incoming requests are validated before processing.

| Field | Rule |
|-------|------|
| `title` | Must not be blank |
| `category` | Must not be blank |
| `amount` | Must be greater than 0 |
| `date` | Must be a valid date (`YYYY-MM-DD`) |

Validation failures return **HTTP 400 Bad Request** with a JSON response describing the validation errors.

---

## HTTP Status Codes

| Status | Description |
|--------|-------------|
| 200 OK | Request completed successfully |
| 201 Created | Expense created successfully |
| 400 Bad Request | Validation failed |
| 404 Not Found | Expense not found |

---

## Error Response Format

All API errors follow a consistent structure.

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Expense not found with id: ...",
  "timestamp": "2026-08-01T10:15:30Z"
}
```

---

## Data Storage

Expenses are stored in a local `expenses.json` file located in the project root.

- The file is created automatically on the first write.
- No external database is required.
- The data file is excluded from version control using `.gitignore`.

---

## Design Decisions

- **UUIDs** are used as expense identifiers to avoid ID collisions and simplify file-based persistence.
- **JSON file storage** keeps the project lightweight and eliminates the need for database configuration.
- Business logic is separated from persistence through the `ExpenseStore` abstraction, making the application easier to test and extend.
- Validation and exception handling are centralized to provide consistent API responses.
- Category filtering is **case-insensitive**, while the original category value is preserved when stored.

---

## Testing

The project includes automated tests covering:

- Persistence layer
- Service layer
- REST controller endpoints
- Validation and error handling

Manual API testing was also performed using HTTP requests to verify endpoint behavior and error responses.
