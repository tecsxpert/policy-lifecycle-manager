import json
from datetime import datetime


def save_day16_report(file_name, data):
    report = {
        "generated_at": datetime.now().isoformat(),
        "report_type": "Day 16 Final Performance Verification",
        "data": data
    }

    with open(file_name, "w") as file:
        json.dump(report, file, indent=4)