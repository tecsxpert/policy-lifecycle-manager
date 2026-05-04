import requests
import day6_inputs

# FastAPI endpoint
URL = "http://127.0.0.1:8000/query"

test_questions = day6_inputs.test_questions

print("\n🚀 DAY 6 PROMPT EVALUATION STARTED\n")

total_score = 0

for i, q in enumerate(test_questions, 1):
    try:
        response = requests.post(URL, json={"question": q})
        data = response.json()

        answer = data.get("answer", "No answer returned")

        # -------------------------
        # 🔥 SIMPLE SCORING SYSTEM
        # -------------------------
        score = 0

        if len(answer) > 150:
            score += 3
        if "AI" in answer or "machine learning" in answer.lower():
            score += 2
        if "FastAPI" in answer or "ChromaDB" in answer:
            score += 2
        if data.get("sources"):
            score += 3

        total_score += score

        print(f"\n======================")
        print(f"Q{i}: {q}")
        print(f"A: {answer}")
        print(f"⭐ Score: {score}/10")

    except Exception as e:
        print(f"\n❌ Error for question: {q}")
        print("Error:", str(e))

print("\n======================")
print(f"🏁 FINAL SCORE: {total_score}/100")
print("\n✅ EVALUATION COMPLETE")