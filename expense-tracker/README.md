# Expense Tracker REST API

A comprehensive Spring Boot REST API for tracking personal expenses and income with user authentication, categorization, and reporting features.

## 🚀 Features

- **User Authentication**: Secure JWT-based authentication system
- **Transaction Management**: Full CRUD operations for expenses and income
- **Categorization**: Organize transactions by customizable categories
- **Advanced Filtering**: Filter transactions by date, category, and type
- **Summary Reports**: Monthly and annual financial summaries
- **Deployment Ready**: Docker support for easy deployment

## 🛠 Tech Stack

- **Spring Boot 3.2.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **JWT (JSON Web Tokens)** - Stateless authentication
- **H2 Database** - In-memory database for development
- **PostgreSQL** - Production database support
- **Maven** - Build and dependency management
- **Docker** - Containerization

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Docker (optional, for containerized deployment)

## 🚀 Quick Start

### 1. Clone the repository
```bash
git clone <repository-url>
cd expense-tracker
```

### 2. Run the application
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

### 3. Access the application
- **API Base URL**: `http://localhost:8080/api`
- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:expensedb`
  - Username: `sa`
  - Password: `password`

## 🔐 Authentication

### Sample User Account
A sample user is automatically created for testing:
- **Username**: `johndoe`
- **Password**: `password123`
- **Email**: `john.doe@example.com`

### Register New User
```bash
POST /api/auth/signup
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Login
```bash
POST /api/auth/signin
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

Response includes JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "johndoe",
  "email": "john.doe@example.com"
}
```

## 📚 API Endpoints

### Authentication Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/signin` | User login |
| GET | `/api/auth/me` | Get current user info |

### Transaction Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions` | Create new transaction |
| GET | `/api/transactions` | Get transactions with filters |
| GET | `/api/transactions/{id}` | Get specific transaction |
| PUT | `/api/transactions/{id}` | Update transaction |
| DELETE | `/api/transactions/{id}` | Delete transaction |
| GET | `/api/transactions/summary/monthly?year=2024&month=1` | Monthly summary |
| GET | `/api/transactions/summary/annual?year=2024` | Annual summary |

### Category Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/{id}` | Get specific category |
| POST | `/api/categories` | Create new category |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

## 💰 Transaction Examples

### Create Income Transaction
```bash
POST /api/transactions
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "type": "INCOME",
  "amount": 5000.00,
  "description": "Monthly Salary",
  "transactionDate": "2024-01-15",
  "categoryId": 9
}
```

### Create Expense Transaction
```bash
POST /api/transactions
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "type": "EXPENSE",
  "amount": 50.00,
  "description": "Grocery Shopping",
  "transactionDate": "2024-01-10",
  "categoryId": 1
}
```

### Filter Transactions
```bash
GET /api/transactions?type=EXPENSE&categoryId=1&startDate=2024-01-01&endDate=2024-01-31&page=0&size=10
Authorization: Bearer <your-jwt-token>
```

## 📊 Sample Data

The application comes with pre-loaded sample data including:

### Categories
- Food & Dining
- Transportation
- Shopping
- Entertainment
- Bills & Utilities
- Healthcare
- Education
- Travel
- Salary
- Freelance
- Investment
- Other Income/Expense

### Sample Transactions
- Monthly salary income
- Various expense transactions (food, transport, bills, etc.)

## 🐳 Docker Deployment

### Build Docker image
```bash
docker build -t expense-tracker .
```

### Run with Docker
```bash
docker run -p 8080:8080 expense-tracker
```

### Docker Compose (with PostgreSQL)
```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: expensedb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
  
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/expensedb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      - db
```

## 🔧 Configuration

### Development (H2 Database)
The application uses H2 in-memory database by default. Configuration is in `application.properties`.

### Production (PostgreSQL)
Uncomment PostgreSQL configuration in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expensedb
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## 🧪 Testing

### Run tests
```bash
./mvnw test
```

### Test with Postman/curl
Use the provided API endpoints with proper authentication headers.

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/example/expensetracker/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # Entity classes
│   │   ├── repository/     # Data access layer
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic
│   └── resources/
│       └── application.properties
└── test/                   # Test classes
```

## 🔒 Security Features

- JWT-based stateless authentication
- Password encryption using BCrypt
- Role-based access control
- CORS support for frontend integration
- Input validation on all endpoints

## 📈 Future Enhancements

- [ ] Email notifications for large expenses
- [ ] Budget tracking and alerts
- [ ] File upload for receipts
- [ ] Advanced analytics and charts
- [ ] Multi-currency support
- [ ] Recurring transactions
- [ ] Export to CSV/PDF

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

Created as part of a comprehensive Spring Boot learning project showcasing:
- RESTful API design
- Spring Security implementation
- JWT authentication
- JPA/Hibernate integration
- Clean architecture principles
- Docker containerization

Perfect for resume/portfolio demonstration of full-stack backend development skills.