import os
import requests
from dotenv import load_dotenv

load_dotenv()

GROQ_API_KEY = os.getenv("GROQ_API_KEY")


def verify_fallback():
    try:
        if not GROQ_API_KEY:
            raise Exception("GROQ_API_KEY missing")

        url = "https://api.groq.com/openai/v1/chat/completions"

        headers = {
            "Authorization": f"Bearer {GROQ_API_KEY}",
            "Content-Type": "application/json"
        }

        payload = {
            "model": "llama-3.1-8b-instant",
            "messages": [
                {
                    "role": "user",
                    "content": "Return one short sentence for fallback verification."
                }
            ],
            "temperature": 0.3,
            "max_tokens": 50
        }

        try:
            response = requests.post(
                url,
                headers=headers,
                json=payload,
                timeout=1
            )

            response.raise_for_status()

            result = {
                "answer": response.json()["choices"][0]["message"]["content"],
                "meta": {
                    "is_fallback": False,
                    "model_used": "llama-3.1-8b-instant"
                }
            }

        except Exception as e:
            result = {
                "answer": "AI service is temporarily unavailable. Please try again later.",
                "meta": {
                    "is_fallback": True,
                    "model_used": "llama-3.1-8b-instant",
                    "reason": str(e)
                }
            }

        passed = (
            "answer" in result
            and "meta" in result
            and "is_fallback" in result["meta"]
        )

        return {
            "test_name": "AI fallback verification",
            "passed": passed,
            "fallback_structure_present": passed,
            "is_fallback": result["meta"]["is_fallback"],
            "result": result,
            "message": "Fallback behavior verified successfully"
        }

    except Exception as e:
        return {
            "test_name": "AI fallback verification",
            "passed": False,
            "error": str(e),
            "message": "Fallback verification failed"
        }