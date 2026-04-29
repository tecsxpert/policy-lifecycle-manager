from flask import Blueprint, request, jsonify
from services.chroma_client import query_chroma
from services.groq_client import call_groq

query_bp = Blueprint("query", __name__)


# test route
@query_bp.route("/test", methods=["GET"])
def test():
    return jsonify({"message": "working"})


# main RAG route
@query_bp.route("/query", methods=["POST"])
def query():

    data = request.get_json()

    if not data or "question" not in data:
        return jsonify({"error": "Question required"}), 400

    question = data["question"]

    # STEP 1: get context from ChromaDB
    results = query_chroma(question)

    if not results:
        return jsonify({
            "answer": "No data found in ChromaDB. Please seed data first.",
            "sources": []
        })

    # STEP 2: build context
    context = "\n".join(results)

    # STEP 3: create prompt for Groq
    prompt = f"""
You are an AI assistant.

Use the context below to answer the question.

Context:
{context}

Question:
{question}

Give a clear and helpful answer.
"""

    # STEP 4: call Groq AI
    answer = call_groq(prompt)

    # STEP 5: return response
    return jsonify({
        "question": question,
        "answer": answer,
        "sources": results
    })