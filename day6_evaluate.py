from fastapi import APIRouter
import requests
import day6_inputs

router = APIRouter()

URL = "http://127.0.0.1:8000/query"


@router.get("/evaluate")
def evaluate():
    results = []

    for q in day6_inputs.test_questions:
        res = requests.post(URL, json={"question": q}).json()
        answer = res.get("answer", "")

        # scoring logic
        score = 0
        if len(answer) > 150:
            score += 3
        if "AI" in answer or "machine learning" in answer.lower():
            score += 2
        if res.get("sources"):
            score += 3
        if "FastAPI" in answer or "ChromaDB" in answer:
            score += 2

        results.append({
            "question": q,
            "answer": answer,
            "score": score
        })

    return {
        "total_questions": len(results),
        "results": results
    }