# APIBP001 — University Management API

Production-grade dual API (REST + GraphQL) for university management. **Team ID:** APIBP001 | **Approach:** A (REST-First + GraphQL Layer)

---

## Team

| # | Name | Roll No | GitHub ID | Email |
|---|------|---------|-----------|-------|
| 1 | Ansh Biraj | 2024SL70010 | [Ansh-Biraj](https://github.com/Ansh-Biraj) | 2024sl70010@wilp.bits-pilani.ac.in |
| 2 | Abhishek Patil | 2024SL70004 | [CheetahPatil](https://github.com/CheetahPatil) | 2024sl70004@wilp.bits-pilani.ac.in |
| 3 | Yash Shimpi | 2024SL70005 | [yash8shimpi](https://github.com/yash8shimpi) | 2024sl70005@wilp.bits-pilani.ac.in |
| 4 | Aburba Chakraborty | 2024SL70048 | [apurbaXD](https://github.com/apurbaXD) | 2024sl70048@wilp.bits-pilani.ac.in |

---

## Quick Start

### With Docker Compose (recommended)

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`. The database is seeded automatically with 40+ entities on first run.

### Without Docker

```bash
mvn spring-boot:run
```

---

## Demo Credentials

Username

Password

`demo`

`demo-password`

---

## Endpoints

Interface

URL

REST API

`http://localhost:8080/api/v1/`

Swagger UI

`http://localhost:8080/docs`

GraphQL / GraphiQL

`http://localhost:8080/graphql`

---

## curl Examples

### 1. Authenticate (get JWT token)

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login 
  -H "Content-Type: application/json" 
  -d '{"username":"demo","password":"demo-password"}' 
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

### 2. List Students (cursor pagination)

```bash
curl -H "Authorization: Bearer $TOKEN" 
  "http://localhost:8080/api/v1/students?limit=5"
```

### 3. Next page using cursor

```bash
curl -H "Authorization: Bearer $TOKEN" 
  "http://localhost:8080/api/v1/students?limit=5&after=<nextCursor>"
```

### 4. Find nearby campus locations (GeoJSON / Map pattern)

```bash
curl -H "Authorization: Bearer $TOKEN" 
  "http://localhost:8080/api/v1/locations/nearby?latitude=40.7128&longitude=-74.0060&radius_km=1.0"
```

### 5. List schedules in a time window (Calendar pattern)

```bash
curl -H "Authorization: Bearer $TOKEN" 
  "http://localhost:8080/api/v1/schedules?start=2025-02-10T00:00:00&end=2025-02-17T23:59:59"
```

### 6. Create a schedule (triggers 409 on conflict)

```bash
curl -X POST -H "Authorization: Bearer $TOKEN" 
  -H "Content-Type: application/json" 
  "http://localhost:8080/api/v1/schedules" 
  -d '{"courseId":1,"locationId":1,"startTime":"2025-02-10T09:00:00","endTime":"2025-02-10T11:00:00"}'
```

### 7. GraphQL query

```bash
curl -X POST -H "Authorization: Bearer $TOKEN" 
  -H "Content-Type: application/json" 
  "http://localhost:8080/graphql" 
  -d '{"query":"{ students(limit:3) { data { id firstName lastName email } hasMore nextCursor } }"}'
```

---

## Rate Limiting

All endpoints are rate-limited to **100 requests/minute per IP**. Responses include:

-   `RateLimit-Limit` — max requests per window
-   `RateLimit-Remaining` — requests remaining
-   `RateLimit-Reset` — Unix timestamp when the window resets

HTTP `429 Too Many Requests` is returned when the limit is exceeded.

---

## Architecture

See [`docs/ADR.md`](docs/ADR.md) for full architecture decision records.

Key decisions:

-   SQLite with haversine filtering in the service layer (no native geo extension needed)
-   Keyset/cursor pagination for stable, index-efficient list traversal
-   Interval-overlap conflict detection for schedule booking (→ HTTP 409)
-   Bucket4j in-memory token bucket for rate limiting
-   `@BatchMapping` in Spring GraphQL for N+1 prevention