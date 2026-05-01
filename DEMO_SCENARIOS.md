# Demo Day Scenarios — Java Developer 2 (Sangeeta Badiyappa Rathod)

**Version:** release/v1.0 | **Date:** 2026-05-07 (Day 18)  
**Total Demo Time:** 8 minutes | **My Segment:** 90 seconds (minutes 3–4.5)

---

## Scenario 1: RBAC Enforcement — VIEWER Cannot Delete (0:00–0:20)

**Setup:** Swagger UI at `http://localhost:8080/swagger-ui/index.html`

**Steps:**
1. POST `/api/auth/register` with body:
   ```json
   {"username":"demoviewer","password":"viewer123","email":"viewer@demo.com"}
   ```
   → Expected: 201 Created, role = `VIEWER`
2. POST `/api/auth/login` with same credentials
   → Expected: 200 OK with JWT token
3. Copy token → click "Authorize" → paste `Bearer <token>`
4. POST `/api/policies/create` with:
   ```json
   {"policyName":"Demo RBAC Policy","policyType":"Life","status":"Active","policyHolder":"Test Holder","expiryDate":"2026-12-31"}
   ```
   → Expected: 201 Created (VIEWER *can* create — wait, no! VIEWER cannot create!)
   
   **Correction:** Step 4 should be done by ADMIN first, then:
5. GET `/api/policies/all` → note any policy ID
6. DELETE `/api/policies/{id}` as VIEWER
   → Expected: **403 Forbidden**

**Narration:** "Watch what happens when a VIEWER tries to delete. 403 Forbidden. Only ADMIN can delete. MANAGERS can create and update. VIEWERS can only read and search. Enforced at the method level with Spring Security @PreAuthorize."

---

## Scenario 2: Flyway Migrations & Seed Data (0:20–0:35)

**Setup:** Terminal with Docker running

**Steps:**
1. Run: `docker-compose down -v` (clean slate)
2. Run: `docker-compose up --build -d`
3. Wait 30 seconds for all services healthy
4. Run: `docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT COUNT(*) FROM policies;"`
   → Expected: `30`
5. Run: `docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT * FROM flyway_schema_history;"`
   → Expected: 5 rows (V1 through V5)

**Narration:** "Our schema is version-controlled with Flyway. Five migrations run automatically on startup. Thirty seed records are present the moment the container comes up. Reproducible across dev, staging, and production."

---

## Scenario 3: Audit Logging — Every Mutation Captured (0:35–0:55)

**Setup:** Swagger UI, logged in as ADMIN

**Steps:**
1. POST `/api/auth/login` as `admin` / `admin123`
2. POST `/api/policies/create`:
   ```json
   {"policyName":"Before Update","policyType":"Auto","status":"Active","policyHolder":"Audit Test","expiryDate":"2026-06-30"}
   ```
   → Capture the returned `id`
3. PUT `/api/policies/{id}`:
   ```json
   {"policyName":"After Update","policyType":"Auto","status":"Pending","policyHolder":"Audit Test Updated","expiryDate":"2027-01-01"}
   ```
4. Query audit log in database:
   ```bash
   docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT action, changed_by, old_value->>'policyName', new_value->>'policyName' FROM audit_log WHERE action='POLICY_UPDATED' ORDER BY change_date DESC LIMIT 1;"
   ```
   → Expected: `POLICY_UPDATED`, `admin`, `Before Update`, `After Update`

**Narration:** "Every mutation is automatically audited. Here I updated a policy name — and the audit log captured the exact before-and-after state as JSON. Even if Jackson serialization fails, we store an error placeholder instead of crashing."

---

## Scenario 4: Rate Limiting — 429 on Abuse (0:55–1:15)

**Setup:** Terminal with `curl`

**Steps:**
1. Run rapid-fire auth requests:
   ```bash
   for i in {1..105}; do curl -s -o /dev/null -w "%{http_code} " http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"wrong"}'; done
   ```
2. Watch for `429` responses after the 100th request
3. Verify header: `Retry-After` is present in 429 response

**Alternative (single command):**
```bash
curl -i http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"username":"x","password":"y"}'
# Repeat 105 times — expect 429 after ~100
```

**Narration:** "Auth endpoints are rate-limited to 100 requests per minute per IP. Exceed that and you get HTTP 429 with a Retry-After header. This prevents brute-force attacks without impacting legitimate users."

---

## Scenario 5: Input Sanitization & CSV Export (1:15–1:30)

**Setup:** Swagger UI or browser

**Steps:**
1. POST `/api/policies/create` as ADMIN:
   ```json
   {"policyName":"XSS<script>alert(1)</script>Test","policyType":"Home","status":"Active","policyHolder":"<b>Bold</b> User","expiryDate":"2026-12-31"}
   ```
   → Expected: 201 Created, but stored as `XSSscriptalert(1)scriptTest` and `bBoldb User` (tags stripped)
2. GET `/api/policies/search?q=XSSscript`
   → Expected: Policy found with sanitized name
3. GET `/api/policies/export`
   → Expected: `policies_export.csv` downloaded with all non-deleted policies
4. Open CSV → verify XSS payload was NOT stored

**Narration:** "All user inputs are sanitized before they reach the database. HTML tags are stripped, preventing XSS attacks. And here is the CSV export — all non-deleted policies formatted for Excel import."

---

## Backup Scenario: Soft Delete Integrity (if time allows)

**Steps:**
1. DELETE `/api/policies/{id}` as ADMIN
2. GET `/api/policies/all` → verify policy NOT in list
3. Query DB directly:
   ```bash
   docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT id, policy_name, is_deleted, status FROM policies WHERE id={id};"
   ```
   → Expected: `is_deleted=true`, `status=DELETED`

**Narration:** "Soft delete means data is never truly lost. The flag is set, status changes to DELETED, but the audit trail and original record remain for compliance."

---

## Demo Day Checklist (Before Going On Stage)

- [ ] Docker Desktop is running
- [ ] `docker-compose down -v && docker-compose up --build -d` completed successfully
- [ ] All services healthy: `docker-compose ps`
- [ ] 30 seed records confirmed: `SELECT COUNT(*) FROM policies = 30`
- [ ] Swagger UI loads: `http://localhost:8080/swagger-ui/index.html`
- [ ] JWT login works for admin / viewer test accounts
- [ ] Backup screenshots of each scenario saved to `demo_backup_screenshots/` folder
- [ ] Internet connection stable (for Groq API calls by AI team)
- [ ] Laptop battery > 80% or plugged in
- [ ] Presentation slides loaded and tested
- [ ] Team handoff cues rehearsed

---

## Transition Script

**From Java Dev 1 (Auth):**
> "Thanks [Dev 1]. Now that we're authenticated, let me show what happens when the wrong role tries to do the wrong thing."

**To Java Dev 3 / Frontend:**
> "With our audit trail proven and data integrity guaranteed, let's see the frontend that insurance brokers actually use — handing over to [Name]."

---

**Document Owner:** Sangeeta Badiyappa Rathod (Java Developer 2)  
**Last Updated:** 2026-05-07 (Day 18)
