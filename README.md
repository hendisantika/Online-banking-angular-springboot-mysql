# Online-banking-angular-springboot-mysql

Online-Bank-Simulator

Spring Boot 4.0 / Spring Data JPA / Spring Security / Hibernate / MySQL / Thymeleaf / REST

The project simulates an online banking system. It allows users to register/login, deposit/withdraw money from accounts, add/edit recipients, transfer money between accounts and recipients, view transactions, make appointments, and more.

There are two roles: **user** and **admin**.

## Features

### Core Banking Features
- User registration and authentication
- Primary and Savings accounts
- Deposit and withdraw money
- Transfer between own accounts
- Transfer to other recipients
- Transaction history

### New Features (v2.0)
- **Transaction Search & Filtering** - Search transactions by date range, type, and description
- **PDF Account Statements** - Download account statements as PDF for any date range
- **Daily Transaction Limits** - Configurable daily limits for withdrawals and transfers
- **Email Notifications** - Get notified for deposits, withdrawals, transfers, and low balance alerts
- **Password Reset** - Forgot password functionality with email reset link
- **Activity/Audit Logging** - Track all account activities for security
- **User Settings** - Configure daily limits and view activity logs

## Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| yu71     | 53cret   | USER |

## Requirements

- Java 21+
- MySQL 8.0+
- Maven 3.8+

## Quick Start

### Clone the repository
```bash
git clone https://github.com/hendisantika/Online-banking-angular-springboot-mysql.git
cd Online-banking-angular-springboot-mysql
```

### Configure MySQL

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/onlinebanking2?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

### Configure Email (Optional)

For email notifications to work, configure SMTP settings in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Run the application
```bash
mvn clean spring-boot:run
```

Access the application at [http://localhost:8080](http://localhost:8080)

## Screenshots

### Sign Up Page
![Sign Up Page](img/signup.png "Sign Up Page")

### Sign In Page
![Sign Up](img/login.png "Login Page")

### Dashboard Page
![Dashboard page](img/dashboard1.png "Dashboard Page")

### Deposit Page
![Deposit Page](img/deposit.png "Deposit Page")

### Dashboard Page
![Dashboard page](img/dashboard2.png "Dashboard Page")

## Tech Stack

- **Backend:** Spring Boot 4.0, Spring Security 6, Spring Data JPA
- **Database:** MySQL with Flyway migrations
- **Frontend:** Thymeleaf, Bootstrap, jQuery
- **PDF Generation:** OpenPDF
- **Email:** Spring Mail

## API Endpoints

### Public Endpoints
- `GET /` - Home page (redirects to login)
- `GET /signup` - Registration page
- `POST /signup` - Register new user
- `GET /password/forgot` - Forgot password page
- `POST /password/forgot` - Request password reset
- `GET /password/reset` - Reset password page
- `POST /password/reset` - Reset password

### User Endpoints (Authenticated)
- `GET /userFront` - User dashboard
- `GET /account/primaryAccount` - View primary account
- `GET /account/savingsAccount` - View savings account
- `POST /account/deposit` - Deposit money
- `POST /account/withdraw` - Withdraw money
- `GET /transfer/betweenAccounts` - Transfer between accounts
- `GET /transfer/recipient` - Manage recipients
- `GET /transfer/toSomeoneElse` - Transfer to recipient
- `GET /appointment/create` - Create appointment
- `GET /user/settings` - User settings
- `GET /user/activity` - Activity log
- `GET /statement/form` - Statement download form
- `GET /statement/download/primary` - Download primary account statement
- `GET /statement/download/savings` - Download savings account statement

### Admin Endpoints
- `GET /api/user/all` - List all users
- `GET /api/appointment/all` - List all appointments

## Deployment with Docker

### Start MySQL Docker Container
```bash
docker run --detach --name=bankmysql --env="MYSQL_ROOT_PASSWORD=root" -p 3306:3306 mysql:8
```

### Build Docker image
```bash
docker build -t hendisantika/online-banking:latest .
```

### Run Docker container
```bash
docker run --detach -p 8080:8080 --link bankmysql:localhost -t hendisantika/online-banking:latest
```

## Deployment without Docker

### Build application
```bash
mvn clean package -DskipTests
```

### Run application
```bash
java -jar target/online-banking-0.0.1-SNAPSHOT.jar
```

## Project Structure

```
src/main/java/com/hendisantika/onlinebanking/
├── config/          # Configuration classes (Security, DataLoader)
├── controller/      # MVC Controllers
├── entity/          # JPA Entities
├── repository/      # Spring Data Repositories
├── resource/        # REST Controllers
├── security/        # Security entities (Role, UserRole)
└── service/         # Service layer

src/main/resources/
├── db/migration/    # Flyway SQL migrations
├── static/          # Static assets (CSS, JS, images)
├── templates/       # Thymeleaf templates
└── application.properties
```

## License

This project is open source and available under the [MIT License](LICENSE).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
