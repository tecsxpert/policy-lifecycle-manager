from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json
import re

categorise_bp = Blueprint("categorise", __name__)

@categorise_bp.route("/categorise", methods=["POST"])
def categorise():
    data = request.get_json()

    # Validate input
    if not data or "text" not in data:
        return jsonify({"error": "Text is required"}), 400

    text = data["text"]

    # Prompt
    prompt = f"""
You are a strict text classification system.

Classify the text into ONE category:
["compliance", "security", "hr", "finance", "operations"]

Rules:
- Return ONLY JSON
- No extra text
- Confidence between 0 and 1
- reasoning max 10 words

Output:
{{
  "category": "compliance",
  "confidence": 0.95,
  "reasoning": "policy related"
}}

Text:
{text}
"""

    # Call AI
    response = call_groq(prompt)

    # If API failed
    if not response:
        return jsonify({
            "message": "AI service failed",
            "is_fallback": True
        })

    try:
        # Try direct JSON
        parsed = json.loads(response)

    except Exception:
        try:
            # Extract JSON if extra text present
            match = re.search(r"\{.*\}", response, re.DOTALL)
            if match:
                parsed = json.loads(match.group())
            else:
                raise Exception("No JSON found")

        except Exception:
            return jsonify({
                "message": "Parsing failed",
                "raw": response,
                "is_fallback": True
            })

    # Final response
    return jsonify({
        "category": parsed.get("category"),
        "confidence": parsed.get("confidence"),
        "reasoning": parsed.get("reasoning"),
        "meta": {
            "cached": False
        }
    })
