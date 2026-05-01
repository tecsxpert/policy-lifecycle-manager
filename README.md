# Policy Lifecycle Manager

**Tool-29** — A production-ready Spring Boot 3.x REST API for managing insurance policies with JWT authentication, RBAC, audit logging, rate limiting, and automated scheduled tasks.

| | |
|---|---|
| **Java** | 17 |
| **Spring Boot** | 3.2.0 |
| **Database** | PostgreSQL 15 |
| **Cache** | Redis 7 |
| **Frontend** | React 18 |
| **AI Service** | Python 3.11 (Flask) |
| **Build** | Maven 3.9 |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT LAYER                                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                       │
│  │   Browser    │  │   Swagger    │  │   curl/      │                       │
│  │   (React)    │  │    UI        │  │   Postman    │                       │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘                       │
└─────────┼─────────────────┼─────────────────┼───────────────────────────────┘
          │                 │                 │
          └─────────────────┼─────────────────┘
                            │ HTTP/JSON
┌───────────────────────────▼─────────────────────────────────────────────────┐
│                           PROXY LAYER                                        │
│                    ┌─────────────────┐                                       │
│                    │   Nginx (80)    │  ← Serves React static build          │
│                    │   frontend      │  ← Proxies /api/* → backend:8080      │
│                    └────────┬────────┘                                       │
└─────────────────────────────┼───────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────────────┐
│                         APPLICATION LAYER                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │              Spring Boot 3.x (port 8080)                            │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │    │
│  │  │   Auth      │  │   Policy    │  │   Audit     │  │  Search   │ │    │
│  │  │ Controller  │  │ Controller  │  │   Aspect    │  │  & Stats  │ │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └─────┬─────┘ │    │
│  │         │                │                │               │       │    │
│  │  ┌──────▼────────────────▼────────────────▼───────────────▼─────┐ │    │
│  │  │              Service Layer (Spring AOP)                      │ │    │
│  │  │  • JWT Filter  • Rate Limiting  • Input Sanitization        │ │    │
│  │  │  • RBAC (@PreAuthorize)  • Global Exception Handling        │ │    │
│  │  └────────────────────────┬────────────────────────────────────┘ │    │
│  │                           │                                      │    │
│  │  ┌────────────────────────▼────────────────────────────────────┐ │    │
│  │  │              Data Access Layer (Spring Data JPA)             │ │    │
│  │  │  • PolicyRepository  • UserRepository  • AuditRepository   │ │    │
│  │  └────────────────────────┬────────────────────────────────────┘ │    │
│  └───────────────────────────┼──────────────────────────────────────┘    │
└─────────────────────────────┼─────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────────────┐
│                          DATA LAYER                                          │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐  │
│  │   PostgreSQL 15     │  │      Redis 7        │  │   Flyway Migrations │  │
│  │   (port 5432)       │  │   (port 6379)       │  │   V1 → V5           │  │
│  │   policies          │  │   Session/Cache     │  │   Schema + Indexes  │  │
│  │   users             │  │                     │  │   Seed Data         │  │
│  │   audit_log         │  │                     │  │                     │  │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────────────┐
│                         AI LAYER                                             │
│                    ┌─────────────────┐                                       │
│                    │  Python Flask   │  ← /health, /predict (mock)           │
│                    │  (port 5000)    │  ← Extensible for ML inference        │
│                    └─────────────────┘                                       │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Java JDK | 17+ | Compile & run Spring Boot |
| Maven | 3.9+ | Build & dependency management |
| Docker | 24+ | Containerized local stack |
| Docker Compose | 2.20+ | Orchestrate 5 services |
| Node.js | 20+ | Build React frontend (optional) |

---

## Environment Variables

Copy `.env.example` to `.env` and configure the values below.

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `POSTGRES_DB` | Yes | `policydb` | PostgreSQL database name |
| `POSTGRES_USER` | Yes | `policyuser` | PostgreSQL username |
| `POSTGRES_PASSWORD` | Yes | — | **Strong password** for PostgreSQL |
| `POSTGRES_PORT` | No | `5432` | Host port mapped to PostgreSQL |
| `REDIS_PORT` | No | `6379` | Host port mapped to Redis |
| `AI_PORT` | No | `5000` | Host port mapped to Python AI service |
| `BACKEND_PORT` | No | `8080` | Host port mapped to Spring Boot |
| `FRONTEND_PORT` | No | `80` | Host port mapped to Nginx |
| `JWT_SECRET` | Yes | — | **256-bit secret** for HS256 JWT signing. Generate with `openssl rand -base64 32` |

> **Security Note:** `JWT_SECRET` must be at least 32 bytes (256 bits) for HS256. Never reuse keys across environments.

---

## Quick Start (Docker Compose)

```bash
# 1. Clone and enter the project
cd policy-lifecycle-manager

# 2. Create environment file
cp .env.example .env
# → Edit .env and set POSTGRES_PASSWORD and JWT_SECRET

# 3. Start all 5 services
docker-compose up --build -d

# 4. Verify health
docker-compose ps
# Expected: postgres, redis, ai, backend, frontend all "healthy" or "Up"

# 5. Verify seed data
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT COUNT(*) FROM policies;"
# Expected: 30

# 6. Open Swagger UI
open http://localhost:8080/swagger-ui/index.html
```

## Local Development (IntelliJ / VS Code)

```bash
# 1. Start infrastructure only
docker-compose up postgres redis -d

# 2. Run Spring Boot from IDE or:
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Run tests
cd backend
mvn clean test
# Expected: BUILD SUCCESS, JaCoCo ≥ 80% line coverage
```

---

## API Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register new user (role = `VIEWER`) |
| POST | `/api/auth/login` | Public | Authenticate, receive JWT |
| POST | `/api/auth/refresh` | Public | Refresh expired JWT |

### Policies (`/api/policies`)
| Method | Endpoint | RBAC | Description |
|--------|----------|------|-------------|
| GET | `/api/policies/all` | ADMIN, MANAGER, VIEWER | Paginated list (excludes soft-deleted) |
| POST | `/api/policies/create` | ADMIN, MANAGER | Create new policy |
| PUT | `/api/policies/{id}` | ADMIN, MANAGER | Update existing policy |
| DELETE | `/api/policies/{id}` | ADMIN | Soft-delete (sets `is_deleted=true`) |
| GET | `/api/policies/search?q={term}` | ADMIN, MANAGER, VIEWER | Search by name or holder |
| GET | `/api/policies/stats` | ADMIN, MANAGER, VIEWER | Total & active counts |
| GET | `/api/policies/export` | ADMIN, MANAGER, VIEWER | CSV export of all non-deleted policies |

### Swagger / OpenAPI
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## Database Migrations (Flyway)

| Migration | Purpose |
|-----------|---------|
| `V1__init.sql` | Creates `policies`, `users`, `roles` tables + indexes |
| `V2__audit.sql` | Creates `audit_log` table with JSON old/new values |
| `V3__roles.sql` | Seeds `ADMIN`, `MANAGER`, `VIEWER` roles |
| `V4__seed_data.sql` | Inserts 30 realistic demo policies |
| `V5__performance_indexes.sql` | Composite & partial indexes for query optimization |

---

## Scheduled Jobs

| Schedule | Task | Description |
|----------|------|-------------|
| Daily 1:00 AM | `checkOverduePolicies()` | Find non-COMPLETED policies past expiry (capped at 500) |
| Daily 2:00 AM | `checkExpiringSoonPolicies()` | Find policies expiring in exactly 7 days (capped at 500) |
| Monday 9:00 AM | `generateWeeklySummary()` | Log total, active, pending policy counts |

> All scheduler methods run with `@Transactional(readOnly = true)` and a pool size of 5 to prevent blocking.

---

## Security Implementation — 4 Demo Talking Points

### 1. JWT Authentication & Authorization
- Every protected endpoint requires a valid Bearer token in the `Authorization` header.
- Tokens are signed with HS256 and expire after 24 hours (configurable via `JWT_EXPIRATION_MS`).
- Role-Based Access Control (RBAC) is enforced at the method level with `@PreAuthorize` — e.g., `hasRole('ADMIN')` for soft-delete, `hasAnyRole('ADMIN', 'MANAGER')` for mutations.

### 2. Rate Limiting (Bucket4j)
- In-memory per-IP rate limiting prevents brute-force and abuse:
  - `/api/auth/**` → 100 req/min
  - `/api/policies/**` → 200 req/min
  - All others → 300 req/min
- Exceeding the limit returns HTTP 429 with a `Retry-After` header.

### 3. Input Sanitization & XSS Prevention
- All free-text user inputs pass through `InputSanitizer` before persistence.
- HTML/script metacharacters (`<`, `>`, `&`, `"`, `'`, `/`) are stripped to prevent XSS.
- Search queries are sanitized before repository execution.

- **Zero hardcoded secrets:** DB password and JWT secret are injected via environment variables (`${DB_PASSWORD}`, `${JWT_SECRET}`).
- `.env` is excluded from Git via `.gitignore`.
- Test secrets in `application-test.yml` are explicitly labeled `TEST-ONLY`.
- The stack is containerized with non-root users, health checks, and network isolation — ready for OWASP ZAP scanning.

---

## 90-Second Demo Script: "Security and Infrastructure"

> *Use this script during the live 8-minute presentation. Speak clearly and point to the code/Swagger as you go.*

**[0:00–0:15] RBAC in Action**
> "First, let me show Role-Based Access Control. I have three roles: ADMIN, MANAGER, and VIEWER. Watch what happens when a VIEWER tries to delete a policy."
> *In Swagger: login as VIEWER → try DELETE /api/policies/1 → show 403 Forbidden.*
> "Only ADMIN can delete. MANAGERS can create and update. VIEWERS can only read and search. This is enforced with Spring Security `@PreAuthorize` annotations."

**[0:15–0:35] Flyway Migrations**
> "Our database schema is version-controlled with Flyway. We have five migrations — from the initial `policies` table to performance indexes and 30 seed records. When the container starts, Flyway automatically applies any missing migrations. This means our schema is reproducible across dev, staging, and production."
> *Show `db/migration/` folder or run `SELECT * FROM flyway_schema_history;` in psql.*

**[0:35–0:55] Audit Logging**
> "Every create, update, and soft-delete is audited via Spring AOP. Here is the `audit_log` table. When I update a policy name, the old value and new value are captured as JSON."
> *In Swagger: PUT update a policy name → query `audit_log` → show `POLICY_UPDATED` with old and new JSON.*
> "Even if Jackson serialization fails, the audit log is still saved with an error placeholder — the application never crashes."

**[0:55–1:15] Rate Limiting & Input Sanitization**
> "To prevent abuse, we have per-IP rate limiting. Auth endpoints are capped at 100 requests per minute. If I exceed that, I get a 429 Too Many Requests with a Retry-After header."
> *Optionally show a rapid-fire curl loop hitting 429.*
> "All user inputs are sanitized — HTML tags are stripped before they ever reach the database, preventing XSS attacks."

**[1:15–1:30] Closing**
> "In summary: JWT tokens with method-level RBAC, versioned database migrations with Flyway, immutable audit trails with AOP, and defense-in-depth with rate limiting and input sanitization. This is a production-grade security posture ready for enterprise deployment."

---

## Known Issues

| ID | Severity | Issue | Reason |
|----|----------|-------|--------|
| P3-1 | Low | Frontend is a static placeholder page | Demo focuses on backend API; full CRUD UI is Q2 roadmap |
| P3-2 | Low | AI service (`ai/app.py`) is a mock stub | ML model integration deferred to post-sprint |
| P3-3 | Low | Rate limiting is in-memory only | Redis-backed distributed limiting planned (see SECURITY.md Issue #1) |

---

## Testing

```bash
cd backend
mvn clean test
```

- **Unit tests:** JWT utility, input sanitizer, exception handler
- **Integration tests:** Auth controller, policy controller with `@SpringBootTest`
- **Repository tests:** `@DataJpaTest` with H2 in-memory
- **JaCoCo threshold:** 80% line coverage enforced in `pom.xml`

---

## Project Structure

```
policy-lifecycle-manager/
├── .env.example              # Environment variable template
├── docker-compose.yml        # 5-service orchestration
├── README.md                 # This file
├── SECURITY.md               # Security checklist & team sign-off
├── QA_CHECKLIST.md           # P1/P2 verification steps
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   ├── src/main/java/...     # Spring Boot application
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/     # Flyway V1–V5
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/                  # React app
└── ai/
    └── app.py                # Flask AI service stub
```

---

## License

Internal use only — Internship Tool-29 Project.




