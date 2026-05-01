# Tool-29 Demo Presentation — Java Developer 2 (Sangeeta Badiyappa Rathod)
**Date:** May 6, 2026 (Day 17) — First Full-Team Rehearsal  
**Total Time:** 8 minutes | **Opening Slides:** 1 minute | **My Segment:** 90 seconds

---

## Slide 1 — The Problem Statement (20 seconds)

### On Screen
```
┌─────────────────────────────────────────────┐
│                                             │
│     🏢 THE PROBLEM: Policy Chaos            │
│                                             │
│                                             │
│  • Insurance brokers manage thousands       │
│    of policies across spreadsheets & email  │
│                                             │
│  • No audit trail → compliance failures     │
│                                             │
│  • No access controls → anyone can delete   │
│                                             │
│  • Expired policies slip through unnoticed  │
│                                             │
│                                             │
│     💡 SOLUTION: Policy Lifecycle Manager   │
│                                             │
└─────────────────────────────────────────────┘
```

### Your Script
> "Insurance brokers today juggle thousands of policies across spreadsheets and email — no audit trail, no access controls, and expired policies slip through the cracks. We're solving this with a secure, auditable, automated Policy Lifecycle Manager."

---

## Slide 2 — The Architecture (20 seconds)

### On Screen
```
┌─────────────────────────────────────────────┐
│                                             │
│     ⚙️  ARCHITECTURE: 5 Layers             │
│                                             │
│          ┌─────────┐                        │
│          │ React   │  ← User Interface      │
│          └───┬─────┘                        │
│              │                              │
│          ┌───▼─────┐                        │
│          │ Nginx   │  ← Reverse Proxy       │
│          └───┬─────┘                        │
│              │                              │
│          ┌───▼───────────┐                  │
│          │ Spring Boot   │  ← Business Logic│
│          │  (Java 17)    │    + Security   │
│          └───┬───────────┘                  │
│              │                              │
│          ┌───▼──────┐    ┌────────┐        │
│          │PostgreSQL│ ←→ │ Redis  │        │
│          └──────────┘    └────────┘        │
│                                             │
└─────────────────────────────────────────────┘
```

### Your Script (20 seconds — 3 bullet points)
> "Our architecture is five layers. One — a React frontend served by Nginx. Two — a Spring Boot 3 backend with JWT authentication and RBAC. Three — PostgreSQL for policy data and Redis for caching, plus a Python AI prediction service. Every layer runs in Docker with health checks and a dedicated bridge network."

---

## Slide 3 — The Demo Flow (20 seconds)

### On Screen
```
┌─────────────────────────────────────────────┐
│                                             │
│     🎬 DEMO FLOW (8 Minutes Total)          │
│                                             │
│  ┌─────────┐  1️⃣  Auth Flow                │
│  │  Java   │      Login → JWT → Access      │
│  │ Dev 1   │                                │
│  └─────────┘                                │
│  ┌─────────┐  2️⃣  CRUD Operations           │
│  │  Java   │      Create → Update → Search │
│  │ Dev 2   │      ← YOU ARE HERE            │
│  └─────────┘        + Audit Logging         │
│  ┌─────────┐        + RBAC Demo             │
│  │  FE/QA  │  3️⃣  Frontend & AI Prediction  │
│  └─────────┘                                │
│                                             │
└─────────────────────────────────────────────┘
```

### Your Script
> "The demo flows through three segments: Java Dev 1 handles authentication, then I demonstrate CRUD with audit logging and RBAC enforcement, and finally the frontend team shows the React UI and AI prediction endpoint."

---

## Java Developer 2 — 90-Second Segment Script

### Timing Breakdown
| Block | Topic | Time |
|-------|-------|------|
| A | RBAC in Action | 0:00–0:20 |
| B | Flyway Migrations | 0:20–0:35 |
| C | Audit Logging Live Demo | 0:35–0:55 |
| D | Rate Limiting & Input Sanitization | 0:55–1:15 |
| E | Closing Statement | 1:15–1:30 |

---

### Block A — RBAC in Action (20 seconds)
> "First, Role-Based Access Control. We have three roles: ADMIN, MANAGER, and VIEWER. Watch what happens when a VIEWER tries to delete a policy."
> *[Swagger: login as VIEWER → DELETE /api/policies/1 → show 403 Forbidden]*
> "Only ADMIN can delete. MANAGERS can create and update. VIEWERS can only read and search. This is enforced with Spring Security `@PreAuthorize` at the method level."

### Block B — Flyway Migrations (15 seconds)
> "Our database schema is version-controlled with Flyway. Five migrations — from the initial policies table to performance indexes and thirty seed records. When the container starts, Flyway automatically applies any missing migrations, making our schema reproducible across dev, staging, and production."

### Block C — Audit Logging Live Demo (20 seconds)
> "Every create, update, and soft-delete is audited via Spring AOP. I'm updating a policy name now — and here is the audit_log table."
> *[Swagger: PUT update policy name → query audit_log → show POLICY_UPDATED]*
> "Old value and new value are captured as JSON. Even if Jackson serialization fails, the audit log is still saved with an error placeholder — the application never crashes."

### Block D — Rate Limiting & Input Sanitization (20 seconds)
> "To prevent abuse, we have per-IP rate limiting. Auth endpoints are capped at 100 requests per minute. Exceed that and you get a 429 Too Many Requests with a Retry-After header. All user inputs pass through InputSanitizer — HTML tags are stripped before they reach the database, preventing XSS."

### Block E — Closing Statement (15 seconds)
> "In summary: JWT tokens with method-level RBAC, versioned schema migrations with Flyway, immutable audit trails with AOP, and defense-in-depth with rate limiting and input sanitization. Production-grade security, ready for enterprise deployment."

---

## Public Speaking Coach Tips

### 3 Tips to Sound More Professional (RBAC & Audit Logging)

**Tip 1: Use "Active Voice + Outcome"**
- ❌ Weak: "We have roles and audit logs."
- ✅ Strong: "We enforce three distinct roles at the method level, and every mutation is automatically audited with full before-and-after JSON snapshots."

**Tip 2: Replace "I think / maybe" with definitive statements**
- ❌ Weak: "I think the audit log catches everything."
- ✅ Strong: "The audit aspect intercepts every controller method guaranteed — there is no code path that can mutate a policy without generating a log entry."

**Tip 3: Use contrast to emphasize value**
- ❌ Flat: "We have rate limiting."
- ✅ Strong: "Without rate limiting, a single malicious client could brute-force auth endpoints. With our Bucket4j filter, each IP gets its own bucket — 100 requests per minute for auth, 200 for policy operations — and exceeding it returns a 429 with a Retry-After header."

---

## Architecture Slide — 20-Second Summary (3 Bullets)

> 1. **"Nginx proxies a React frontend to a Spring Boot 3 API, running on Java 17 with Spring Security 6."**
> 2. **"PostgreSQL stores policies and audit logs with Flyway versioned migrations; Redis handles session caching."**
> 3. **"Everything is containerized with Docker health checks, non-root users, and network isolation on a dedicated bridge."**

*Practice this until it flows in exactly 20 seconds.*

---

## Q&A Prep — 3 Questions from Mentor

| # | Question | Your 1-Sentence Answer |
|---|----------|----------------------|
| 1 | *"How do you handle database schema changes in production without downtime?"* | "We use Flyway with baseline-on-migrate and backwards-compatible migrations — additive changes only in production, with a rollback script tested in staging." |
| 2 | *"Why did you choose in-memory rate limiting over Redis-backed?"* | "In-memory was sufficient for our 30-demo-user scale; we documented Redis-backed distributed limiting as Issue #1 in our SECURITY.md for post-sprint implementation." |
| 3 | *"What happens if your audit log serialization fails mid-transaction?"* | "The audit aspect catches Jackson exceptions, logs the error, and stores a `{"error": "serialization_failed"}` placeholder — the business transaction commits successfully regardless." |

---

## Day 17 TODO

- [x] Create 3 opening slides (Problem Statement, Architecture, Demo Flow)
- [x] Refine 90-second verbal script with timing blocks
- [x] Apply 3 public speaking tips to RBAC/Audit Logging segment
- [x] Memorize 20-second architecture summary
- [x] Prepare Q&A cheat sheet for Docker health checks & Flyway migrations
- [ ] Team dry run with stopwatch — identify transition gaps
- [ ] Fix any rehearsal issues (data loading, awkward handoffs)
- [ ] Re-test all 5 demo scenarios after fixes

---

## Transition Notes

| Handoff From | Handoff To | Cue |
|--------------|-----------|-----|
| Java Dev 1 (Auth) | **You (RBAC/Audit)** | *"Now that we're authenticated, let me show you what happens when the wrong role tries to do the wrong thing."* |
| **You (Audit done)** | Java Dev 2 next / Frontend | *"With our audit trail proven, let's see the frontend that brokers actually use — handing over to [Name]."* |

**Document Owner:** Sangeeta Badiyappa Rathod (Java Developer 2)  
**Last Updated:** 2026-05-06 (Day 17)
