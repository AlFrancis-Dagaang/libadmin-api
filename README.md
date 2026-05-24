# LibAdmin API

A traditional library management system built with **Java Servlets** for managing books, members, borrowing transactions, book returns, and payment handling.

## Project Overview

`LibAdmin API` is a servlet-based library management application that exposes JSON endpoints for librarian workflows. The codebase is organized into controller, service, DAO, model, configuration, utility, and filter layers. Authentication is session-based, and every protected route is guarded by an auth filter.

## Features

- Admin login and logout using HTTP session authentication
- Book CRUD operations
- Book filtering by type, availability, and whether price is null
- Member CRUD operations
- Member filtering by type
- View a member's borrowing transactions
- Create book issue transactions
- Complete and cancel book agreements for book bank flows
- Process book returns
- Create payment transactions for return bills
- Retrieve transaction details and agreement details
- JSON request/response handling with LocalDate support

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java Servlets (Jakarta Servlet 6.1) |
| Build Tool | Maven |
| Database Access | JDBC |
| Database | MySQL |
| Servlet Container | Apache Tomcat |
| JSON Library | Gson |
| ORM Helper | ModelMapper |
| Testing | JUnit 5, JUnit 3, TestNG |

## Architecture Overview

The project follows a simple layered servlet architecture:

1. **Controller layer** (`controller`) receives HTTP requests and writes JSON responses.
2. **Service layer** (`service`) contains business rules such as book limits, issue/return logic, and payment handling.
3. **DAO layer** (`dao`) performs database operations through JDBC.
4. **Model / DTO layer** (`model`) defines request and response data structures.
5. **Configuration layer** (`config`) wires services and application rules.
6. **Utility layer** (`util`) provides JSON parsing, path handling, database connectivity, and lending/penalty calculations.
7. **Filter layer** (`filter`) protects all non-auth routes using session-based authorization.

### Request Flow

Client -> Servlet -> Service -> DAO -> MySQL -> DAO -> Service -> Servlet -> JSON Response

## Authentication

- Login endpoint: `POST /v1/lms/auth/login`
- Logout endpoint: `POST /v1/lms/auth/logout`
- All routes under `/v1/lms/*` are protected by `AuthFilter` except `/v1/lms/auth/*`
- A valid session must contain the `admin` attribute

## API Endpoints

### Authentication

| Method | Path | Description | Auth Required |
| --- | --- | --- | --- |
| POST | `/v1/lms/auth/login` | Logs in an admin and stores the admin object in the HTTP session | No |
| POST | `/v1/lms/auth/logout` | Invalidates the current session | No |

### Books

| Method | Path | Description | Auth Required |
| --- | --- | --- | --- |
| GET | `/v1/lms/books` | Returns all books | Yes |
| POST | `/v1/lms/books` | Creates a new book | Yes |
| GET | `/v1/lms/books/{id}` | Returns a single book by ID | Yes |
| PUT | `/v1/lms/books/{id}` | Updates a book by ID | Yes |
| DELETE | `/v1/lms/books/{id}` | Deletes a book by ID | Yes |
| GET | `/v1/lms/books/filter?type={type}` | Filters books by type | Yes |
| GET | `/v1/lms/books/filter?isAvailable={boolean}` | Filters books by availability | Yes |
| GET | `/v1/lms/books/filter?isPriceIsNull={boolean}` | Filters books by whether price is null | Yes |

### Members

| Method | Path | Description | Auth Required |
| --- | --- | --- | --- |
| GET | `/v1/lms/members` | Returns all members | Yes |
| POST | `/v1/lms/members` | Creates a new member | Yes |
| GET | `/v1/lms/members/{id}` | Returns a single member by ID | Yes |
| PUT | `/v1/lms/members/{id}` | Updates a member by ID | Yes |
| DELETE | `/v1/lms/members/{id}` | Deletes a member by ID | Yes |
| GET | `/v1/lms/members/filter?type={type}` | Filters members by type | Yes |
| GET | `/v1/lms/members/{id}/book-transactions` | Returns all transactions for a member | Yes |

### Book Transactions

| Method | Path | Description | Auth Required |
| --- | --- | --- | --- |
| GET | `/v1/lms/book-transactions` | Returns all book transactions | Yes |
| POST | `/v1/lms/book-transactions` | Creates a new book issue transaction | Yes |
| GET | `/v1/lms/book-transactions/{id}` | Returns a transaction by ID | Yes |
| GET | `/v1/lms/book-transactions/{id}/details` | Returns transaction details without agreement data | Yes |
| GET | `/v1/lms/book-transactions/{id}/details/agreement` | Returns transaction details with agreement data | Yes |
| GET | `/v1/lms/book-transactions/{id}/agreement` | Returns the agreement linked to a transaction | Yes |
| POST | `/v1/lms/book-transactions/{id}/complete-transaction` | Completes a book agreement transaction | Yes |
| POST | `/v1/lms/book-transactions/{id}/cancel-agreement` | Cancels a book agreement transaction | Yes |
| POST | `/v1/lms/book-transactions/return` | Processes a book return | Yes |
| GET | `/v1/lms/book-transactions/bill/{billId}/payment-transactions` | Returns all payment transactions for a bill | Yes |
| POST | `/v1/lms/book-transactions/bill/payment-transaction` | Creates a payment transaction for a bill | Yes |

## Request and Response Format

All successful responses are written through the shared response wrapper:

```json
{
  "status": 200,
  "message": "Success",
  "data": {}
}
```

Errors use the error wrapper:

```json
{
  "message": "Unauthorized",
  "code": 401
}
```

### Date Format

`LocalDate` values are serialized as `MMMM dd, yyyy`.
Example: `May 24, 2026`

## Sample Requests and Responses

### 1) Login

#### Request

```json
{
  "username": "admin",
  "password": "admin123"
}
```

#### Response

```json
{
  "status": 200,
  "message": "Login successful",
  "data": "admin"
}
```

### 2) Create Book

#### Request

```json
{
  "author": "Robert C. Martin",
  "title": "Clean Code",
  "price": 599.00,
  "yearOfPublication": 2008,
  "isAvailable": true,
  "type": "Book Bank"
}
```

#### Response

```json
{
  "status": 201,
  "message": "Successfully Created Book",
  "data": {
    "bookId": 101,
    "author": "Robert C. Martin",
    "title": "Clean Code",
    "price": 599.0,
    "yearOfPublication": 2008,
    "isAvailable": true,
    "type": "Book Bank"
  }
}
```

### 3) Create Member

#### Request

```json
{
  "name": "Jane Doe",
  "address": "Quezon City",
  "phoneNumber": 9123456789,
  "type": "Student"
}
```

#### Response

```json
{
  "status": 201,
  "message": "",
  "data": {
    "memberId": 12,
    "type": "Student",
    "dateOfMembership": "May 24, 2026",
    "numberOfBookIssued": 0,
    "maxBookLimit": 10,
    "name": "Jane Doe",
    "address": "Quezon City",
    "phoneNumber": 9123456789
  }
}
```

### 4) Issue a Book

#### Request

```json
{
  "memberId": 12,
  "bookId": 101,
  "bookType": "Book Bank"
}
```

#### Response

For a **general book**, the service returns a `BookTransaction`.
For a **book bank** title, the service returns the created `BookAgreement`.

Example response for a book bank issue:

```json
{
  "status": 201,
  "message": "",
  "data": {
    "agreementId": 7,
    "transactionId": 3001,
    "bookPrice": 599.0,
    "serviceFee": 29.95,
    "totalAmount": 628.95,
    "agreementDate": "May 24, 2026",
    "active": false
  }
}
```

### 5) Process a Book Return

#### Request

```json
{
  "transactionId": 3001,
  "bookCondition": "Good",
  "librarianNotes": "Returned in good condition"
}
```

#### Response

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "returnStatusId": 8,
    "transactionId": 3001,
    "returnType": "BOOK_BANK",
    "returnDate": "May 24, 2026",
    "bookCondition": "Good",
    "returnTimeline": "On Time",
    "penaltyAmount": 0.0,
    "refundAmount": 599.0,
    "librarianNotes": "Returned in good condition"
  }
}
```

### 6) Create Payment Transaction

#### Request

```json
{
  "billId": 15,
  "amount": 100.00,
  "paymentMethod": "Cash"
}
```

#### Response

```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "paymentId": 21,
    "billId": 15,
    "transactionType": "PAYMENT",
    "amount": 100.0,
    "status": "SUCCESS",
    "paymentMethod": "Cash",
    "paymentDate": "May 24, 2026"
  }
}
```

## How to Run Locally

### Prerequisites

- Java 21 or a compatible JDK for your Tomcat runtime
- Maven
- MySQL
- Apache Tomcat 10.1+ or another Jakarta Servlet 6.1 compatible container
- IntelliJ IDEA or another Java IDE

### Database Setup

1. Create a MySQL database named `librarydb`.
2. Create the tables used by the application for:
   - admins
   - books
   - members
   - book transactions
   - book agreements
   - book return records
   - book return bills
   - payment transactions
3. Seed at least one admin account so login can succeed.
4. Update the values in `src/main/java/com/app/util/DBConnection.java` to match your local MySQL host, database, username, and password.

### Configuration Notes

- The application uses annotation-based servlet and filter mapping.
- `web.xml` is minimal and does not define the routes.
- Authentication is session-based, so the client must preserve the session cookie after login.

### Build and Deploy

1. Open the project in IntelliJ IDEA.
2. Build the WAR with Maven:

```bash
mvn clean package
```

3. Deploy the generated WAR from `target/` to Tomcat.
4. Call `POST /v1/lms/auth/login` first, then reuse the same session for protected endpoints.

## Future Improvements

- Externalize database credentials into environment variables or a properties file
- Hash admin passwords instead of storing plaintext credentials
- Add pagination, sorting, and better query filters
- Standardize error handling and response messages
- Add validation for request payloads
- Add OpenAPI/Swagger documentation
- Add automated unit and integration tests for controllers, services, and DAOs
- Add role-based access control if more user types are introduced
- Replace hardcoded business values with configurable policy rules

## Author

**Al Francis Dagaang**
