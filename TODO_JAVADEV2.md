# Java Developer 2 — Day 1 to Day 17 Fix Plan

## Critical Fixes (P1 — Block Build)
- [x] 1. Lombok fails with Java 25 — explicit annotation processor config in pom.xml
- [x] 2. Fix `docker-compose.yml` `ai` service indentation bug
- [x] 3. Fix `docker-compose.yml` backend Redis env var names (`SPRING_DATA_REDIS_HOST`)
- [x] 4. Add backend healthcheck in `docker-compose.yml`
- [x] 5. Add missing `.env.example` file
- [x] 6. Add backend Dockerfile healthcheck

## Missing Features (P2 — Day 9)
- [x] 7. Add CSV export endpoint `GET /api/policies/export`
- [x] 8. Add `InputSanitizer` unit test

## Verification (P3)
- [x] 9. Run `mvn clean compile` — must pass
- [x] 10. Run `mvn clean test` — must pass with ≥ 80% coverage
- [x] 11. Verify docker-compose syntax
- [x] 12. Update SECURITY.md sign-off and TODO.md status
