import time
import requests
import statistics

BASE_URL = "http://127.0.0.1:5000"

endpoints = [
    "/test-report"
]

results = []

for endpoint in endpoints:

    times = []

    print(f"\nTesting {endpoint}")

    for i in range(50):

        start = time.time()

        response = requests.get(BASE_URL + endpoint)

        end = time.time()

        response_time = (end - start) * 1000

        times.append(response_time)

        print(f"Request {i+1}: {response_time:.2f} ms")

    times.sort()

    p50 = statistics.median(times)
    p95 = times[int(0.95 * len(times)) - 1]
    p99 = times[int(0.99 * len(times)) - 1]

    result = {
        "endpoint": endpoint,
        "p50_ms": round(p50, 2),
        "p95_ms": round(p95, 2),
        "p99_ms": round(p99, 2)
    }

    results.append(result)

print("\nFINAL RESULTS\n")

for result in results:
    print(result)