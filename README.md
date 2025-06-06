# Library Management REST API

A RESTful service for managing a library system built with Java, Spring Boot, JPA, H2/PostgreSQL, and Spring Validation.

## Features

- CRUD operations for books and members
- Borrowing and returning books with amount tracking
- Validation rules for book and member data
- Business constraints:
    - A book cannot be deleted if borrowed
    - A member cannot borrow more than 10 books (limit is configurable)
    - A member cannot be deleted while holding books
- Endpoints to:
    - Retrieve all books borrowed by a member
    - Retrieve all distinct borrowed book titles
    - Retrieve all borrowed books with borrowed copy counts
- Swagger documentation
- Unit tests using JUnit and Mockito

## Run the application

```bash
./mvnw spring-boot:run
```
Access Swagger UI at: http://localhost:8080/swagger-ui/index.html