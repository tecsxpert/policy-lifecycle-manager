# Policy Lifecycle Manager — Backend API

Java 17 • Spring Boot 3.x • PostgreSQL 15 • Spring Security 6

## API Endpoints

### Authentication (`/api/auth`)
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST   | `/api/auth/register` | Register a new user (role defaults to `VIEWER`) | Public |
| POST   | `/api/auth/login`    | Authenticate and receive JWT | Public |
| POST   | `/api/auth/refresh`  | Refresh an expired JWT | Public |

### Policies (`/api/policies`)
| Method | Endpoint | Description | RBAC |
|--------|----------|-------------|------|
| PUT    | `/api/policies/{id}` | Update an existing policy | `ADMIN`, `MANAGER` |
| DELETE | `/api/policies/{id}` | Soft-delete a policy (sets status to `DELETED`) | `ADMIN` |
| GET    | `/api/policies/search?q={term}` | Search policies by name or holder | `ADMIN`, `MANAGER`, `VIEWER` |
| GET    | `/api/policies/stats` | Get total and active policy counts | `ADMIN`, `MANAGER`, `VIEWER` |

### Swagger UI
Once the application is running, open:
```
http://localhost:8080/swagger-ui/index.html
```

## Database Migrations (Flyway)
| File | Purpose |
|------|---------|
| `V1__init.sql` | Creates `policies` table with indexes on `status` and `policy_holder` |
| `V2__audit.sql` | Creates `audit_log` table with composite index |
| `V3__roles.sql` | Creates `roles` table and seeds `ADMIN`, `MANAGER`, `VIEWER` |

## Background Tasks
| Schedule | Task |
|----------|------|
| Daily 1:00 AM | Find overdue policies (`status != 'COMPLETED'` and `expiry_date` in the past) |
| Daily 2:00 AM | Find policies expiring in exactly 7 days |
| Monday 9:00 AM | Weekly summary (total, active, pending counts) |

## Security
- Passwords hashed with BCrypt
- JWT-based authentication
- Method-level RBAC via `@PreAuthorize`
- Audit logging via Spring AOP (`@Aspect`)
