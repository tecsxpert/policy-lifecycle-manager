import time
import requests
import statistics

BASE_URL = "http://127.0.0.1:5002"

ENDPOINTS = [
    {
        "name": "home",
        "method": "GET",
        "url": f"{BASE_URL}/"
    }
]

REQUEST_COUNT = 3
TARGET_P95_MS = 2000


def call_endpoint(endpoint):
    start_time = time.time()

    try:
        response = requests.get(endpoint["url"], timeout=10)
        response_time_ms = round((time.time() - start_time) * 1000, 2)

        return {
            "status_code": response.status_code,
            "response_time_ms": response_time_ms,
            "error": None
        }

    except Exception as e:
        response_time_ms = round((time.time() - start_time) * 1000, 2)

        return {
            "status_code": 500,
            "response_time_ms": response_time_ms,
            "error": str(e)
        }


def benchmark_endpoint(endpoint):
    times = []
    statuses = []
    errors = []

    for _ in range(REQUEST_COUNT):
        result = call_endpoint(endpoint)
        times.append(result["response_time_ms"])
        statuses.append(result["status_code"])

        if result["error"]:
            errors.append(result["error"])

    p50 = round(statistics.median(times), 2)
    p95 = round(max(times), 2)
    p99 = round(max(times), 2)

    passed = all(status == 200 for status in statuses) and p95 <= TARGET_P95_MS and not errors

    return {
        "endpoint": endpoint["name"],
        "url": endpoint["url"],
        "total_requests": REQUEST_COUNT,
        "p50_ms": p50,
        "p95_ms": p95,
        "p99_ms": p99,
        "target_p95_ms": TARGET_P95_MS,
        "passed": passed,
        "errors": errors
    }


def run_performance_check():
    results = [benchmark_endpoint(endpoint) for endpoint in ENDPOINTS]

    return {
        "summary": {
            "overall_passed": all(item["passed"] for item in results),
            "total_endpoints": len(results),
            "message": "Performance verification completed"
        },
        "results": results
    }