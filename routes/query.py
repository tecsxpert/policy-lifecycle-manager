from flask import Blueprint, request, jsonify
from services.chroma_client import query_document

query_bp = Blueprint("query", __name__)

@query_bp.route("/query", methods=["POST"])
def query():
    data = request.get_json()

    if not data or "question" not in data:
        return jsonify({"error": "Missing question"}), 400

    result = query_document(data["question"])

    return jsonify(result)