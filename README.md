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

{
  "shortCode": "abc123"
}
```

---

### 2. Redirect to original URL

```http
GET /{shortCode}
```

Returns a `302 Found` with a `Location` header to redirect to the original URL.

---

### 3. Get click count for a short code

```http
GET /analytics/{shortCode}
```

**Response**
```json
{
  "shortCode": "abc123",
  "clicks": 42
}
```


---

## 📊 Database Design

**Table: `url_mapping`**

| Column       | Type      | Notes                  |
|--------------|-----------|------------------------|
| id           | BIGINT    | Primary Key            |
| short_code   | VARCHAR   | Unique, indexed        |
| original_url | TEXT      |                        |
| click_count  | INTEGER   | Tracks usage           |
| created_at   | TIMESTAMP | Defaults to `now()`    |

---

## 🧠 Design Highlights

- **Caching**: URLs are cached in Redis to avoid hitting the DB repeatedly
- **Persistence**: All mappings and click counts are stored in PostgreSQL
- **Exception Handling**: Custom `UrlNotFoundException` for unknown short codes
- **Scalability**: Foundation for future features like custom aliases, expiration, and analytics

---

## ✅ Running Locally

### Prerequisites
- Java 17+
- PostgreSQL running locally (or via Docker)
- Redis (preferably via Docker)

### Start Redis (Docker)
```bash
docker run -d -p 6379:6379 --name redis redis
```

### Run the App
```bash
./mvnw spring-boot:run
```

---

## 📈 Future Enhancements
- Expiring short URLs
- Per-user short links with auth
- Geo/IP-based analytics
- Rate limiting & abuse protection
- Frontend UI

---

## 🧑‍💻 Author

Built as a hands-on learning project to strengthen backend system design and real-time performance handling.

