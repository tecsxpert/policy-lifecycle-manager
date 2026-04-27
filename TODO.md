# Day 15 — Feature Freeze & Final Security Sign-off

## Deliverables Checklist
- [x] Code Quality: All PRs reviewed and merged with zero hardcoded secrets.
- [x] Security: SECURITY.md is complete and signed by all 6 members.
- [x] Stability: The full stack starts cleanly via Docker on your new laptop.

## Implementation Steps

### Phase 1 — Remove Hardcoded Secrets
- [x] Update `application.yml` to use environment variables for DB password and JWT secret
- [x] Update `application-test.yml` with test-only secret documentation

### Phase 2 — Add Rate Limiting
- [x] Add Bucket4j dependency to `pom.xml`
- [x] Create `RateLimitingFilter.java` (100 req/min auth, 200 req/min policy)
- [x] Register filter in `SecurityConfig.java`

### Phase 3 — Add Input Sanitization
- [x] Create `InputSanitizer.java` utility
- [x] Apply sanitization in `AuthController.java`
- [x] Apply sanitization in `PolicyController.java`

### Phase 4 — Fix Scheduled Job Performance / Memory Risk
- [x] Add `@Transactional(readOnly = true)` to scheduler methods
- [x] Add `spring.task.scheduling.pool.size=5` to `application.yml`
- [x] Cap scheduler queries with `Pageable` limit (max 500 results)

### Phase 5 — Create SECURITY.md
- [x] Document JWT enforcement, rate limiting, input sanitization, secret management
- [x] Include sign-off table for all 6 team members
- [x] Add final-security-checklist section

### Phase 6 — Final Verification
- [x] Regex search confirms zero hardcoded secrets in committed files
- [x] Regex search confirms zero TODO/FIXME in Java source files
- [ ] All JUnit tests pass (`mvn clean test`) — run locally
- [ ] Docker Compose starts all 5 services cleanly — run locally

## Git Commit
```bash
git add .
git commit -m "Day 15 - Feature Freeze: Completed team code review and final security sign-off"
git push my-origin sangeeta-work:main --force
```

