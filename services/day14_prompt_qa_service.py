import time
from services.day14_groq_client import call_day14_groq
from utils.day14_demo_records import DEMO_RECORDS
from utils.day14_prompt_templates import PROMPTS


def is_demo_ready(output):
    if not output:
        return False

    bad_words = [
        "error",
        "failed",
        "i cannot",
        "i'm sorry",
        "as an ai language model"
    ]

    lower_output = output.lower()

    for word in bad_words:
        if word in lower_output:
            return False

    return len(output.strip()) > 30


def run_prompt_qa():
    results = []

    for record in DEMO_RECORDS:
        for prompt_name, prompt_template in PROMPTS.items():
            start_time = time.time()
            prompt = prompt_template.format(text=record["text"])

            try:
                ai_output = call_day14_groq(prompt)
                response_time_ms = round((time.time() - start_time) * 1000, 2)

                ready = is_demo_ready(ai_output)

                results.append({
                    "record_id": record["id"],
                    "prompt_name": prompt_name,
                    "input": record["text"],
                    "output": ai_output,
                    "demo_ready": ready,
                    "response_time_ms": response_time_ms,
                    "meta": {
                        "is_fallback": False,
                        "model_used": "llama-3.1-8b-instant"
                    }
                })

            except Exception as e:
                fallback_output = "AI service is temporarily unavailable. Please try again later."

                results.append({
                    "record_id": record["id"],
                    "prompt_name": prompt_name,
                    "input": record["text"],
                    "output": fallback_output,
                    "demo_ready": False,
                    "response_time_ms": None,
                    "meta": {
                        "is_fallback": True,
                        "model_used": "llama-3.1-8b-instant",
                        "reason": str(e)
                    }
                })

    total_tests = len(results)
    passed = len([item for item in results if item["demo_ready"]])
    failed = total_tests - passed

    return {
        "summary": {
            "total_records": len(DEMO_RECORDS),
            "total_prompts": len(PROMPTS),
            "total_tests": total_tests,
            "passed": passed,
            "failed": failed,
            "demo_ready": failed == 0
        },
        "results": results
    }