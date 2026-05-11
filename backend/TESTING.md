# Policy Lifecycle Manager - System Testing

This document outlines comprehensive testing procedures for the Policy Lifecycle Manager backend system.

## Prerequisites

- Spring Boot application running on `http://localhost:8080`
- H2 database configured (in-memory)
- Test files: `test.pdf` and `test.txt` in the project root
- REST Client (e.g., VS Code REST Client extension) for executing `test.http`

## Authentication Testing

### Login Endpoint
- **Endpoint**: `GET /auth/login?username=admin`
- **Expected**: Returns JWT token in response
- **Test**: Execute login request and verify token is returned

### Protected Routes
- All `/policies` and `/files` endpoints require `Authorization: Bearer <token>` header
- **Test**: Attempt access without token → expect 401 Unauthorized

### Unauthorized Access
- **Test**: `GET /policies` without JWT → 401 response

## Policy API Testing

### CRUD Operations
- **GET /policies**: Retrieve all policies (cached)
- **GET /policies/{id}**: Retrieve specific policy
- **POST /policies**: Create new policy with valid data
- **PUT /policies/{id}**: Update existing policy (if implemented)
- **DELETE /policies/{id}**: Delete policy (if implemented)

### Validation Behavior
- **Invalid Status**: POST with `status: "WRONG"` → 400 Bad Request
- **Invalid ID**: GET `/policies/999` → 404 Not Found
- **Missing Fields**: POST with incomplete data → 400 Bad Request
- **Duplicate Policy Number**: POST with existing policy number → 400 Bad Request (if validation added)

## Cache Testing

### Cache Verification
- **First Request**: `GET /policies` → Hits database (check logs: "Fetching from DB...")
- **Second Request**: `GET /policies` → Uses cache (no DB log)
- **Cache Eviction**: After POST create/update/delete → Cache cleared on next GET

## Email Testing

### Email Notifications
- **Policy Creation**: POST `/policies` → Email sent to configured address
- **Verification**: Check email inbox for "Policy Created" notification
- **Note**: Email service uses Gmail SMTP (credentials in `application.yml`)

## File Upload Testing

### Upload Success
- **Endpoint**: `POST /files/upload`
- **File**: Valid PDF file
- **Expected**: 200 OK with file metadata

### Invalid File Rejection
- **File**: Non-PDF file (e.g., TXT)
- **Expected**: 400 Bad Request

### Download
- **Endpoint**: `GET /files/{id}`
- **Expected**: File download with correct content-type

### Preview
- **Endpoint**: `GET /files/preview/{id}`
- **Expected**: File preview (inline display)
- **Note**: Requires JWT token even in browser

## Exception Handling

### HTTP Status Codes
- **400 Bad Request**: Invalid input data, wrong status, invalid file type
- **401 Unauthorized**: Missing/invalid JWT token
- **404 Not Found**: Non-existent resource ID
- **500 Internal Server Error**: Unexpected exceptions

### Error Response Format
All errors return JSON:
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "message": "Error description",
  "status": 400
}
```

## Security Tests

### JWT Token Validation
- **Expired Token**: Use expired token → 401
- **Invalid Token**: Malformed token → 401
- **Missing Token**: No Authorization header → 401

### File Upload Security
- **Unauthorized Upload**: POST without token → 401
- **File Type Validation**: Only PDF allowed

## Known Behaviors

- **Preview in Browser**: `/files/preview/{id}` requires JWT token; direct browser access returns 401
- **Cache Logs**: PolicyService logs "Fetching from DB..." only on cache miss
- **Email Configuration**: Uses hardcoded Gmail credentials; replace for production
- **Database**: H2 in-memory; data resets on restart
- **File Storage**: Files stored in `backend/uploads/files/` directory

## Testing Observations

- All endpoints properly secured with JWT
- Validation works for policy status enum
- Cache implementation reduces DB hits on repeated requests
- File upload restricts to PDF only
- Global exception handler provides consistent error responses
- Email service sends notifications on policy creation