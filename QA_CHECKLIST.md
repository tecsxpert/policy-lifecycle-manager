# Day 13 — QA Checklist: Full System Test & Bug Fixing

## P1 Bug Verification (Crash / System Failure)

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| 1 | **Clean Slate Test** | Run `docker-compose down -v`, then `docker-compose up --build` | All 5 services start. Flyway runs V1-V5. 30 seed records exist in DB. | ⬜ |
| 2 | **Swagger UI Accessibility** | Open `http://localhost:8080/swagger-ui/index.html` (no JWT) | Swagger UI loads. Auth endpoints visible. "Authorize" button present. | ⬜ |
| 3 | **JWT Authentication Flow** | Register → Login → Access `/api/policies/all` with Bearer token | 201 → 200 with token → 200 with paginated policies. | ⬜ |
| 4 | **Frontend → Backend CORS** | Open React frontend `http://localhost` and attempt API call | No CORS errors in browser console. API responds correctly. | ⬜ |
| 5 | **Soft Delete Integrity** | DELETE `/api/policies/1` as ADMIN, then GET `/api/policies/all` | Policy `is_deleted=true`, `status=DELETED`. Does NOT appear in `/all`. | ⬜ |

## P2 Bug Verification (Data Integrity / Incorrect Behavior)

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| 6 | **Audit Log Old/New Values** | PUT update policy name, then query `audit_log` table | `action=POLICY_UPDATED`. `old_value` JSON contains old name. `new_value` JSON contains new name. | ⬜ |
| 7 | **Audit Log on Create** | POST create policy, then query `audit_log` table | `action=POLICY_CREATED`. `old_value=null`. `new_value` JSON contains full new policy. | ⬜ |
| 8 | **Audit Log on Soft Delete** | DELETE policy, then query `audit_log` table | `action=POLICY_DELETED`. `old_value` JSON contains pre-delete state. `new_value` JSON contains `is_deleted=true`. | ⬜ |
| 9 | **Scheduled Job Logs in Docker** | Run `docker logs policy-backend --follow` and wait for 1:00 AM / 2:00 AM UTC | Overdue and Expiring Soon logs appear with SLF4J formatting (not raw stdout). | ⬜ |
| 10 | **RBAC Enforcement** | Attempt DELETE `/api/policies/1` as VIEWER role | Returns 403 Forbidden. Audit log does NOT create entry for unauthorized attempts. | ⬜ |

## Edge Cases for Soft Delete & Audit Logging

### Edge Case 1: Double Soft Delete
**Scenario:** Call DELETE on a policy that is already soft-deleted (`is_deleted=true`).
**Expected:** Returns 404 Not Found. No duplicate audit log entry. No data corruption.

### Edge Case 2: Update a Soft-Deleted Policy
**Scenario:** Call PUT `/api/policies/{id}` on a policy that has already been soft-deleted.
**Expected:** Returns 404 Not Found. `findByIdAndIsDeletedFalse` correctly filters deleted records. Audit log NOT created.

### Edge Case 3: Audit Log Serialization Failure
**Scenario:** Policy contains special characters or cyclic references that break Jackson serialization.
**Expected:** Audit log is still saved. `old_value` or `new_value` contains `{"error": "serialization_failed"}`. Application does NOT crash.

### Edge Case 4: Concurrent Update Race Condition
**Scenario:** Two ADMIN users simultaneously update the same policy.
**Expected:** Both updates succeed (last write wins). TWO audit log entries are created, each with correct old/new values reflecting the state at the time of their respective transactions.

### Edge Case 5: Search Includes Deleted Policies
**Scenario:** Call GET `/api/policies/search?q=John` after soft-deleting a policy held by "John Doe".
**Expected:** Search results do NOT include the deleted policy. The `searchByNameOrHolder` query should ideally exclude `is_deleted=true` records.

## Top 3 Configuration Mistakes for @Scheduled Jobs in Docker

1. **Missing Timezone:** The Docker container may run in UTC while cron expressions assume local time. Ensure `TZ` environment variable is set (e.g., `TZ=America/New_York`) or use UTC-aware cron expressions.

2. **No Thread Pool Configuration:** By default Spring uses a single-threaded scheduler. If a job runs long, subsequent jobs are blocked. Add `spring.task.scheduling.pool.size=5` in `application.yml`.

3. **`@EnableScheduling` Missing:** If the main application class or a configuration class is missing `@EnableScheduling`, jobs will never fire. Verify `ToolApplication.java` has this annotation.

## Manual Weekly Summary Test (Without Waiting a Week)

Change the cron expression temporarily to fire every minute for testing:
```java
@Scheduled(cron = "0 * * * * *")  // Every minute (for testing only)
```
Or use Spring Boot Actuator to trigger manually if available.

Alternatively, write a unit test with `@SpringBootTest` and directly call `schedulerService.generateWeeklySummary()` to verify logic and logging.

## Docker Compose Validation Commands

```bash
# Clean slate test
docker-compose down -v
docker-compose up --build -d

# Verify all services are healthy
docker-compose ps

# Verify 30 seed records
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT COUNT(*) FROM policies;"

# Verify Flyway migrations ran
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT * FROM flyway_schema_history;"

# Check backend logs for scheduled jobs
docker logs policy-backend --tail 100

# Check audit logs after CRUD operations
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT action, changed_by, old_value, new_value FROM audit_log;"
```

## Git Commit

```bash
git add .
git commit -m "Day 13 - Completed full system test and resolved all P1/P2 backend bugs"
git push my-origin sangeeta-work:main --force
```

