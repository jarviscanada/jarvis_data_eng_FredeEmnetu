# Stock Quote App

A terminal-based stock trading application that allows users to look up real-time stock quotes, buy and sell shares, and manage their portfolio. The app integrates with the Alpha Vantage API to fetch live market data and persists all trading activity to a PostgreSQL database. Built with Java using JDBC for database connectivity, OkHttp for HTTP requests, Jackson for JSON deserialization, and Maven for dependency management and packaging.

# Quick Start

**Prerequisites**
- Docker installed and running
- `setup.sh` and `jdbc/sql/stockquote.sql` are included in the repo — no manual setup needed

**1. Start the PostgreSQL container**

```bash
bash setup.sh create db_user db_password
```

This pulls the PostgreSQL image, creates a `jrvs-psql` Docker container, and automatically runs `stockquote.sql` to initialize the `stock_quote` database and tables. To start/stop an existing container:

```bash
bash setup.sh start
bash setup.sh stop
```

**2. Run the application**

```bash
docker run --rm -it --network host \
  -e HOST=localhost \
  -e PORT=5432 \
  -e DATABASE=stock_quote \
  -e DBUSER=postgres \
  -e DBPASSWORD=password \
  -e APIKEY=your_api_key \
  --name jrvs-stockquote \
  fredeemnetu/stockquote:latest
```

# Implementation

## ER Diagram

```
+------------------+          +------------------+
|      quote       |          |     position     |
+------------------+          +------------------+
| symbol (PK)      |<---------| symbol (PK, FK)  |
| open             |          | number_of_shares |
| high             |          | value_paid       |
| low              |          +------------------+
| price            |
| volume           |
| latest_trading_day|
| previous_close   |
| change           |
| change_percent   |
| timestamp        |
+------------------+
```

## Design Patterns

The application uses the **DAO (Data Access Object)** pattern to separate database interaction logic from business logic. Each entity — `Quote` and `Position` — has its own DAO class (`QuoteDao`, `PositionDao`) responsible for all CRUD operations against the database. This keeps the service layer clean and focused purely on business rules, with no SQL leaking into it.

The DAO pattern is closely related to the **Repository** pattern. While both abstract data access, they differ in intent: DAO is more low-level and maps directly to database tables and operations, whereas Repository sits at a higher level of abstraction and operates in terms of domain objects and collections. In this app, the DAO pattern was chosen because each class maps directly to a single table (`quote`, `position`) and the operations are straightforward CRUD — making the lightweight DAO approach a natural fit.

Together, these patterns make the codebase modular and testable. The service layer (`QuoteService`, `PositionService`) depends on DAO interfaces rather than concrete implementations, which allows dependencies to be mocked during unit testing without requiring a live database connection.

# Test

The application was tested at two levels. Unit tests were written using **JUnit 4** and **Mockito** to validate business logic in the service layer in isolation. DAO dependencies were mocked using `Mockito.mock()` and stubbed with `when(...).thenReturn(...)` to simulate database responses without a live connection, allowing edge cases and error conditions to be tested reliably.

Integration tests were written against a live PostgreSQL instance running in a Docker container (`jrvs-psql`). The database was initialized using `stockquote.sql`, which creates the `quote` and `position` tables. Test data was inserted directly through the DAO layer in `@BeforeClass` setup methods, and results were validated by querying the database and asserting expected state. Database connection parameters were passed in via environment variables at runtime.

To run the app:

```bash
docker run --rm -it --network host \
  -e HOST=localhost \
  -e PORT=5432 \
  -e DATABASE=stock_quote \
  -e DBUSER=postgres \
  -e DBPASSWORD=password \
  -e APIKEY=your_api_key \
  --name jrvs-stockquote \
  fredeemnetu/stockquote:latest
```