# ✨ URL Shortener API - Spring Boot + PostgreSQL + JWT

A **URL shortening service** built with **Spring Boot**, **PostgreSQL (NeonDB)**, and **JWT authentication**.  
Users can **register, login, create short links (with optional custom names)**, **manage their links**, and **control link expiry**.  
Anonymous (unauthenticated) users can also create public short links!

---

## 📚 Features

- **Public Short Link Creation** (No login required)
- **Authenticated User Short Link Creation**
- **Custom Short Codes** (User can specify their own)
- **Register / Login / Logout**
- **JWT Authentication with HttpOnly Cookies**
- **Session Management**
- **CRUD for Short Links:**
  - Create new short link
  - Delete specific short link
  - Update `expiredAt` timestamp
- **Auto-expiration of URLs**
- **User Profile API** (User details + created short links)
- **Secure password hashing**
- **PostgreSQL (NeonDB) as database provider**
- **Proper HTTP status codes and error handling**
- **Clean, modular code structure**

---

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot 3.x
- **Database**: PostgreSQL 17 (NeonDB)
- **Authentication**: JWT + HttpOnly Cookies
- **Security**: Spring Security 6
- **ORM**: Hibernate + JPA
- **Build Tool**: Maven

---

## 🗂️ API Endpoints

| Method | Endpoint                     | Auth? | Description |
|:------:|:-----------------------------|:-----:|:------------|
| POST   | `/auth/register`              | ❌ | Register a new user |
| POST   | `/auth/login`                 | ❌ | Login user, set JWT cookie |
| GET    | `/auth/logout`                | ✅ | Logout user, clear session |
| POST   | `/shorten`                    | ❌ | Public create short URL |
| POST   | `/user/shorten`                | ✅ | Authenticated create short URL |
| DELETE | `/user/url/{id}`               | ✅ | Delete specific short URL |
| PUT    | `/user/url/{id}/expire`        | ✅ | Update expiry date for a short URL |
| GET    | `/user/profile`                | ✅ | Get user profile + created URLs |
| GET    | `/{shortCode}`                 | ❌ | Redirect to original URL |

---

## ⚙️ Project Structure

```
src/main/java/me/dineshsutihar/urlshortener/
├── controller/
│   ├── AuthController.java
│   ├── UrlController.java
│   └── UserController.java
├── entity/
│   ├── User.java
│   └── ShortUrl.java
├── repository/
│   ├── UserRepository.java
│   └── ShortUrlRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   └── SecurityConfig.java
├── service/
│   ├── AuthService.java
│   ├── UrlService.java
│   └── UserService.java
└── UrlShortenerApplication.java
```

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git https://github.com/dineshsutihar/URLShortner-Backend.git
cd URLShortner-Backend
```

### 2. Configure application properties

Edit `src/main/resources/application.properties`:

```properties
# PostgreSQL Configuration - currently i am using NeonDb
spring.datasource.url=jdbc:postgresql://your-db-url/<database-name>
spring.datasource.username=<your-db-username>
spring.datasource.password=<your-db-password>

# JPA Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Secret
app.jwt.secret=secret-key-keep-it-secret
```

---

### 3. Run the application

```bash
./mvnw spring-boot:run
```
Or open in IDE(maybe Vscode, IntelliJ Idea) and **Run UrlShortenerApplication.java**.

---

## 🛡️ Security

- JWT tokens are stored in **HttpOnly Cookies** to prevent XSS attacks.
- Passwords are **hashed securely** before storing in database.
- Only authenticated users can perform **private** URL operations.
- Public URL creation allowed without authentication.

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

## 🤝 Contributions

Contributions, issues and feature requests are welcome!  
Feel free to check [issues page](https://github.com/dineshsutihar/URLShortner-Backend/issues).

---

> Built with ❤️ using Spring Boot and NeonDB
---