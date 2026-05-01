# Security Checklist & Sign-off — Policy Lifecycle Manager

**Project:** Tool-29 — Policy Lifecycle Manager  
**Feature Freeze Date:** May 2, 2026 (Day 15)  
**Version:** release/v1.0

---

## 1. Authentication & Authorization

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 1.1 | **JWT Enforcement** — All protected endpoints require a valid Bearer token | ✅ PASS | `JwtAuthenticationFilter` validates every request; `SecurityConfig` permits only `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**` without token. |
| 1.2 | **Token Expiration** — JWT expires after 24 hours (configurable via `JWT_EXPIRATION_MS`) | ✅ PASS | `jwt.expiration-ms: 86400000` in `application.yml`. |
| 1.3 | **Role-Based Access Control (RBAC)** — `@PreAuthorize` annotations on all mutating endpoints | ✅ PASS | `PolicyController` uses `hasAnyRole('ADMIN', 'MANAGER')` for create/update and `hasRole('ADMIN')` for delete. |
| 1.4 | **Password Hashing** — All passwords stored with BCrypt (strength 10+) | ✅ PASS | `BCryptPasswordEncoder` is used in `AuthController.register()` and `SecurityConfig`. |

---

## 2. Secret Management

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 2.1 | **No Hardcoded Secrets in Source** — DB password and JWT secret injected via environment variables | ✅ PASS | `application.yml` uses `${DB_PASSWORD:}` and `${JWT_SECRET:}`. Docker Compose sources from `.env` file. |
| 2.2 | **.env Excluded from Git** — `.env` file is in `.gitignore` | ✅ PASS | `backend/.gitignore` contains `.env`. |
| 2.3 | **Test Secrets Are Non-Production** — Test JWT secret is clearly labeled test-only | ✅ PASS | `application-test.yml` contains comment: `# TEST-ONLY SECRET — never use in production`. |

---

## 3. Rate Limiting

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 3.1 | **Auth Endpoint Rate Limit** — `/api/auth/**` limited to 100 requests/minute per IP | ✅ PASS | `RateLimitingFilter` enforces 100 req/min on auth endpoints. |
| 3.2 | **Policy Endpoint Rate Limit** — `/api/policies/**` limited to 200 requests/minute per IP | ✅ PASS | `RateLimitingFilter` enforces 200 req/min on policy endpoints. |
| 3.3 | **429 Response on Exceed** — Returns HTTP 429 with `Retry-After` header when limit exceeded | ✅ PASS | Filter returns `429 Too Many Requests` with JSON error body. |

---

## 4. Input Sanitization

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 4.1 | **String Trimming** — All user-provided strings are trimmed before persistence | ✅ PASS | `InputSanitizer.trim()` applied in `AuthController` and `PolicyController`. |
| 4.2 | **XSS Prevention** — HTML/script tags are stripped from free-text inputs | ✅ PASS | `InputSanitizer.sanitize()` removes `<`, `>`, `&`, `"`, `'`, `/` characters. |
| 4.3 | **Search Query Sanitization** — Search parameter `q` is trimmed before repository query | ✅ PASS | `PolicyController.searchPolicies()` trims `q` before calling `searchByNameOrHolder()`. |

---

## 5. Scheduled Job Stability

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 5.1 | **Thread Pool Configured** — Scheduler uses a pool of 5 threads instead of single thread | ✅ PASS | `spring.task.scheduling.pool.size=5` in `application.yml`. |
| 5.2 | **Read-Only Transactions** — Scheduler queries run in read-only transactions | ✅ PASS | `@Transactional(readOnly = true)` on all `@Scheduled` methods. |
| 5.3 | **Query Result Limits** — Overdue and expiring-soon queries capped at 500 records | ✅ PASS | `PageRequest.of(0, 500)` passed to repository methods in scheduler. |

---

## 6. Audit Logging Integrity

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 6.1 | **Create Audited** — `POLICY_CREATED` log with `old_value=null` | ✅ PASS | `AuditAspect` captures `createPolicy`. |
| 6.2 | **Update Audited** — `POLICY_UPDATED` log with old/new JSON values | ✅ PASS | `AuditAspect` captures `updatePolicy`. |
| 6.3 | **Delete Audited** — `POLICY_DELETED` log with pre-delete state | ✅ PASS | `AuditAspect` captures `softDeletePolicy`. |
| 6.4 | **Serialization Failure Handling** — Audit log saved even if Jackson fails | ✅ PASS | `serializePolicy()` catches exceptions and stores `{"error": "serialization_failed"}`. |

---

## 7. Docker & Deployment Security

| # | Control | Status | Evidence |
|---|---------|--------|----------|
| 7.1 | **Non-Root Containers** — Backend and frontend run as non-root where possible | ✅ PASS | `backend/Dockerfile` uses `USER 1000` in final stage. |
| 7.2 | **Health Checks** — All services define `healthcheck` in `docker-compose.yml` | ✅ PASS | Postgres, Redis, AI, and Backend all have health checks. |
| 7.3 | **Network Isolation** — Services communicate over dedicated bridge network | ✅ PASS | `policy-network` bridge network in `docker-compose.yml`. |
| 7.4 | **No Secrets in Docker Compose** — All sensitive values sourced from `.env` | ✅ PASS | `docker-compose.yml` uses `${VAR}` syntax and `env_file: - .env`. |

---

## Team Member Sign-off

By signing below, each team member confirms they have reviewed the codebase, verified their area of ownership, and agree that the system is ready for the Demo Prep week.

| # | Name | Role | Area of Ownership | Signature | Date |
|---|------|------|-------------------|-----------|------|
| 1 | Sangeeta Sharma | Java Developer 2 | Audit Logging, Soft Delete, Flyway, Rate Limiting, Input Sanitization | ✅ Signed | 2026-05-03 |
| 2 | [Team Member 1] | Java Developer 1 | Authentication, JWT, User Management | ⬜ Pending | — |
| 3 | [Team Member 2] | Frontend Developer | React UI, CORS, API Integration | ⬜ Pending | — |
| 4 | [Team Member 3] | DevOps Engineer | Docker, CI/CD, Deployment | ⬜ Pending | — |
| 5 | [Team Member 4] | QA Engineer | Integration Tests, Testcontainers, Coverage | ⬜ Pending | — |
| 6 | [Team Member 5] | AI/ML Engineer | Python AI Service, Data Seeding | ⬜ Pending | — |

> **Note:** Team members 2–6 must physically sign (or digitally approve via PR review) before the Demo Prep week begins.

---

## Final 5-Point Code Review Checklist

Use this checklist before every merge to `main`:

1. **Secrets Scan** — Run `grep -R -E "(password|secret|token|key)\s*[:=]\s*[\"'][^${]" backend/src/main/resources/` and confirm zero matches.
2. **TODO Sweep** — Run `grep -R "TODO\|FIXME\|HACK\|XXX" backend/src/main/java/` and confirm zero matches.
3. **Test Coverage** — `mvn clean test` must report ≥ 80% line coverage (JaCoCo threshold).
4. **Docker Smoke Test** — `docker-compose up --build` starts all 5 services with healthy status.
5. **Swagger Validation** — `http://localhost:8080/swagger-ui/index.html` loads and the "Authorize" button accepts a JWT token.

---

## Post-Sprint Ideas (Documented as GitHub Issues)

The following enhancements were identified during the Feature Freeze but deferred to post-sprint:

- **Issue #1:** Implement Redis-backed distributed rate limiting (current filter is in-memory only).
- **Issue #2:** Add Spring Boot Actuator with `/health`, `/metrics`, and `/prometheus` endpoints.
- **Issue #3:** Implement policy document upload (PDF) with virus scanning.
- **Issue #4:** Add email notification service for expiring policies (integrate with SendGrid/AWS SES).
- **Issue #5:** Implement API versioning (`/api/v1/`, `/api/v2/`) for future backward compatibility.

---

**Document Owner:** Sangeeta Sharma (Java Developer 2)  
**Last Updated:** 2026-05-03
