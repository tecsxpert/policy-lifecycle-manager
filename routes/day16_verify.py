from flask import Blueprint, jsonify
from services.day16_performance_service import run_performance_check
from services.day16_fallback_service import verify_fallback
from services.day16_redis_service import verify_redis_cache
from utils.day16_report_writer import save_day16_report

day16_verify_bp = Blueprint("day16_verify", __name__)


@day16_verify_bp.route("/day16/performance", methods=["GET"])
def performance_check():
    result = run_performance_check()
    save_day16_report("day16_performance_report.json", result)
    return jsonify(result), 200


@day16_verify_bp.route("/day16/fallback", methods=["GET"])
def fallback_check():
    result = verify_fallback()
    return jsonify(result), 200


@day16_verify_bp.route("/day16/redis", methods=["GET"])
def redis_check():
    result = verify_redis_cache()
    return jsonify(result), 200


@day16_verify_bp.route("/day16/final-verify", methods=["GET"])
def final_verify():
    performance = run_performance_check()
    fallback = verify_fallback()
    redis = verify_redis_cache()

    final_result = {
        "summary": {
            "performance_passed": performance["summary"]["overall_passed"],
            "fallback_passed": fallback["passed"],
            "redis_cache_passed": redis["passed"],
            "day16_completed": (
                performance["summary"]["overall_passed"]
                and fallback["passed"]
                and redis["passed"]
            )
        },
        "performance": performance,
        "fallback": fallback,
        "redis": redis
    }

    save_day16_report("day16_performance_report.json", final_result)

    return jsonify(final_result), 200