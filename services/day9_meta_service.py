import time

def build_ai_response(prompt: str, cached: bool = False):
    start_time = time.time()

    answer = f"You asked: {prompt}"

    response_time_ms = int((time.time() - start_time) * 1000)

    return {
        "success": True,
        "data": {
            "answer": answer
        },
        "meta": {
            "confidence": 0.95,
            "model_used": "llama-3.3-70b",
            "tokens_used": len(prompt.split()) * 5,
            "response_time_ms": response_time_ms,
            "cached": cached
        }
    }