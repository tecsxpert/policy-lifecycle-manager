from flask import Flask
from routes.day11generate_report import generate_report_bp

app = Flask(__name__)

app.register_blueprint(generate_report_bp)


@app.route("/", methods=["GET"])
def home():
    return {
        "message": "Day 11 app working"
    }


if __name__ == "__main__":
    app.run(debug=True, port=5000)