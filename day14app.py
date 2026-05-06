from flask import Flask
from routes.day14_prompt_qa import day14_prompt_qa_bp

app = Flask(__name__)

app.register_blueprint(day14_prompt_qa_bp)


@app.route("/", methods=["GET"])
def home():
    return {
        "message": "Day 14 prompt QA app working"
    }


if __name__ == "__main__":
    app.run(debug=True, port=5002)