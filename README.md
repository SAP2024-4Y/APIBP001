# APIBP001 — University Management API

## Team Details

| # | Name               | Roll No     | GitHub ID                                       | Email                              |
|---|--------------------|-------------|-------------------------------------------------|------------------------------------|
| 1 | Ansh Biraj         | 2024SL70010 | [Ansh-Biraj](https://github.com/Ansh-Biraj)     | 2024sl70010@wilp.bits-pilani.ac.in |
| 2 | Abhishek Patil     | 2024SL70004 | [CheetahPatil](https://github.com/CheetahPatil) | 2024sl70004@wilp.bits-pilani.ac.in |
| 3 | Yash Shimpi        | 2024SL70005 | [yash8shimpi](https://github.com/yash8shimpi)   | 2024sl70005@wilp.bits-pilani.ac.in |
| 4 | Aburba Chakraborty | 2024SL70048 | [apurbaXD](https://github.com/apurbaXD)         | 2024sl70048@wilp.bits-pilani.ac.in |

- Team ID: `APIBP001`
- Chosen Approach: `A` — REST-First + GraphQL Layer

---

## Quick start

```bash
git clone https://github.com/SAP2024-4Y/APIBP001
cd APIBP001
docker compose up
# Server:       http://localhost:8080
# REST docs:    http://localhost:8080/docs
# GraphQL IDE:  http://localhost:8080/graphql
```

The server seeds 40+ entities automatically on first boot. No manual migration or seed command is needed.

---

### Demo credentials

| Field    | Value           |
|----------|-----------------|
| Username | `demo`          |
| Password | `demo-password` |

```bash
# Obtain a JWT token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo-password"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

---

### REST examples

```bash
# List students (cursor pagination)
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/students?limit=5"

# Next page using cursor
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/students?limit=5&after=<next_cursor>"

# Filter and sort courses
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/courses?status=ACTIVE&category=Computer+Science&sort=title&order=asc"

# Nearby campus locations — GeoJSON response (Map pattern)
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/locations/nearby?latitude=40.7128&longitude=-74.0060&radius_km=1.0"

# Schedules in a time window (Calendar pattern)
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/v1/schedules?starts_at=2025-02-10T00:00:00Z&ends_at=2025-02-17T23:59:59Z"

# Create a schedule (returns 409 if the slot overlaps)
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  "http://localhost:8080/api/v1/schedules" \
  -d '{"courseId":1,"locationId":1,"startTime":"2026-06-01T10:00:00","endTime":"2026-06-01T12:00:00"}'
```

---

### GraphQL example

Open the GraphiQL IDE at `http://localhost:8080/graphql` and run:

```graphql
{
  students(limit: 3) {
    data { id firstName lastName email }
    has_more
    next_cursor
  }
}
```

Or via curl:

```bash
curl -s -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  "http://localhost:8080/graphql" \
  -d '{"query":"{ students(limit:3) { data { id firstName lastName email } has_more next_cursor } }"}'
```

---

### Run tests locally

```bash
mvn verify
```

---

## Service endpoints

| Interface        | URL                             |
|------------------|---------------------------------|
| REST API         | http://localhost:8080/api/v1/   |
| Swagger UI       | http://localhost:8080/docs      |
| GraphQL / GraphiQL | http://localhost:8080/graphql |
| Health check     | http://localhost:8080/health    |

---

## Rate limiting

All endpoints are rate-limited to **100 requests/minute per IP**. Every response carries:

- `RateLimit-Limit` — maximum requests per window
- `RateLimit-Remaining` — requests remaining in the current window
- `RateLimit-Reset` — Unix timestamp when the window resets

HTTP `429 Too Many Requests` is returned when the limit is exceeded.

---

## Architecture

See [`docs/ADR.md`](docs/ADR.md) for the full Architecture Decision Record.
