# Policy Lifecycle Manager - Docker Testing Guide

## Overview

This document provides comprehensive instructions for running the Policy Lifecycle Manager system using Docker Compose and testing all functionality.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose v2.0+
- Maven 3.8+ (for local builds)
- Java 17+ (for local development)

## Quick Start

### 1. Start All Services

```bash
# Start all services (MySQL, Redis, Backend)
docker compose up --build

# Or run in background
docker compose up --build -d
```

**Expected Output:**
- MySQL container: `policy-mysql` running on port 3307
- Redis container: `policy-redis` running on port 6379
- Backend container: `policy-backend` running on port 8080

### 2. Access the Application

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Base URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console (if enabled)

## Docker Compose Commands

### Start Services

```bash
# Build and start all services
docker compose up --build

# Start without rebuilding
docker compose up

# Start in background (detached mode)
docker compose up -d
```

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
