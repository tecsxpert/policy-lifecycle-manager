import requests

BASE_URL = "http://127.0.0.1:5001/query"

test_inputs = [
    "Summarise this policy",
    "Recommend actions for delayed approval",
    "Classify compliance issue",
    "Generate report",
    "Explain audit failure"
]

def call_api(prompt):
    res = requests.post(BASE_URL, json={"prompt": prompt})
    return res.json(), res.status_code


def score_response(data, status):
    if status != 200:
        return 0

    confidence = data.get("meta", {}).get("confidence", 0)

    return confidence * 5


def test_api_quality():
    results = []

    for prompt in test_inputs:
        data, status = call_api(prompt)
        score = score_response(data, status)
        results.append(score)

    avg_score = sum(results) / len(results)

    print("Average Score:", avg_score)

    assert avg_score >= 4