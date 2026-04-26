# Day 10 & 11 Implementation TODO

## Day 10 — Core Feature Development
- [x] Analyze project structure and identify missing pieces
- [x] Create `backend/pom.xml`
- [x] Create `backend/src/main/resources/application.yml`
- [x] Create `Policy.java` entity
- [x] Create `User.java` entity
- [x] Create `UserRepository.java`
- [x] Create `JwtUtil.java`
- [x] Create `JwtAuthenticationFilter.java`
- [x] Update `SecurityConfig.java`
- [x] Update `PolicyController.java` (add GET /all, POST /create, update soft delete)
- [x] Update `PolicyRepository.java`
- [x] Update `V1__init.sql`
- [x] Create `V4__seed_data.sql` (30 demo records)
- [x] Create `PolicyControllerIntegrationTest.java`
- [x] Update `SecurityAuditTest.java`
- [x] Create `docker-compose.yml`
- [x] Create `Dockerfile`
- [x] Create `V5__performance_indexes.sql`

## Day 11 — Integration Testing & Coverage (80% JaCoCo)
- [x] Add Testcontainers dependencies to `pom.xml`
- [x] Add AssertJ dependency
- [x] Add JaCoCo plugin with 80% threshold
- [x] Create `FullSystemIntegrationTest.java` (Testcontainers + PostgreSQL + full CRUD + audit log)
- [x] Create `PolicyRepositoryTest.java` (@DataJpaTest with custom queries, pagination, counts)
- [x] Create `JwtUtilTest.java` (unit tests for JWT generation/validation)
- [x] Create `AuthControllerTest.java` (register, login, refresh, error cases)
- [x] Create `PolicySchedulerServiceTest.java` (Mockito unit tests for cron jobs)
- [x] Create `GlobalExceptionHandler.java` (REST exception handler)
- [x] Create `GlobalExceptionHandlerTest.java` (403, 400, 500 scenarios)
- [x] Update `docker-compose.yml` with 5 services (Backend, AI, Frontend, DB, Redis)
- [x] Create `frontend/index.html` and `frontend/nginx.conf`
- [ ] Run `mvn clean test` (Maven not installed on this machine — run locally)
- [ ] Run `docker-compose up --build` (Docker not installed on this machine — run locally)
- [ ] Commit with message: "Day 11 - Reached 80% coverage with Testcontainers and Repository tests"

