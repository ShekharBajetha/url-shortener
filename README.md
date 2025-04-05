# 🔗 URL Shortener

A minimal yet efficient URL shortening service built with **Spring Boot**, **PostgreSQL**, and **Redis**. It mimics the functionality of services like Bit.ly, with support for redirection, caching, and click tracking.

---

## 🚀 Features

- ✅ Generate a unique short URL for any long URL
- ✅ Redirect to the original URL using the short code
- ✅ Cache short-to-original URL mappings in Redis (1-hour TTL)
- ✅ Track how many times each short URL is clicked
- ✅ RESTful API design with proper exception handling
- ✅ Built for extensibility: supports analytics, expiration, rate-limiting, etc.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Redis**
- **Docker** (for Redis)
- **pgAdmin** (for DB exploration)

---

## 📦 Endpoints

### 1. Create a short URL

```http
POST /api/shorten
Content-Type: application/json

{
  "originalUrl": "https://github.com"
}
