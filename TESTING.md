# Docker Testing & Validation Guide

**Policy Lifecycle Manager** - Full Docker Compose containerization with MySQL, Redis, and Spring Boot backend.

---

## Quick Start

### Start All Services (MySQL, Redis, Backend)
```bash
docker compose up --build
```

Expected output:
```
✔ Container policy-mysql     Healthy
✔ Container policy-redis     Healthy
✔ Container policy-backend   Started
```

### Stop All Services
```bash
docker compose down
```

### Stop All Services & Remove Volumes
```bash
docker compose down -v
```

### View Running Containers
```bash
docker ps --filter "name=policy"
```

---

## Port Mappings

| Service | Internal Port | Host Port | URL |
|---------|---------------|-----------|-----|
| Backend | 8080 | 8080 | http://localhost:8080 |
| MySQL | 3306 | 3307 | localhost:3307 |
| Redis | 6379 | 6379 | localhost:6379 |

---

## Application Testing

### 1. Swagger/OpenAPI Testing

#### Access Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

**Verify:**
- Swagger page loads
- All API endpoints visible
- No YAML parsing errors
- API documentation displays correctly

---

### 2. JWT Authentication Testing

#### Generate JWT Token
```bash
curl -X GET "http://localhost:8080/auth/login?username=admin" \
  -H "accept: application/json"
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3ODU3Nzg1OCwiZXhwIjoxNzc4NTk0Mjg5fQ..."
}
```

#### Test Protected API with JWT
```bash
TOKEN="your_jwt_token_here"
curl -X GET "http://localhost:8080/policies?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "accept: application/json"
```

**Expected Response:** 200 OK with policy data

#### Test Unauthorized Access (without JWT)
```bash
curl -X GET "http://localhost:8080/policies?page=0&size=5"
```

**Expected Response:** 401 Unauthorized

**Verify:**
- JWT token generation works
- Protected endpoints require Bearer token
- Unauthorized requests return 401
- All endpoints accessible with valid token

---

### 3. Policy APIs Testing

#### Get All Policies (Paginated)
```bash
TOKEN="your_jwt_token"
curl -X GET "http://localhost:8080/policies?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

#### Get Policy by ID
```bash
TOKEN="your_jwt_token"
curl -X GET "http://localhost:8080/policies/1" \
  -H "Authorization: Bearer $TOKEN"
```

#### Create New Policy
```bash
TOKEN="your_jwt_token"
curl -X POST "http://localhost:8080/policies" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "POL999",
    "policyName": "Test Policy",
    "policyType": "Health",
    "premiumAmount": 5000,
    "startDate": "2026-01-01",
    "endDate": "2027-01-01",
    "status": "ACTIVE"
  }'
```

**Verify:**
- GET /policies returns paginated results
- GET /policies/{id} returns single policy
- POST /policies creates new policy
- All policies stored in MySQL
- 30 seeded policies in database

---

### 4. Redis Caching Testing

#### Verify Redis Connection
```bash
docker exec policy-redis redis-cli ping
```

**Expected Response:** `PONG`

#### Check Redis Keys (Cache Entries)
```bash
docker exec policy-redis redis-cli KEYS "*"
```

#### Monitor Cache Performance
1. Make first request to `/policies/1` (DB hit)
2. Make second request to `/policies/1` (cache hit - should be faster)
3. Check backend logs for cache activity

**Expected Behavior:**
- First request: Database query executed
- Second request: Data served from Redis cache
- 10-minute TTL (600000 ms) per configuration
- @Cacheable and @CacheEvict annotations active

#### View Cache Configuration
```bash
# Check cache TTL in application.yml
cat backend/src/main/resources/application.yml | grep -A 2 "redis:"
```

**Expected Output:**
```yaml
cache:
  type: redis
  redis:
    time-to-live: 600000  # 10 minutes
```

---

### 5. MySQL Connection & Data Testing

#### Access MySQL Container
```bash
docker exec -it policy-mysql mysql -u root -p
Password: root
```

#### Verify Database & Schema
```sql
USE policydb;
SHOW TABLES;
SELECT COUNT(*) as policy_count FROM policies;
```

**Expected Results:**
- Database: `policydb` exists
- Tables: `policies`, `file_metadata`, other schema tables
- Records: 30 demo policies seeded

#### Check Seeded Data Sample
```sql
SELECT policy_number, policy_name, status FROM policies LIMIT 5;
```

#### Verify Connection String
- Inside containers: `jdbc:mysql://mysql:3306/policydb`
- Host machine: `jdbc:mysql://localhost:3307/policydb`
- Username: `root`
- Password: `root`

**Verify:**
- MySQL container accessible
- Database credentials working
- 30 policies seeded successfully
- Container-to-container communication via hostname

---

### 6. File Upload Testing

#### Upload File via API
```bash
TOKEN="your_jwt_token"
curl -X POST "http://localhost:8080/files/upload" \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@path/to/file.pdf"
```

#### Verify Uploaded Files in Container
```bash
docker exec policy-backend ls -la /app/uploads/files
```

**Expected Output:** Files with UUID-based naming

**Verify:**
- UUID-based filename generation working
- Files persisted in volume
- File size validation preserved
- Upload directory exists in container

---

## Unit & Integration Tests

### Run All Tests
```bash
cd backend
mvn test
```

**Expected Output:**
```
[INFO] Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Test Classes Included
- `JwtUtilTest` - JWT token generation & validation
- `AuthControllerTest` - Authentication endpoints
- `PolicyServiceImplTest` - Policy business logic
- `PolicyControllerIntegrationTest` - API integration
- `PolicyRepositoryTest` - Database operations

---

## Container Verification

### Check Container Status
```bash
docker ps -a --filter "name=policy"
```

### View Container Logs
```bash
docker logs policy-backend --tail 50
docker logs policy-mysql --tail 20
docker logs policy-redis --tail 20
```

---

## Rebuild & Redeploy

### Rebuild Backend After Code Changes
```bash
cd backend
mvn clean package -DskipTests
```

### Full Rebuild from Scratch
```bash
docker compose down -v
docker compose up --build
```

---

## Troubleshooting

### Backend Container Won't Start
```bash
docker logs policy-backend --tail 100
```

### MySQL Connection Failed
```bash
docker exec policy-mysql mysqladmin ping -h localhost -u root -proot
```

### Redis Cache Not Working
```bash
docker exec policy-redis redis-cli ping
```

---

**Verification Status:** ✅ All Systems Operational
- Docker containers: Running with health checks
- Database: 30 policies seeded
- JWT authentication: Active
- Redis caching: Active (10-minute TTL)
- File uploads: Working with UUID naming
- Unit tests: 23/23 passing
- Swagger API: Accessible

### Stop Services

```bash
# Stop all services (keeps volumes and data)
docker compose stop

# Stop and remove containers (keeps volumes)
docker compose down

# Stop and remove everything including volumes
docker compose down -v

# Stop and remove everything including images
docker compose down -rmi all
```

### View Logs

```bash
# View all logs
docker compose logs -f

# View specific service logs
docker compose logs -f backend
docker compose logs -f mysql
docker compose logs -f redis

# View last 100 lines
docker compose logs --tail=100 backend
```

### Restart Services

```bash
# Restart all services
docker compose restart

# Restart specific service
docker compose restart backend
```

## Verify Containers

### Check Running Containers

```bash
docker ps
```

**Expected Output:**
```
CONTAINER ID   IMAGE                                   NAMES
xxxxx          policy-lifecycle-manager:latest         policy-backend
xxxxx          mysql:8                                 policy-mysql
xxxxx          redis:7                                 policy-redis
```

### Check Container Status

```bash
# View container details
docker compose ps

# View container logs
docker compose logs backend
```

## Testing Guide

### 1. Swagger/OpenAPI Testing

1. Open: http://localhost:8080/swagger-ui/index.html
2. Click **Authorize** button in top-right
3. Paste JWT token: `eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3ODUwNzg4OSwiZXhwIjoxNzc4NTk0Mjg5fQ.3lRqm1WgG9MBsBtCo57vdbw5WoDeAK2_11Eg81h7VsE`
4. Try API endpoints

### 2. JWT Authentication Testing

#### Generate JWT Token

```bash
curl -X GET "http://localhost:8080/auth/login?username=admin"
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3ODUwNzg4OSwiZXhwIjoxNzc4NTk0Mjg5fQ.3lRqm1WgG9MBsBtCo57vdbw5WoDeAK2_11Eg81h7VsE"
}
```

#### Test Protected API (with JWT)

```bash
curl -X GET "http://localhost:8080/policies" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3ODUwNzg4OSwiZXhwIjoxNzc4NTk0Mjg5fQ.3lRqm1WgG9MBsBtCo57vdbw5WoDeAK2_11Eg81h7VsE"
```

#### Test Unauthorized Access (without JWT)

```bash
curl -X GET "http://localhost:8080/policies"
```

**Expected Response:** `401 Unauthorized`

### 3. MySQL Database Testing

#### Connect to MySQL Container

```bash
docker compose exec mysql mysql -u root -proot -D policydb
```

#### View Tables

```sql
SHOW TABLES;
DESC policies;
SELECT COUNT(*) FROM policies;
```

#### Exit MySQL CLI

```
exit
```

### 4. Redis Cache Testing

#### Connect to Redis Container

```bash
docker compose exec redis redis-cli
```

#### Check Cache Status

```
KEYS *
DBSIZE
INFO
```

#### View Cache Entry

```
GET "policies::1"
TTL "policies::1"
```

#### Clear All Cache

```
FLUSHALL
```

#### Exit Redis CLI

```
exit
```

### 5. File Upload Testing

#### Test Upload Endpoint

```bash
curl -X POST "http://localhost:8080/files/upload" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.pdf"
```

**Expected Response:**
```json
{
  "filename": "uuid-generated-filename.pdf",
  "uploadTime": "2026-05-12T11:00:00",
  "size": 12345
}
```

#### Verify File Storage

```bash
# Access uploaded files from container
docker compose exec backend ls -la /app/uploads/files/

# Copy file from container
docker compose cp policy-backend:/app/uploads/files/yourfile.pdf ./
```

### 6. Create Policy Testing

#### Create a New Policy

```bash
curl -X POST "http://localhost:8080/policies" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "POL-2026-001",
    "policyName": "Health Insurance",
    "policyType": "HEALTH",
    "premiumAmount": 5000.00,
    "startDate": "2026-05-12",
    "endDate": "2027-05-12",
    "status": "ACTIVE"
  }'
```

#### Retrieve All Policies

```bash
curl -X GET "http://localhost:8080/policies" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get Policy by ID

```bash
curl -X GET "http://localhost:8080/policies/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 7. Cache Verification

1. First API call (DB hit):
   ```bash
   curl -X GET "http://localhost:8080/policies" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```
   - Check logs: should see SQL query executed

2. Second API call (Cache hit):
   ```bash
   curl -X GET "http://localhost:8080/policies" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```
   - Check logs: should NOT see SQL query (served from Redis)

3. Clear Cache and Verify:
   ```bash
   docker compose exec redis redis-cli FLUSHALL
   ```
   - Next call should hit DB again

## Environment Variables

The backend container uses these environment variables:

| Variable | Value | Purpose |
|----------|-------|---------|
| `DB_URL` | `jdbc:mysql://mysql:3306/policydb` | MySQL connection URL |
| `DB_USERNAME` | `root` | MySQL user |
| `DB_PASSWORD` | `root` | MySQL password |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | JDBC driver |
| `REDIS_HOST` | `redis` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `MAIL_USERNAME` | `yourmail@gmail.com` | Email account |
| `MAIL_PASSWORD` | `yourpassword` | Email password |
| `JWT_SECRET` | `yourjwtsecretkey...` | JWT signing key |
| `SERVER_PORT` | `8080` | Application port |

**To modify variables**, edit `docker-compose.yml` and restart services:
```bash
docker compose up --build
```

## Network Communication

Services communicate via the `policy-network` bridge network:

- `backend` → `mysql:3306` (database)
- `backend` → `redis:6379` (cache)
- Host → `backend:8080` (API access)
- Host → `mysql:3307` (direct DB access)
- Host → `redis:6379` (direct cache access)

**Note:** Services use internal hostnames (`mysql`, `redis`), NOT `localhost`.

## Persistent Volumes

| Volume | Mount Point | Purpose |
|--------|------------|---------|
| `mysql-data` | `/var/lib/mysql` | MySQL database files |
| `redis-data` | `/data` | Redis persistence |
| `uploads-data` | `/app/uploads` | File uploads |

**Inspect Volumes:**
```bash
docker volume ls
docker volume inspect policy-lifecycle-manager_mysql-data
```

## Troubleshooting

### Container Won't Start

1. Check logs:
   ```bash
   docker compose logs backend
   ```

2. Verify dependencies started:
   ```bash
   docker compose ps
   ```

3. Check health status:
   ```bash
   docker compose exec backend curl http://localhost:8080/swagger-ui/index.html
   ```

### Database Connection Failed

1. Verify MySQL is running:
   ```bash
   docker compose logs mysql
   ```

2. Test MySQL connectivity:
   ```bash
   docker compose exec backend bash
   nc -zv mysql 3306
   ```

### Redis Connection Failed

1. Verify Redis is running:
   ```bash
   docker compose logs redis
   ```

2. Test Redis connectivity:
   ```bash
   docker compose exec backend bash
   nc -zv redis 6379
   ```

### Port Already in Use

```bash
# Check which process uses the port
netstat -ano | findstr :8080

# Kill the process (Windows)
taskkill /PID <PID> /F

# Or use different ports in docker-compose.yml
# Change "8080:8080" to "8081:8080"
```

### Out of Memory

```bash
# Check Docker resource usage
docker stats

# Increase Docker Desktop memory limit in settings
```

## Full Reset

**WARNING:** This deletes all data!

```bash
# Stop and remove everything
docker compose down -v

# Remove images
docker rmi policy-lifecycle-manager:latest mysql:8 redis:7

# Clean up dangling volumes
docker volume prune -f

# Rebuild and restart
docker compose up --build
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Build and Test with Docker
  run: |
    docker compose build
    docker compose up -d
    sleep 10
    docker compose exec -T backend mvn test
    docker compose down -v
```

## Production Deployment

For production, consider:

1. **Use environment file:**
   ```bash
   docker compose --env-file .env.prod up
   ```

2. **Add secrets management:**
   - Use Docker secrets for passwords
   - Rotate JWT keys regularly
   - Use managed database services

3. **Enable logging:**
   ```yaml
   logging:
     driver: "json-file"
     options:
       max-size: "10m"
       max-file: "3"
   ```

4. **Add resource limits:**
   ```yaml
   resources:
     limits:
       cpus: '1'
       memory: 512M
   ```

## Health Checks

All services have health checks. Monitor status:

```bash
# View health status
docker compose ps

# Check detailed health status
docker inspect policy-backend | grep -A 10 '"Health"'
```

## Performance Tips

1. **Build once, run many:**
   ```bash
   docker compose build
   docker compose up -d
   ```

2. **Use named volumes for persistence**
3. **Enable query caching in application.yml**
4. **Monitor logs for slow queries:**
   ```bash
   docker compose logs -f mysql | grep "Query_time"
   ```

## Support

For issues, check:
- Application logs: `docker compose logs -f backend`
- Database logs: `docker compose logs -f mysql`
- Cache logs: `docker compose logs -f redis`
- Docker Desktop logs
- System resource availability

---

**Last Updated:** May 12, 2026
**Version:** 1.0
**Status:** Production Ready
