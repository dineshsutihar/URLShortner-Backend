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

| Method | Endpoint                       | Auth?           | Description                                                     | Requires Data to Send                                 |
|--------|--------------------------------|-----------------|-----------------------------------------------------------------|------------------------------------------------------|
| POST   | `/api/url/public`              | No              | Create a public short URL.                                      | `originalUrl` (string)                               |
| POST   | `/api/url/private`             | Yes             | Create a private short URL (authenticated user).                | `originalUrl` (string), `customShortCode` (optional)  |
| DELETE | `/api/url/delete/{id}`         | Yes (Admin/User) | Delete a short URL by its ID.                                   | `id` (Long)                                           |
| PUT    | `/api/url/change-expired/{id}` | Yes (Admin/User) | Update the expiration time for a short URL.                    | `id` (Long), `newExpiration` (ISO 8601 date string)   |
| GET    | `/api/url/public/{shortCode}`  | No              | Expand a short URL to its original URL.                         | `shortCode` (string)                                 |
| GET    | `/api/user/profile`            | Yes             | Get the profile details of the authenticated user.              | None (Authorization header with JWT required)        |

---

## ⚙️ Project Structure

```
src/main/java/me/dineshsutihar/urlshortener/
├── config/
│   ├── SecurityConfig.java
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
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
├── service/
│   ├── AuthService.java
│   ├── UrlService.java
│   └── UserService.java
└── UrlShortenerApplication.java
```

---

### Breakdown:

1. **Controller (`UrlController.java` and `UserController.java`)**: Handles the HTTP routes for URL shortening, expanding, deletion, etc.
2. **Model (`ShortUrl.java` and `User.java`)**: Defines the data structures for short URLs and users.
3. **Repository (`ShortUrlRepository.java` and `UserRepository.java`)**: Interfaces for interacting with the PostgreSQL database to save and fetch URL and user data.
4. **Security (`JwtTokenProvider.java`)**: Handles JWT token generation and validation for user authentication.
5. **Service (`UserService.java`)**: Contains business logic related to user authentication and profile management.
6. **Test (`/test` folder)**: will contain unit tests for various parts of the application.

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
#Default Basic Properties 
spring.application.name=urlshortner
server.port=3030

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