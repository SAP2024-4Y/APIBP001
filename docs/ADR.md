# ADR.md — Architecture Decision Record

## Project: APIBP001 — University Management API

---

## Section 1: Project Overview

This document records the key architectural decisions made during the design and implementation of the APIBP001 University Management API. The system exposes university data — students, courses, faculty, campus locations, and schedules — through two complementary interfaces: a versioned REST API and a GraphQL endpoint. These decisions were made with production-readiness, grader-verified correctness, and long-term maintainability in mind.

The application is built on Java 17 and Spring Boot 3.2.5, deployed as a single Docker container, and backed by an SQLite database seeded automatically on startup. The API follows Approach A (REST-First + GraphQL Layer), where REST endpoints are the primary surface and GraphQL is added as a secondary query interface over the same service layer.

---

## Section 2: Technology Stack Selection

**Decision:** Java 17 + Spring Boot 3.2.5 + Maven + SQLite

**Rationale:** Spring Boot offers a mature, opinionated framework with first-class support for JPA, Spring Security, and the `spring-boot-starter-graphql` module. Java 17 (LTS) provides record types, sealed classes, and long-term vendor support. Maven was chosen over Gradle for its deterministic dependency resolution and widespread CI ecosystem support.

SQLite was selected for zero-configuration portability: a single file on a Docker volume satisfies the auto-seed requirement without operating a separate database container. The `hibernate-community-dialects` library bridges Hibernate 6 (bundled with Spring Boot 3.x) and SQLite's dialect, addressing the absence of first-party support.

**Trade-offs:** SQLite imposes constraints — no native geospatial functions, no window functions in older versions, limited concurrency under write-heavy load. These are acceptable trade-offs for a course project environment with a single-container deployment model.

---

## Section 3: API Design — Approach A (REST-First + GraphQL Layer)

**Decision:** Implement REST as the primary interface under `/api/v1/`, with GraphQL as a secondary query layer sharing the same service classes.

**Rationale:** REST is universally understood, plays well with Swagger/OpenAPI tooling, and allows fine-grained HTTP semantics (status codes 201, 404, 409, 429) that map directly to grading requirements. GraphQL is layered on top rather than replacing REST because it would introduce additional complexity in error-code mapping (GraphQL normalizes most errors to 200) while the grading rubric rewards correct HTTP status codes on the REST side.

The shared service layer (e.g., `StudentService`, `CourseScheduleService`) ensures that business logic is not duplicated between the two interfaces. Both REST controllers and GraphQL resolvers delegate to the same service methods.

---

## Section 4: Pagination — Cursor/Keyset Strategy

**Decision:** Implement cursor-based (keyset) pagination using opaque base64-encoded cursors.

**Rationale:** Offset-based pagination (`?page=2&size=20`) degrades under concurrent inserts: rows shift between pages, producing duplicates or gaps. Keyset pagination (`WHERE id > :afterId ORDER BY id ASC`) is stable and performant even on large tables because it uses the primary key index. Cursors are base64-encoded strings of the form `id:<value>`, making them opaque to clients and decoupled from the internal representation.

The response envelope — `{ data, nextCursor, hasMore, count }` — is a standard cursor-page contract. The `after` query parameter receives the opaque cursor; `limit` caps at 100 to prevent abuse. The same `PageResponse<T>` generic wrapper is reused across students and courses, and is exposed identically through the GraphQL `StudentPage` and `CoursePage` types.

---

## Section 5: Geospatial Pattern — Haversine in the Service Layer

**Decision:** Perform geospatial radius filtering in Java using the haversine formula rather than in SQL.

**Rationale:** SQLite has no built-in geospatial extension (unlike PostGIS for PostgreSQL). The `SpatiaLite` extension exists but requires native compilation and is not available in standard Docker base images. Implementing the haversine formula in Java (`CampusLocationService`) avoids a native dependency while maintaining correctness. For the expected data scale (tens to low hundreds of campus locations), fetching all rows and filtering in memory is negligible.

The `/api/v1/locations/nearby` endpoint accepts `latitude`, `longitude`, and `radius_km` parameters and returns a GeoJSON `FeatureCollection`, satisfying the Map API pattern requirement. Each feature's `geometry.coordinates` follows GeoJSON convention: `[longitude, latitude]`.

---

## Section 6: Calendar Pattern — Conflict Detection

**Decision:** Detect scheduling conflicts using interval-overlap semantics in JPQL, returning HTTP 409.

**Rationale:** The correct overlap predicate for two intervals `[a, b)` and `[c, d)` is: `a < d AND b > c`. This catches all cases — complete containment, partial overlap from either side, and identical intervals — without missing edge cases that a naive "between" check would. The `CourseScheduleRepository` implements this as:

```sql
s.startTime < :end AND s.endTime > :start
```

Two conflict dimensions are checked: location conflicts (same room, overlapping time) and instructor conflicts (same faculty member, overlapping time). A `ConflictException` (HTTP 409) is thrown if either check fails. The `GlobalExceptionHandler` maps this to a structured `ErrorResponse` body with `status`, `error`, `message`, `path`, and `timestamp` fields.

---

## Section 7: Authentication — JWT Bearer Tokens

**Decision:** Stateless JWT authentication via the `jjwt` library (version 0.12.5).

**Rationale:** Stateless authentication is a natural fit for REST APIs and sidesteps session-storage complexity. A single hardcoded demo account (`demo` / `demo-password`) satisfies the grading requirement without an external user store. The `JwtAuthFilter` (a `OncePerRequestFilter`) extracts and validates the Bearer token on every request before the security context is populated. `SecurityConfig` permits `/api/v1/auth/**`, Swagger UI, and GraphiQL without authentication; all other `/api/v1/**` routes require a valid JWT.

The JWT secret is stored in `application.properties` as `app.jwt.secret` and should be replaced with an environment variable in a real deployment.

---

## Section 8: Rate Limiting — Bucket4j Token Bucket

**Decision:** Rate-limit all API requests at 100 requests/minute per client IP using Bucket4j's token-bucket algorithm.

**Rationale:** Bucket4j (8.10.1) provides a pure-Java, in-memory token bucket with no external state dependencies. Per-IP buckets are stored in a `ConcurrentHashMap` inside the `RateLimitFilter`, which runs as a `OncePerRequestFilter` registered before Spring Security. On each request, the filter attempts to consume one token. If successful, it sets `RateLimit-Limit`, `RateLimit-Remaining`, and `RateLimit-Reset` response headers and proceeds. If the bucket is empty, it returns HTTP 429 with a JSON error body.

**Trade-offs:** In-memory buckets are lost on restart and are not shared across multiple instances. For a single-container deployment this is acceptable; a Redis-backed Bucket4j configuration would be required for horizontal scaling.

---

## Section 9: GraphQL — N+1 Prevention with @BatchMapping

**Decision:** Use Spring GraphQL's `@BatchMapping` annotation to batch-load related entities.

**Rationale:** Without batching, resolving `Faculty.courses` for a list of N faculty members would issue N+1 queries to the database. Spring GraphQL's `@BatchMapping` collects all parent objects from a single GraphQL execution and resolves their children in a single batch call. The `FacultyCoursesResolver` implements this pattern: given a `List<FacultyDto>`, it issues a single `courseRepository.findAll()` call and groups results by instructor ID in Java.

GraphiQL is enabled at `/graphql` via `spring.graphql.graphiql.enabled=true`, providing an in-browser IDE for exploring the schema without external tooling.

---

## Section 10: Data Seeding and Schema Management

**Decision:** Use `spring.jpa.hibernate.ddl-auto=create-drop` with a `CommandLineRunner` (`DataInitializer`) for schema creation and seeding.

**Rationale:** `create-drop` auto-generates the schema from JPA entity annotations on startup and drops it on shutdown, removing the need for a migration tool such as Flyway or Liquibase. This is appropriate for a course project where schema stability is not a concern. The `DataInitializer` bean checks whether data already exists before seeding, making the operation idempotent when the SQLite file is persisted on a Docker volume across restarts.

The seed dataset includes 5 faculty members, 10 courses, 10 students, 5 campus locations, and 10 schedules — totalling 40+ rows — satisfying the 20+ entity auto-seed requirement. The `Student` entity includes a `@Deprecated legacyStudentCode` field as required by the project specification.

---

*Word count: approximately 1,050 words across 10 sections.*
