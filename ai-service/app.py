from flask import Flask

app = Flask(__name__)

@app.route("/")
def home():
    return "AI Service Running"

@app.route("/health")
def health():
    return "OK"