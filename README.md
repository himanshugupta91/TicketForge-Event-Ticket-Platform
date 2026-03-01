<div align="center">
  
  # Event Ticket Platform

  **A secure, high-performance backend platform for managing event lifecycles and ticket distribution.**

</div>

---

## 🚀 About The Project

A robust Spring Boot backend designed to handle the complexities of event management and seat reservation. Whether you are an **Organizer** creating large-scale festivals or a **Staff Member** validating tickets at the venue, the platform provides a seamless, secure, and atomic experience.

### Why This Project?
- **High Concurrency**: Uses pessimistic locking to ensure tickets are never oversold.
- **Enterprise Security**: Integrated with Keycloak for robust JWT-based OIDC authentication.
- **Developer Friendly**: Clean architecture, automated DTO mapping with MapStruct, and comprehensive API documentation.

---

## ✨ Features

- **📅 Event Lifecycle Management**: Complete control for organizers to create, publish, and manage events.
- **🔍 Intelligent Discovery**: Public API for searching and filtering published events with PostgreSQL full-text search.
- **🎫 Atomic Ticket Purchase**: High-integrity ticketing system with pessimistic locking to prevent overselling.
- **🖼️ QR Code Integration**: Automatic generation and retrieval of QR codes for secure venue entry.
- **✅ Multi-mode Validation**: Support for both manual ID entry and high-speed QR code scanning.
- **🔐 Enterprise Security**: Integrated user provisioning and JWT validation via Keycloak.

---

## 🛠 Tech Stack

### 🚀 Core Backend
- **Java 21**: Leveraging the latest LTS features.
- **Spring Boot 4.0.2**: The foundation of our micro-framework.
- **Spring Data JPA**: Efficient database access with Hibernate.
- **PostgreSQL**: Reliable relational data storage.

### 🛡 Security & Auth
- **Spring Security**: Robust resource server configuration.
- **Keycloak**: Leading open-source Identity and Access Management.
- **OAuth2 / JWT**: Standardized token-based authentication.

### 🧰 Utilities & Tools
- **MapStruct**: High-performance, type-safe bean mapping.
- **Lombok**: Reduced boilerplate for cleaner code.
- **ZXing**: Trusted library for barcode and QR code processing.
- **Docker**: Containerized environment for Postgres, Adminer, and Keycloak.

---

## 🚦 Getting Started

### 📋 Prerequisites
- **Java 21** (OpenJDK recommended)
- **Docker & Docker Compose**
- **Maven** (or used bundled `./mvnw`)

### 🛠️ Setup Steps

1. **Clone & Compile**:
   ```bash
   ./mvnw -DskipTests clean compile
   ```

2. **Launch Dependencies**:
   ```bash
   docker compose up -d
   ```
   *Starts Postgres (5432), Adminer (8888), and Keycloak (9090).*

3. **Configure Keycloak**:
   - Create realm: `event-ticket-platform`
   - Create roles: `ROLE_ORGANIZER`, `ROLE_STAFF`
   - Ensure tokens include `sub`, `preferred_username`, and `email`.

4. **Run Application**:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Access the API at `http://localhost:8080`*

### 🔍 Troubleshooting

- **Database Connection Error**: Ensure docker containers are healthy. Run `docker ps` to verify.
- **Unauthorized (401)**: Check if the JWT has expired or if the `issuer-uri` in `application.properties` matches your Keycloak setup.
- **Empty Authorities**: Ensure your Keycloak roles have the `ROLE_` prefix.

---

<div align="center">
  <sub>Built with ❤️</sub>
</div>
