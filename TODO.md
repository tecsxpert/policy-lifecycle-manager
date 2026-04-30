# Day 17 — Demo Rehearsal & Slide Prep

## Deliverables Checklist
- [x] 3 opening slides created (Problem Statement, Architecture, Demo Flow)
- [x] 90-second verbal script refined with timing blocks
- [x] 3 public speaking tips applied to RBAC/Audit Logging segment
- [x] 20-second architecture summary memorized
- [x] Q&A cheat sheet prepared for Docker health checks & Flyway migrations
- [x] Build verification: mvn clean test passes (105 tests, 0 failures)
- [x] All demo scenarios verified via integration tests
- [x] Git commit & push Day 17 updates

## Implementation Steps

### Phase 1 — Presentation Slides
- [x] Slide 1: The Problem Statement (20 seconds)
- [x] Slide 2: The Architecture — ASCII diagram from README (20 seconds)
- [x] Slide 3: The Demo Flow (20 seconds)
- [x] Constraint verified: 3 slides = 1 minute total

### Phase 2 — Verbal Script Refinement
- [x] Block A — RBAC in Action (0:00–0:20)
- [x] Block B — Flyway Migrations (0:20–0:35)
- [x] Block C — Audit Logging Live Demo (0:35–0:55)
- [x] Block D — Rate Limiting & Input Sanitization (0:55–1:15)
- [x] Block E — Closing Statement (1:15–1:30)

### Phase 3 — Public Speaking Coach Output
- [x] Tip 1: Active Voice + Outcome
- [x] Tip 2: Replace "I think / maybe" with definitive statements
- [x] Tip 3: Use contrast to emphasize value

### Phase 4 — Q&A Prep
- [x] Q1: Database schema changes in production without downtime?
- [x] Q2: Why in-memory rate limiting over Redis-backed?
- [x] Q3: What if audit log serialization fails mid-transaction?

### Phase 5 — Transition Notes
- [x] Handoff from Java Dev 1 (Auth) → You (RBAC/Audit)
- [x] Handoff from You (Audit done) → Frontend / Next speaker

### Phase 6 — Git
- [x] Commit with: "Day 17 - Added presentation slides, rehearsal script, and Q&A prep"
- [x] Push to update PR #3

## Known Issues (P3 — documented, not fixed now)
| # | Issue | Why deferred |
|---|-------|-----------|
| P3-1 | Frontend is a static placeholder (no actual CRUD UI) | Demo focuses on backend API; frontend will be shown as architecture-only |
| P3-2 | AI service (`ai/app.py`) is a mock stub | AI integration is Q2 enhancement; out of scope for sprint |
| P3-3 | Rate limiting is in-memory only (no Redis backed) | DOCUMENTED in SECURITY.md Issue #1; requires Redis integration post-sprint |
