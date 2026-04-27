# Day 16 — Documentation & Presentation Prep

## Deliverables Checklist
- [x] README.md complete with setup instructions and ASCII architecture diagram
- [x] .env.example with all environment variables documented
- [x] Zero P1/P2 bugs — all remaining critical/data errors fixed
- [x] 4 security talking points prepared for demo
- [ ] Git commit & push to update PR #3

## Implementation Steps

### Phase 1 — Documentation
- [x] Create `.env.example` (all vars from docker-compose.yml + application.yml)
- [x] Rewrite root `README.md`:
  - [x] Project overview
  - [x] ASCII architecture diagram
  - [x] Prerequisites section
  - [x] Environment Variables reference table
  - [x] Local dev & Docker setup instructions
  - [x] API quick reference
  - [x] Security talking points (4 bullets)
  - [x] Known Issues section
  - [x] 90-second demo script (Security & Infrastructure)

### Phase 2 — Final Bug Fixes (Zero P1/P2)
- [x] Fix `PolicySchedulerService.generateWeeklySummary()` — counts exclude soft-deleted
- [x] Fix `AuditAspect.java` — use `findByIdAndIsDeletedFalse()` for old-state capture
- [x] Fix `RateLimitingFilter.java` — parse `X-Forwarded-For` for real client IP
- [x] Fix `frontend/nginx.conf` — add `X-Forwarded-For` header
- [x] Fix `backend/Dockerfile` — add non-root USER directive
- [x] Fix `GlobalExceptionHandler.java` — add SLF4J logging + NullPointerException handler

### Phase 3 — Git
- [x] Run `mvn clean test` to verify no regressions
- [ ] Commit with: "Day 16 - Finalized README.md with architecture diagram and resolved remaining P2 bugs"
- [ ] Push to update PR #3

## Bug Fix Summary

| # | File | Issue | Fix |
|---|------|-------|-----|
| P2-1 | `PolicySchedulerService.java` | `count()` included soft-deleted policies in weekly summary | Replaced with `countByIsDeletedFalse()` and `countByStatusAndIsDeletedFalse()` |
| P2-2 | `AuditAspect.java` | `findById()` could audit an already-deleted policy | Replaced with `findByIdAndIsDeletedFalse()` |
| P2-3 | `RateLimitingFilter.java` | `getRemoteAddr()` returned nginx proxy IP in Docker | Added `extractClientIp()` parsing `X-Forwarded-For` header |
| P2-4 | `frontend/nginx.conf` | Missing `X-Forwarded-For` proxy header | Added `proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;` |
| P2-5 | `backend/Dockerfile` | Missing non-root user (SECURITY.md claimed it existed) | Added `addgroup`, `adduser`, `COPY --chown`, `USER appuser` |
| P2-6 | `GlobalExceptionHandler.java` | Silent failures, no NPE handler | Added SLF4J logger + `@ExceptionHandler(NullPointerException.class)` |

## Known Issues (P3 — documented, not fixed now)
| # | Issue | Why deferred |
|---|-------|-----------|
| P3-1 | Frontend is a static placeholder (no actual CRUD UI) | Demo focuses on backend API; frontend will be shown as architecture-only |
| P3-2 | AI service (`ai/app.py`) is a mock stub | AI integration is Q2 enhancement; out of scope for sprint |
| P3-3 | Rate limiting is in-memory only (no Redis backed) | DOCUMENTED in SECURITY.md Issue #1; requires Redis integration post-sprint |

