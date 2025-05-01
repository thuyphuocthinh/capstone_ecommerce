# 🛒 Capstone Project - Ecommerce Platform

A full-featured e-commerce platform built with **Java Spring Boot**, designed to support multi-role users (Customer, Shop Owner, Admin) with real-time features, security, and performance optimization.

---

## 🚀 Tech Stack

### 🧠 Backend: Spring Boot
- **Spring Web** – RESTful API development  
- **Spring Security + JWT** – Secure user authentication & authorization  
- **Spring JPA (Hibernate)** – ORM for database operations  
- **Spring Mail** – Email service integration (e.g., for verification, order notifications)  
- **Spring OAuth2** – Social login support (Google, etc.)  
- **STOMP over WebSocket** – Real-time communication (comments, notifications)  
- **Redis (Cache + Stream)** – Performance caching & event streaming  
- **Full-text search** – MySQL full text search

### 🗃️ Database
- **MySQL** – Relational database for structured data  

---

## 🎯 Features

### 👤 Customer
- Sign up / Login (Email & Social login)
- Browse & search products
- Add to cart & checkout
- Order management & status tracking
- Real-time comments with shop
- Email verification & notifications

### 🏬 Shop Dashboard
- Product management (CRUD)
- Order management
- Revenue statistics
- Real-time comments with customers
- Discount management

### 🛠️ Admin Dashboard
- User & shop management
- Category & brand management
- Discount management

---

## 🛠️ Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8+
- Redis

### 🛠️ Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-username/ecommerce-springboot.git

# 2. Navigate into the project
cd ecommerce-springboot

# 3. Set up environment variables (you can put this in a .env file or export manually)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=ecommerce
export DB_USERNAME=root
export DB_PASSWORD=yourpassword

export REDIS_HOST=localhost
export REDIS_PORT=6379

export JWT_SECRET=your_jwt_secret

# Optional: Mail config (for Spring Mail)
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_email_password

# 4. Run the application
./mvnw spring-boot:run

