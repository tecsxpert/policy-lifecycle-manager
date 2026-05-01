# Demo Day Script — Java Developer 2 (Sangeeta Badiyappa Rathod)

**Date:** Friday 9 May 2026  
**Demo Day:** 8-minute live presentation  
**My Segment:** Minutes 3:00 – 4:30 (90 seconds)

---

## Pre-Demo Setup (Arrive 30 minutes early)

1. **Clean Slate Reset:**
   ```bash
   docker-compose down -v
   docker-compose up --build -d
   ```
2. **Verify all 5 services healthy:**
   ```bash
   docker-compose ps
   ```
3. **Confirm 30 seed records:**
   ```bash
   docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT COUNT(*) FROM policies;"
   ```
4. **Open Swagger UI** in browser: `http://localhost:8080/swagger-ui/index.html`
5. **Open presentation slides** (3 slides: Problem, Architecture, Demo Flow)
6. **Test login** with admin credentials
7. **Close all unnecessary browser tabs and applications**

---

## Slide 1 — The Problem (15 seconds)

**On Screen:** Title slide with bullet points

**Script:**
> "Insurance brokers today manage thousands of policies across spreadsheets and email — no audit trail, no access controls, expired policies slip through unnoticed. Policy Lifecycle Manager solves this with a secure, auditable, automated REST API."

---

## Slide 2 — Architecture (15 seconds)

**On Screen:** 5-layer architecture diagram

**Script:**
> "Five layers. React frontend served by Nginx. Spring Boot 3 backend with JWT and RBAC. PostgreSQL for data, Redis for caching. Python AI service on Flask. Everything containerized with Docker health checks."

---

## Slide 3 — Demo Flow (15 seconds)

**On Screen:** 3-segment timeline

**Script:**
> "Three segments. First, authentication and JWT. Then my segment — CRUD with audit logging, RBAC enforcement, and security hardening. Finally, the React dashboard and AI predictions."

---

## LIVE DEMO — My Segment (90 seconds)

### [0:00–0:20] RBAC in Action

**Action:** Swagger UI → login as VIEWER → attempt DELETE

**Script:**
> "First, Role-Based Access Control. We have three roles: ADMIN, MANAGER, VIEWER. Watch what happens when a VIEWER tries to delete a policy."

**Click:** POST `/api/auth/login` with `demoviewer` credentials
**Click:** DELETE `/api/policies/1`
**Point to screen:** 403 Forbidden response

> "Only ADMIN can delete. MANAGERS can create and update. VIEWERS can only read and search. Enforced with Spring Security @PreAuthorize at the method level."

---

### [0:20–0:35] Flyway Migrations

**Action:** Terminal window → show migrations

**Script:**
> "Our database schema is version-controlled with Flyway. Five migrations — from the initial policies table to performance indexes and thirty seed records. When the container starts, Flyway automatically applies any missing migrations."

**Type:**
```bash
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT * FROM flyway_schema_history;"
```

> "This means our schema is reproducible across dev, staging, and production. Zero manual SQL."

---

### [0:35–0:55] Audit Logging Live

**Action:** Swagger UI → create policy → update policy → show audit log

**Script:**
> "Every create, update, and soft-delete is audited via Spring AOP. I'm creating a policy now — and here I update its name."

**Click:** POST `/api/policies/create` with "Before Update"
**Click:** PUT `/api/policies/{id}` with "After Update"
**Switch to terminal:**

```bash
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT action, changed_by, old_value->>'policyName', new_value->>'policyName' FROM audit_log WHERE action='POLICY_UPDATED' ORDER BY change_date DESC LIMIT 1;"
```

> "Old value and new value captured as JSON. Even if Jackson serialization fails, the audit log is still saved with an error placeholder — the application never crashes."

---

### [0:55–1:15] Rate Limiting & Input Sanitization

**Action:** Terminal → rapid curl loop or show pre-recorded output

**Script:**
> "To prevent abuse, per-IP rate limiting. Auth endpoints capped at 100 requests per minute. Exceed that and you get HTTP 429 with a Retry-After header."

**Show terminal with:**
```bash
curl -i http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"username":"x","password":"y"}'
# (Repeat until 429 appears)
```

> "And all user inputs are sanitized — HTML tags are stripped before reaching the database, preventing XSS attacks."

---

### [1:15–1:30] Closing

**Script:**
> "In summary: JWT tokens with method-level RBAC, versioned schema migrations with Flyway, immutable audit trails with AOP, and defense-in-depth with rate limiting and input sanitization. Production-grade security, ready for enterprise deployment."

**Handoff:**
> "With our audit trail proven and data integrity guaranteed, let's see the frontend — handing over to [Name]."

---

## Q&A Cheat Sheet

| Question | One-Sentence Answer |
|----------|-------------------|
| "How do you handle schema changes in production?" | "Flyway with baseline-on-migrate and backwards-compatible migrations — additive changes only, rollback scripts tested in staging." |
| "Why in-memory rate limiting instead of Redis?" | "Sufficient for demo scale; Redis-backed distributed limiting documented as Issue #1 in SECURITY.md for post-sprint." |
| "What if audit serialization fails mid-transaction?" | "Audit aspect catches Jackson exceptions, stores a placeholder, and the business transaction commits successfully." |
| "How do you prevent SQL injection?" | "Spring Data JPA with parameterized queries — no raw SQL concatenation. All inputs sanitized before persistence." |
| "What's your test coverage?" | "83% line coverage with 76 JUnit tests, all passing. JaCoCo enforces the threshold in CI." |

---

## Emergency Backup Plans

| Risk | Mitigation |
|------|-----------|
| **Docker fails to start** | Have `docker-compose ps` screenshot showing healthy services |
| **JWT token expires mid-demo** | Pre-generate tokens; refresh token endpoint ready |
| **Rate limit blocks legitimate demo** | Use different IP or restart Docker to reset buckets |
| **Audit log query is slow** | Pre-run query and screenshot results |
| **Swagger UI doesn't load** | Direct browser to `http://localhost:8080/v3/api-docs` as fallback |

---

## Post-Demo Actions

1. Thank the audience and mentors
2. Collect feedback for post-sprint issues
3. Update GitHub with final `release/v1.0` tag
4. Celebrate with the team 🎉

---

**Document Owner:** Sangeeta Badiyappa Rathod (Java Developer 2)  
**Last Updated:** 2026-05-07 (Day 18)
