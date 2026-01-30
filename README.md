# 🚀 DSA Training System Demo Backend

<div align="center">

<!-- TODO: Add project logo if available -->

[![GitHub stars](https://img.shields.io/github/stars/ngleanhvu/dsa_training_system_demo?style=for-the-badge)](https://github.com/ngleanhvu/dsa_training_system_demo/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/ngleanhvu/dsa_training_system_demo?style=for-the-badge)](https://github.com/ngleanhvu/dsa_training_system_demo/network)
[![GitHub issues](https://img.shields.io/github/issues/ngleanhvu/dsa_training_system_demo?style=for-the-badge)](https://github.com/ngleanhvu/dsa_training_system_demo/issues)
[![GitHub license](https://img.shields.io/github/license/ngleanhvu/dsa_training_system_demo?style=for-the-badge)](LICENSE)

**A robust backend service for a Data Structures & Algorithms training and evaluation system.**

<!-- TODO: Add live demo link if a frontend exists and is deployed alongside this backend -->
<!-- [Live Demo](https://demo-link.com) | -->
<!-- TODO: Add documentation link if external API docs are available (e.g., Swagger UI) -->
<!-- [API Documentation](https://docs-link.com) -->

</div>

## 📖 Overview

This repository hosts the backend service for a Data Structures and Algorithms (DSA) training system. It provides core functionalities for user management, problem definition, code submission, automated solution evaluation, and tracking user progress. Built with Spring Boot, this service offers a scalable and secure foundation for an interactive learning platform.

## ✨ Features

-   **🔐 User Authentication & Authorization**: Secure user registration, login, and role-based access control using JWT.
-   **📚 DSA Problem Management**: CRUD operations for defining, updating, and retrieving diverse DSA problems with descriptions, example test cases, and difficulty levels.
-   **💻 Code Submission & Evaluation**: API for users to submit code solutions which are then evaluated against a comprehensive set of test cases.
-   **📊 Automated Testing**: Integration with a code execution environment (e.g., Piston) to run user-submitted code and provide immediate feedback.
-   **📈 Submission History & Progress Tracking**: Records and retrieves user submissions, results, and overall progress on problems.
-   **⚙️ Robust & Scalable Architecture**: Designed with Spring Boot best practices for maintainability and performance.

## 🛠️ Tech Stack

**Backend:**
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON-web-tokens&logoColor=white)

**Database:**
<!-- TODO: Detect specific database from pom.xml or application.properties/yml -->
![Database Placeholder](https://img.shields.io/badge/Database-SQL_or_NoSQL-blue?style=for-the-badge)

**Code Execution Environment:**
![Piston](https://img.shields.io/badge/Piston-Code_Execution-orange?style=for-the-badge)

## 🚀 Quick Start

Follow these steps to get the DSA Training System Backend up and running on your local machine.

### Prerequisites
-   **Java Development Kit (JDK)**: Version 17 or higher.
-   **Apache Maven**: Version 3.8.x or higher.
-   **Database**: A running instance of the chosen database (e.g., PostgreSQL, MySQL, or an in-memory database like H2 for development).
-   **Code Execution Engine**: An instance of a Piston-compatible code execution engine (e.g., [Piston](https://github.com/piston-bot/piston-API) running locally or remotely).

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/ngleanhvu/dsa_training_system_demo.git
    cd dsa_training_system_demo
    ```

2.  **Build the project**
    This will compile the Java code and package it into a JAR file.
    ```bash
    ./mvnw clean install
    ```

3.  **Environment setup**
    Create a `.env` file for local development by copying `local.env`.
    ```bash
    cp local.env .env
    ```
    Open `.env` and configure your environment variables:

    ```ini
    # Database Configuration
    SPRING_DATASOURCE_URL=jdbc:h2:mem:dsa_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE # Example for H2
    SPRING_DATASOURCE_USERNAME=sa
    SPRING_DATASOURCE_PASSWORD=
    SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
    # Alternatively for PostgreSQL:
    # SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dsa_training
    # SPRING_DATASOURCE_USERNAME=youruser
    # SPRING_DATASOURCE_PASSWORD=yourpassword
    # SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

    # JWT Security Keys
    # Path to the private key for signing JWTs
    JWT_PRIVATE_KEY_PATH=classpath:private-key.pem # Or a direct base64 encoded key
    # Path to the public key for verifying JWTs
    JWT_PUBLIC_KEY_PATH=classpath:public-key.pem   # Or a direct base64 encoded key

    # Piston Code Execution Engine Configuration
    PISTON_API_BASE_URL=http://localhost:2000/api/v2 # URL of your Piston instance
    ```
    **Note**: The `private-key.pem` and `public-key.pem` files should contain actual generated RSA keys. These files are currently placeholders with size 1 byte in the repository content, indicating they need to be properly generated.

4.  **Database setup**
    Spring Boot will typically initialize an in-memory H2 database automatically on startup if configured. For a persistent database (e.g., PostgreSQL), ensure your database is running and accessible. Spring Data JPA might create schemas automatically (depending on `spring.jpa.hibernate.ddl-auto` setting in `application.properties`/`application.yml`).

5.  **Start the development server**
    ```bash
    ./mvnw spring-boot:run
    ```

6.  **Access the API**
    The backend service will be running on `http://localhost:[detected-port, typically 8080]`.
    <!-- TODO: Confirm default port from application.properties/yml -->

## 📁 Project Structure

```
dsa_training_system_demo/
├── .mvn/                     # Maven Wrapper files
├── data/                     # Placeholder for data files or samples
├── infra/                    # Infrastructure-as-code or deployment configurations
├── src/
│   ├── main/
│   │   ├── java/             # Main Java source code
│   │   │   └── com/dsa/training/system/demo/
│   │   │       ├── DsaTrainingSystemDemoApplication.java # Main Spring Boot application
│   │   │       ├── controller/   # REST API endpoints
│   │   │       ├── service/      # Business logic
│   │   │       ├── repository/   # Data access layer (JPA/Hibernate)
│   │   │       ├── model/        # Domain entities/data models
│   │   │       ├── config/       # Spring configuration classes
│   │   │       └── security/     # JWT authentication and authorization setup
│   │   └── resources/          # Configuration files, static assets
│   │       ├── application.properties # Spring Boot application properties
│   │       └── (private-key.pem, public-key.pem - actual keys should be here or outside repo)
│   └── test/
│       └── java/             # Test source code
└── local.env                 # Environment variables for local development
├── mvnw                      # Maven Wrapper script (Linux/macOS)
├── mvnw.cmd                  # Maven Wrapper script (Windows)
├── piston                    # Placeholder, possibly for Piston client or config
├── pom.xml                   # Maven Project Object Model (project dependencies and build config)
├── private-key.pem           # JWT Private Key (placeholder, should be generated/secured)
└── public-key.pem            # JWT Public Key (placeholder, should be generated/secured)
```

## ⚙️ Configuration

### Environment Variables
The `local.env` file (which you should copy to `.env`) is used to configure the application.

| Variable                  | Description                                            | Default (H2)                        | Required |
|---------------------------|--------------------------------------------------------|-------------------------------------|----------|
| `SPRING_DATASOURCE_URL`   | JDBC URL for the database connection.                  | `jdbc:h2:mem:dsa_db`                | Yes      |
| `SPRING_DATASOURCE_USERNAME` | Username for database access.                        | `sa`                                | Yes      |
| `SPRING_DATASOURCE_PASSWORD` | Password for database access.                        | (empty)                             | No       |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | JDBC driver class name.                    | `org.h2.Driver`                     | Yes      |
| `JWT_PRIVATE_KEY_PATH`    | Path to the file containing the private key for JWT signing (e.g., `classpath:private-key.pem`). | `classpath:private-key.pem`         | Yes      |
| `JWT_PUBLIC_KEY_PATH`     | Path to the file containing the public key for JWT verification (e.g., `classpath:public-key.pem`).  | `classpath:public-key.pem`          | Yes      |
| `PISTON_API_BASE_URL`     | Base URL for the Piston code execution API.            | `http://localhost:2000/api/v2`      | Yes      |

### Configuration Files
-   `pom.xml`: Defines project dependencies, build plugins, and project metadata.
-   `src/main/resources/application.properties` (or `application.yml`): Main Spring Boot configuration file, overriding or complementing environment variables.

## 🔧 Development

### Available Scripts
| Command             | Description                                          |
|---------------------|------------------------------------------------------|
| `./mvnw clean`      | Cleans the build directory.                          |
| `./mvnw install`    | Compiles, tests, and packages the project.           |
| `./mvnw spring-boot:run` | Runs the Spring Boot application in development mode.|
| `./mvnw test`       | Runs all unit and integration tests.                 |

### Development Workflow
1.  Ensure all prerequisites are installed.
2.  Clone the repository and set up environment variables as described in [Quick Start](#🚀-quick-start).
3.  Start the Spring Boot application using `./mvnw spring-boot:run`.
4.  Develop API endpoints, services, and models. The application will typically auto-restart on code changes if `spring-boot-devtools` is enabled (check `pom.xml`).
5.  Use a REST client (like Postman or Insomnia) to test API endpoints.

## 🧪 Testing

The project uses JUnit and Spring Boot Test for unit and integration testing.

```bash
# Run all tests
./mvnw test

# Run tests with a specific profile (if defined in pom.xml)
# ./mvnw test -P test-profile

# Skip tests during build
./mvnw install -DskipTests
```

## 🚀 Deployment

### Production Build
To create a production-ready executable JAR file:
```bash
./mvnw clean package
```
This will generate `dsa_training_system_demo-[version].jar` in the `target/` directory.

### Deployment Options
-   **Direct Execution**: The JAR can be run directly using `java -jar target/dsa_training_system_demo-[version].jar`. Ensure all environment variables are properly set in the production environment.
-   **Docker**: The `infra` directory might contain `Dockerfile` or `docker-compose.yml` for containerized deployment.
-   **Cloud Platforms**: Deployable to platforms like AWS Elastic Beanstalk, Google Cloud Run, Azure App Service, or any server capable of running Java applications.

## 📚 API Reference

The API provides various endpoints for interacting with the DSA training system.

### Authentication
All protected endpoints require a JSON Web Token (JWT) in the `Authorization` header, formatted as `Bearer <token>`.
-   **`/auth/login`**: Authenticate user and receive a JWT.
-   **`/auth/register`**: Register a new user.

### Endpoints
*(Example endpoints based on inferred features)*

#### User Management
-   `POST /api/users/register`: Register a new user.
-   `POST /api/users/login`: Authenticate and get JWT.
-   `GET /api/users/{id}`: Retrieve user details (requires authentication).

#### Problem Management
-   `GET /api/problems`: Get all DSA problems (paginated/filtered).
-   `GET /api/problems/{id}`: Get a specific DSA problem by ID.
-   `POST /api/problems`: Create a new DSA problem (requires admin/moderator role).
-   `PUT /api/problems/{id}`: Update an existing DSA problem (requires admin/moderator role).
-   `DELETE /api/problems/{id}`: Delete a DSA problem (requires admin/moderator role).

#### Submission & Evaluation
-   `POST /api/submissions`: Submit code for a specific problem.
    -   Body: `{ "problemId": "...", "language": "...", "code": "..." }`
-   `GET /api/submissions/{id}`: Get details of a specific submission.
-   `GET /api/users/{userId}/submissions`: Get all submissions for a user.

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.
<!-- TODO: Create a CONTRIBUTING.md file -->

### Development Setup for Contributors
The development setup is as described in the [Quick Start](#🚀-quick-start) section. Ensure you have the JDK, Maven, a database, and the Piston execution engine running locally.

## 📄 License

This project is licensed under the [LICENSE_NAME](LICENSE) - see the LICENSE file for details.
<!-- TODO: Add a LICENSE file and specify the license name -->

## 🙏 Acknowledgments

-   **Spring Boot**: For simplifying Java application development.
-   **Piston**: For providing a robust code execution API.
-   **JWT Libraries**: For secure token-based authentication.

## 📞 Support & Contact

-   📧 Email: [contact@example.com] <!-- TODO: Add actual contact email -->
-   🐛 Issues: [GitHub Issues](https://github.com/ngleanhvu/dsa_training_system_demo/issues)
-   💬 Discussions: [GitHub Discussions](https://github.com/ngleanhvu/dsa_training_system_demo/discussions) <!-- TODO: Enable GitHub Discussions if desired -->

---

<div align="center">

**⭐ Star this repo if you find it helpful!**

Made with ❤️ by [ngleanhvu]

</div>
