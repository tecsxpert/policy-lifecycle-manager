from flask import Blueprint, jsonify
from services.day14_prompt_qa_service import run_prompt_qa

day14_prompt_qa_bp = Blueprint("day14_prompt_qa", __name__)


@day14_prompt_qa_bp.route("/prompt-qa", methods=["GET"])
def prompt_qa():
    report = run_prompt_qa()
    return jsonify(report), 200