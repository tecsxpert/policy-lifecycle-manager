from flask import Flask, request, jsonify
from services.groq_client import call_groq
from services.day13_ai_fallback import fallback_response

app = Flask(__name__)


@app.route("/", methods=["GET"])
def home():
    return {
        "message": "Day 13 real fallback app working"
    }


@app.route("/generate-report-safe", methods=["POST"])
def generate_report_safe():
    data = request.get_json()

    if not data:
        return jsonify({
            "error": "JSON body is required"
        }), 400

    text = data.get("text")

    if not text:
        return jsonify({
            "error": "text is required"
        }), 400

    prompt = f"""
Generate a professional policy lifecycle report.

Input:
{text}

Return a clear report with:
- title
- executive summary
- overview
- key risks
- recommendations
"""

    try:
        ai_response = call_groq(prompt)

        return jsonify({
            "answer": ai_response,
            "meta": {
                "is_fallback": False,
                "model_used": "llama-3.1-8b-instant",
                "confidence": 0.95
            }
        }), 200

    except Exception as e:
        response = fallback_response(str(e))
        return jsonify(response), 200


if __name__ == "__main__":
    app.run(debug=True, port=5001)