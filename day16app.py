from flask import Flask
from routes.day16_verify import day16_verify_bp

app = Flask(__name__)

app.register_blueprint(day16_verify_bp)


@app.route("/", methods=["GET"])
def home():
    return {
        "message": "Day 16 final performance verification app working"
    }


if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True, port=5003)