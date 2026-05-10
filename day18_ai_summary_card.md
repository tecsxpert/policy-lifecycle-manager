# AI Service Summary Card — Policy Lifecycle Manager

## Project
Policy Lifecycle Manager — AI Microservice

## Role
AI Developer 2

## AI Tech Stack
- Python 3.11
- Flask
- Groq API
- LLaMA 3.1 8B Instant
- ChromaDB
- Sentence Transformers
- Redis Cache Verification
- Docker

## AI Endpoints

### 1. POST /categorise
Classifies policy text into predefined categories such as Security, Compliance, HR, Finance, and Operations.

### 2. POST /generate-report
Generates a professional policy lifecycle report from input policy text.

### 3. GET /generate-report/<job_id>
Checks async report generation job status and returns completed report output.

### 4. GET /health
Returns AI service health, uptime, model info, response metrics, ChromaDB status, and cache stats.

### 5. GET /prompt-qa
Runs prompt QA validation against 30 seeded demo records and verifies demo readiness.

### 6. GET /day16/final-verify
Verifies final AI performance, cache behavior, and fallback handling before demo.

## Key Features
- Real Groq AI responses
- Async report generation
- Fallback handling on AI failure
- Redis/cache verification
- Prompt QA using 30 demo records
- Dockerized AI service
- Demo backup screenshots prepared

## Demo Backup
Backup screenshots are available in:

demo-backup-screenshots/

These screenshots can be used if Groq API fails during live demo.

## GitHub Link
https://github.com/anushards17/Sharadha-Bai-Hs-/tree/final-clean

## Demo Day Note
The AI service supports policy categorisation, report generation, prompt validation, performance verification, caching checks, and fallback safety for reliable live demonstration.