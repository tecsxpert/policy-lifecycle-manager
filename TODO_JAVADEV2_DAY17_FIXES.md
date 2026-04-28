# Java Developer 2 — Day 17 Fixes Plan

## Missing Features (P1)
- [ ] 1. Create Service layer (`PolicyService`) with business logic + validation
- [ ] 2. Create DTOs (`PolicyRequest`, `PolicyResponse`)
- [ ] 3. Add Custom Exceptions (`PolicyNotFoundException`, `ValidationException`)
- [ ] 4. Update `GlobalExceptionHandler` — 404, 400, generic handlers with consistent JSON
- [ ] 5. Add Redis Cache Config + `@Cacheable` on GET endpoints
- [ ] 6. Add Email Service (`JavaMailSender`, `EmailService`) — send on create/overdue
- [ ] 7. Add File Attachment endpoints (`POST /upload`, `GET /files/{id}`)
- [ ] 8. Update `pom.xml` — add mail dependency, fix JaCoCo phase
- [ ] 9. Update `application.yml` — mail config with env placeholders
- [ ] 10. Add `@Valid` on controller request bodies

## Coverage Boost (P2)
- [ ] 11. `PolicyServiceTest` — 10+ unit tests (Mockito)
- [ ] 12. `EmailServiceTest` — mock JavaMailSender
- [ ] 13. `PolicyControllerTest` — MockMvc tests for new endpoints
- [ ] 14. Fix package typo `com.inship.tool` → `com.internship.tool`
- [ ] 15. Add DTO + Exception tests

## Verification (P3)
- [ ] 16. `mvn clean test` passes ≥ 80% line coverage
- [ ] 17. `docker-compose up --build` healthy
- [ ] 18. Update SECURITY.md / README / TODO status

