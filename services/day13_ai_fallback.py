def fallback_response(reason="AI service unavailable"):
    return {
        "answer": "AI service is temporarily unavailable. Please try again later.",
        "meta": {
            "is_fallback": True,
            "reason": reason,
            "model_used": "llama-3.1-8b-instant",
            "confidence": 0.0
        }
    }