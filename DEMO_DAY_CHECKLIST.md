# DEMO DAY CHECKLIST — Friday 9 May 2026

**Project:** Tool-29 — Policy Lifecycle Manager  
**My Role:** Java Developer 2 (Sangeeta Badiyappa Rathod)  
**Demo Time:** 8 minutes | **My Segment:** 90 seconds (minutes 3:00–4:30)

---

## Morning of Demo Day — Arrive 1 Hour Early

### Setup (30 minutes before team arrival)
- [ ] Charge laptop to 100% or keep plugged in
- [ ] Close all unnecessary applications (browsers, IDEs, Slack)
- [ ] Disable notifications (Windows Focus Assist / Do Not Disturb)
- [ ] Set display to 1920x1080 for projector compatibility
- [ ] Test HDMI/DisplayPort connection to projector
- [ ] Open Swagger UI in Chrome (not Edge): `http://localhost:8080/swagger-ui/index.html`
- [ ] Open terminal for Flyway and audit log queries
- [ ] Open presentation slides (3 slides: Problem, Architecture, Demo Flow)
- [ ] Print Q&A cheat sheet and place on desk

---

## Clean Slate Test (Must Complete)

```bash
# 1. Wipe everything
docker-compose down -v

# 2. Fresh build
docker-compose up --build -d

# 3. Wait 2-3 minutes for all services healthy
docker-compose ps

# 4. Verify 30 seed records
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT COUNT(*) FROM policies;"
# Expected: 30

# 5. Verify Flyway migrations
docker exec -it policy-postgres psql -U policyuser -d policydb -c "SELECT version, description, success FROM flyway_schema_history;"
# Expected: 5 rows, all success = true

# 6. Verify Swagger loads
curl -s http://localhost:8080/swagger-ui/index.html | head -n 5
# Expected: HTML output

# 7. Verify AI service health
curl -s http://localhost:5000/health
# Expected: "OK" or similar
```

---

## 5 Demo Scenarios — Verify Each One

| # | Scenario | Verification Command | Expected Result |
|---|----------|---------------------|-----------------|
| 1 | **RBAC Enforcement** | Login as VIEWER → DELETE `/api/policies/1` | 403 Forbidden |
| 2 | **Flyway Migrations** | Query `flyway_schema_history` | 5 rows, all success |
| 3 | **Audit Logging** | Create → Update → Query `audit_log` | POLICY_CREATED, POLICY_UPDATED with JSON |
| 4 | **Rate Limiting** | 105 rapid POSTs to `/api/auth/login` | First ~100 = 401, rest = 429 |
| 5 | **CSV Export** | GET `/api/policies/export` | `policies_export.csv` downloaded |

---

## Backup Screenshots (In Case Live Demo Fails)

Save these to `demo_backup_screenshots/` folder:

| Screenshot | File Name |
|------------|-----------|
| Swagger UI with Authorize button | `01_swagger_ui.png` |
| RBAC 403 Forbidden response | `02_rbac_403.png` |
| Flyway schema history table | `03_flyway_migrations.png` |
| Audit log JSON old/new values | `04_audit_log.png` |
| Rate limiting 429 response | `05_rate_limit_429.png` |
| CSV export file contents | `06_csv_export.png` |
| docker-compose ps (all healthy) | `07_docker_compose_ps.png` |

> **Note:** Take these during the morning setup. If Docker fails during the live demo, show screenshots instead.

---

## Team Handoff Cues

| From | To | Exact Words |
|------|-----|-------------|
| **Java Dev 1** | **Me** | *"Now that we're authenticated, let me show what happens when the wrong role tries to do the wrong thing."* |
| **Me** | **Java Dev 3 / Frontend** | *"With our audit trail proven and data integrity guaranteed, let's see the frontend that insurance brokers actually use — handing over to [Name]."* |

---

## Emergency Protocols

| Problem | Immediate Action |
|---------|-----------------|
| Docker won't start | Show backup screenshots. Say: "Here's what the live system looks like when running." |
| JWT token expires | Use refresh endpoint: POST `/api/auth/refresh` with existing token |
| Rate limit blocks demo IP | Restart Docker (`docker-compose down -v && docker-compose up -d`) — resets buckets |
| Audit log query slow | Show pre-captured screenshot instead |
| Swagger UI blank | Open `http://localhost:8080/v3/api-docs` as fallback |
| Internet down (Groq API) | AI team uses backup screenshots — your segment unaffected |

---

## Personal Checklist

- [ ] Wearing professional attire
- [ ] Water bottle on desk
- [ ] Phone on silent
- [ ] Notes printed and on desk
- [ ] Stopwatch or timer visible
- [ ] Breathing exercises completed (3 deep breaths)
- [ ] Smile — you've prepared for 20 days

---

## Post-Demo Actions

1. Thank mentors and audience
2. Collect feedback for post-sprint GitHub issues
3. Take team photo for portfolio
4. Celebrate 🎉

---

## Final Affirmation

> "I have prepared for 20 days. I know every line of code I wrote. I can explain every design decision. I am ready to demonstrate production-grade security and infrastructure. This demo will be excellent."

---

**Document Owner:** Sangeeta Badiyappa Rathod (Java Developer 2)  
**Last Updated:** 2026-05-08 (Day 19)
