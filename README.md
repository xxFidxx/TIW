# TIW – Online Auction Platform

A Jakarta EE web application that implements an online auction system, built as a university project at Politecnico di Milano.

## Features

- **User Authentication** – Register, log in, and log out with session-based security
- **Create Auctions** – Sellers can create auctions, attach items with images, set starting prices and minimum bid increments
- **Browse & Search** – Buyers can browse open auctions and search by keyword
- **Place Bids** – Submit offers on active auctions with real-time price updates
- **Close Auctions** – Sellers can close auctions and award winning bids
- **Image Upload** – Upload product images for auction items

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Web Framework | Jakarta Servlet 6.0 |
| Templating | Thymeleaf 3.1 |
| Database | MariaDB |
| Build Tool | Maven 3 |
| Packaging | WAR |

## Project Structure

```
src/main/java/it/polimi/tiw/
├── beans/          # Entity classes (User, Asta, Offerta, Articolo)
├── Dao/            # Data Access Objects for database operations
├── servlets/       # HTTP servlet controllers
├── filters/        # Authentication filter
└── rescources/     # Utility helpers

src/main/webapp/
├── templates/      # Thymeleaf HTML templates
├── css/            # Stylesheets
└── WEB-INF/        # Deployment descriptor and libraries
```

## Prerequisites

- **Java 21**
- **Maven 3**
- **MariaDB** running on `localhost:3306` with a database named `tiw`

## Getting Started

1. **Set up the database** – Create a MariaDB database named `tiw` and configure credentials in `src/main/webapp/WEB-INF/web.xml`.

2. **Build the project**
   ```bash
   ./mvnw clean package
   ```

3. **Deploy** – Deploy the generated WAR file from `target/` to a Jakarta EE-compatible servlet container (e.g. Apache Tomcat 10+).

## Architecture

The application follows a classic **3-tier MVC architecture**:

- **Presentation** – Thymeleaf templates rendered by servlet controllers
- **Business Logic** – Jakarta Servlets handling HTTP requests
- **Data Access** – DAO classes executing SQL queries against MariaDB

An `AuthenticationFilter` protects all routes except login and registration, redirecting unauthenticated users to the login page.
