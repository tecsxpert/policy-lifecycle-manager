from services.day7_model_info import get_model_name
from services.day7_chroma_stats import get_chroma_doc_count
from services.day7_cache_stats import get_cache_stats
from flask import Blueprint, jsonify
from utils.day7_timer import get_uptime
from services.day7_metrics_service import get_avg_response_time, add_response_time
import time

health_bp = Blueprint("health", __name__)

# HEALTH ROUTE
@health_bp.route("/health", methods=["GET"])
def health():
    return jsonify({
        "model":get_model_name(),
        "avg_response_time": get_avg_response_time(),
        "chroma_doc_count": get_chroma_doc_count(),
        "uptime": get_uptime(),
        "cache": get_cache_stats()
    })

#  TEST ROUTE 
@health_bp.route("/test", methods=["GET"])
def test():
    start = time.time()

    time.sleep(0.5)  # simulate delay

    end = time.time()
    add_response_time(end - start)

    return {"message": "test done"}
