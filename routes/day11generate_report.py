from flask import Blueprint, request, jsonify
from threading import Thread
import time
import uuid

from services.day11webhook_client import send_webhook

generate_report_bp = Blueprint("generate_report", __name__)

jobs = {}


@generate_report_bp.route("/test-report", methods=["GET"])
def test_report():
    return jsonify({
        "message": "generate report route working"
    }), 200


def background_report(job_id, text, webhook_url=None):
    jobs[job_id]["status"] = "processing"

    time.sleep(10)

    jobs[job_id]["status"] = "completed"
    jobs[job_id]["result"] = {
        "title": "AI Generated Report",
        "summary": f"Report generated for: {text}"
    }

    send_webhook(webhook_url, {
        "job_id": job_id,
        "status": "completed",
        "result": jobs[job_id]["result"]
    })


@generate_report_bp.route("/generate-report", methods=["POST"])
def generate_report():
    data = request.get_json()

    if not data:
        return jsonify({
            "error": "JSON body is required"
        }), 400

    text = data.get("text")
    webhook_url = data.get("webhook_url")

    if not text:
        return jsonify({
            "error": "text is required"
        }), 400

    job_id = str(uuid.uuid4())

    jobs[job_id] = {
        "job_id": job_id,
        "status": "pending",
        "result": None
    }

    thread = Thread(
        target=background_report,
        args=(job_id, text, webhook_url)
    )
    thread.start()

    return jsonify({
        "message": "Report generation started",
        "job_id": job_id,
        "status": "pending"
    }), 202


@generate_report_bp.route("/generate-report/<job_id>", methods=["GET"])
def get_report_status(job_id):
    job = jobs.get(job_id)

    if not job:
        return jsonify({
            "error": "Job not found"
        }), 404

    return jsonify(job), 200