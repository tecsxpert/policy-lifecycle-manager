# Day 18 — Fresh Machine Test

## Deliverables Checklist
- [ ] Fresh machine test: clone repo, create .env, docker-compose up --build, full stack healthy in under 3 min
- [ ] Verify 30 seed records loaded
- [ ] Verify all 5 Flyway migrations applied
- [ ] Verify Swagger UI accessible at http://localhost:8080/swagger-ui/index.html
- [ ] Verify all 5 Docker services healthy
- [ ] Git commit & push Day 18 updates

## Implementation Steps

### Phase 1 — Prepare Environment
- [ ] Copy .env.example to .env
- [ ] Set POSTGRES_PASSWORD to a strong password
- [ ] Set JWT_SECRET (generate with openssl rand -base64 32)
- [ ] Verify Docker and Docker Compose installed

### Phase 2 — Execute Fresh Machine Test
```bash
# Step 1: Clean slate
docker-compose down -v

# Step 2: Fresh build
docker-compose up --build -d

# Step 3: Wait for health (up to 3 minutes)
docker-compose ps

# Step 4: Verify seed data
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT COUNT(*) FROM policies;"
# Expected: 30

# Step 5: Verify Flyway migrations
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT version, description, success FROM flyway_schema_history;"
# Expected: 5 rows, all success = true

# Step 6: Verify Swagger loads
curl -s http://localhost:8080/swagger-ui/index.html | head -n 5

# Step 7: Verify AI service health
curl -s http://localhost:5000/health
```

### Phase 3 — Verify Demo Scenarios
- [ ] Verify RBAC: Login as VIEWER → DELETE /api/policies/1 → 403 Forbidden
- [ ] Verify Flyway: 5 migrations applied successfully
- [ ] Verify Audit Logging: Create → Update → Query audit_log shows old/new JSON
- [ ] Verify Rate Limiting: 105 rapid POSTs → First ~100 = 401, rest = 429
- [ ] Verify CSV Export: GET /api/policies/export returns CSV file

### Phase 4 — Timing Target
- [ ] Verify full stack healthy in under 3 minutes (target: <= 180 seconds)

### Phase 5 — Git
- [ ] Commit with: "Day 18 - Fresh machine test completed, all services healthy"
- [ ] Push to update branch

## Environment Variables Required
| Variable | Description | Example |
|----------|-------------|---------|
| POSTGRES_DB | Database name | policydb |
| POSTGRES_USER | Database user | policyuser |
| POSTGRES_PASSWORD | Strong password | P@ssw0rd!2026 |
| POSTGRES_PORT | Host port | 5432 |
| REDIS_PORT | Host port | 6379 |
| AI_PORT | Host port | 5000 |
| BACKEND_PORT | Host port | 8080 |
| FRONTEND_PORT | Host port | 80 |
| JWT_SECRET | 256-bit secret | (generate with openssl) |

## Known Issues (P3 — documented, not fixed now)
| # | Issue | Why deferred |
|---|-------|-------------|
| P3-1 | Frontend is a static placeholder (no actual CRUD UI) | Demo focuses on backend API; frontend will be shown as architecture-only |
| P3-2 | AI service (ai/app.py) is a mock stub | AI integration is Q2 enhancement; out of scope for sprint |
| P3-3 | Rate limiting is in-memory only (no Redis backed) | DOCUMENTED in SECURITY.md Issue #1; requires Redis integration post-sprint |

## Success Criteria
- [ ] All 5 Docker services show "healthy" or "Up" status
- [ ] 30 seed records visible in policies table
- [ ] 5 Flyway migrations applied successfully
- [ ] Swagger UI accessible at http://localhost:8080/swagger-ui/index.html
- [ ] Full stack starts in under 3 minutes

## Notes
- If Docker fails to start, verify .env file has correct values
- Verify Docker Desktop is running on Windows
- Check port availability if services fail to bind
