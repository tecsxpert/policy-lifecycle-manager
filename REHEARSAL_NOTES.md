# Rehearsal Notes — Java Developer 2 (Sangeeta Badiyappa Rathod)

**Date:** 2026-05-08 (Day 19) — Rehearsal 2  
**Total Time:** 8 minutes | **My Segment:** 90 seconds

---

## Rehearsal 1 Results (Day 17)

| Block | Planned Time | Actual Time | Issue | Fix Applied |
|-------|-------------|-------------|-------|-------------|
| RBAC Demo | 0:20 | 0:25 | Swagger UI took time to load | Pre-open Swagger in browser before demo starts |
| Flyway Migrations | 0:15 | 0:12 | Terminal font too small | Increase terminal font to 16pt |
| Audit Logging | 0:20 | 0:22 | JSON query wrapped awkwardly | Pre-format query in text file for copy-paste |
| Rate Limiting | 0:20 | 0:18 | curl loop ran too fast | Slow down to show individual responses |
| Closing | 0:15 | 0:13 | Rushed handoff | Practice transition cue 3 more times |

**Total Segment Time:** 1:30 planned → 1:30 actual ✅

---

## Fixes Applied After Rehearsal 1

1. ✅ Swagger UI pre-loaded in browser tab before demo
2. ✅ Terminal font increased to 16pt
3. ✅ Pre-formatted SQL queries saved in `demo_queries.sql`
4. ✅ curl loop modified to show 5 requests with 1-second delay
5. ✅ Transition script memorized: "With our audit trail proven..."

---

## Rehearsal 2 Checklist (Day 19)

- [ ] Full team present with stopwatch
- [ ] Clean Docker slate: `docker-compose down -v && docker-compose up --build -d`
- [ ] All 5 services healthy within 3 minutes
- [ ] 30 seed records confirmed
- [ ] Swagger UI loads and Authorize button works
- [ ] JWT login successful for admin and viewer accounts
- [ ] RBAC demo: VIEWER gets 403 on DELETE
- [ ] Flyway migrations visible in database
- [ ] Audit log shows POLICY_CREATED, POLICY_UPDATED, POLICY_DELETED
- [ ] Rate limiting returns 429 after 100 auth requests
- [ ] CSV export downloads successfully
- [ ] Handoff to next speaker is smooth
- [ ] Each team member answers 5 key questions without notes

---

## 5 Key Questions I Must Answer Without Notes

1. **"What does your component do?"**
   > "I own audit logging, database schema with Flyway, RBAC enforcement, rate limiting, input sanitization, and scheduled jobs. Every policy mutation is audited, every endpoint is protected by role, and every input is sanitized."

2. **"What security measures did you implement?"**
   > "Four layers: JWT authentication with method-level RBAC, per-IP rate limiting with Bucket4j, XSS prevention via input sanitization, and immutable audit trails via Spring AOP. Zero hardcoded secrets — all injected via environment variables."

3. **"How do you handle database schema changes?"**
   > "Flyway versioned migrations. Five SQL files from V1 to V5. Baseline-on-migrate ensures new environments start correctly. Additive changes only in production with rollback scripts tested in staging."

4. **"What happens if the AI service is down?"**
   > "The backend handles AI service unavailability gracefully. The AiServiceClient returns null on timeout, and the business transaction continues. The policy is still created — just without AI enrichment."

5. **"What is your test coverage?"**
   > "83% line coverage with 76 JUnit tests. JaCoCo enforces 80% minimum in CI. Tests cover unit, integration, repository, controller, and scheduler layers."

---

## Timing Cues (Memorize These)

| Cue | Action |
|-----|--------|
| "Now that we're authenticated..." | Start RBAC demo |
| "Our schema is version-controlled..." | Switch to terminal |
| "Every mutation is automatically audited..." | Create then update policy |
| "To prevent abuse..." | Show rate limiting |
| "With our audit trail proven..." | Handoff to next speaker |

---

## Emergency Contacts

| Issue | Contact |
|-------|---------|
| Docker won't start | Java Dev 3 (DevOps) |
| JWT token expired | Java Dev 1 (Auth) |
| Swagger UI broken | Java Dev 3 (Frontend) |
| Database locked | Java Dev 2 (You) — `docker-compose down -v` |
| Groq API down | AI Dev 1 — use backup screenshots |

---

## Post-Rehearsal Actions

- [ ] Note any new issues
- [ ] Fix P1/P2 issues immediately
- [ ] Document P3 issues as known
- [ ] Update DEMO_DAY_SCRIPT.md with timing adjustments
- [ ] Practice solo one more time before bed
- [ ] Sleep early — Demo Day is tomorrow!

---

**Document Owner:** Sangeeta Badiyappa Rathod (Java Developer 2)  
**Last Updated:** 2026-05-08 (Day 19)
