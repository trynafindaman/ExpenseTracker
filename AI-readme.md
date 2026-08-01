# AI_NOTES.md

## 1. AI Usage

This project was designed, implemented, and integrated by me. AI was used as a development assistant to speed up development by suggesting boilerplate code, implementation approaches, and alternative solutions.

AI assistance included:
- Suggesting an initial project structure (controller, service, store, model, DTO, and exception packages).
- Generating boilerplate code for common Spring Boot components such as DTOs, exception classes, and parts of the persistence layer.
- Suggesting REST endpoint structure, request/response models, validation annotations, and unit test scaffolding.
- Providing explanations and implementation ideas during debugging and development.

No AI-generated code was accepted without review. Every suggested implementation was evaluated, modified where necessary, compiled, tested, and integrated manually.

---

## 2. Manual Development and Validation

The following work was completed and verified manually:

- Designed and implemented the overall Expense Tracker REST API.
- Integrated all project components into a working Spring Boot application.
- Compiled and tested every milestone locally using `./mvnw test` before proceeding.
- Fixed a Spring Initializr package-naming issue (`com.expensetracker.expense_tracker` vs. `com.expensetracker`) that prevented `@SpringBootTest` from locating the application configuration.
- Diagnosed and resolved a Spring Boot 4 migration issue where `@AutoConfigureMockMvc` moved to a different module and package. Verified the solution using Spring's official migration documentation.
- Corrected incorrect Maven dependency artifact IDs after verifying the available artifacts on Maven Central.
- Fixed a misplaced JUnit test file (`ExpenseServiceTest.java`) that had been created under `src/main/java` instead of `src/test/java`.
- Removed a duplicate smoke test (`ApplicationTests.java`) during the final project review.
- Manually tested every REST endpoint using PowerShell (`Invoke-RestMethod`) in addition to automated tests.
- Verified the behavior of the global exception handler by intentionally triggering error scenarios before and after implementation.
- Resolved a GitHub push conflict caused by an auto-generated remote README after confirming that no project work would be overwritten.

---

## 3. AI Suggestions That Were Modified or Rejected

Several AI suggestions were intentionally changed based on design decisions:

- Chose to preserve the original case of expense categories while trimming whitespace instead of automatically normalizing values. Category filtering remains case-insensitive for convenience.
- Selected UUIDs instead of auto-incrementing integer IDs to avoid identifier recovery logic with file-based persistence.
- Used Java Records instead of Lombok to reduce dependencies while maintaining immutable data models.
- Declined to introduce a separate `ExpenseService` interface and implementation because the existing `ExpenseStore` abstraction already provides sufficient separation of concerns and testability.

---

## 4. Personal Contribution

This project represents my own implementation of the assignment.

My responsibilities included:
- Designing the application architecture.
- Implementing and integrating all application components.
- Making architectural and design decisions.
- Debugging framework, dependency, and configuration issues.
- Writing, running, and validating automated tests.
- Performing manual API testing.
- Reviewing every AI-generated suggestion before deciding whether to accept, modify, or reject it.

AI served as a development assistant throughout the project, while all final implementation decisions, debugging, validation, and integration were performed by me.
