from flask import Flask, jsonify
from routes.categorise import categorise_bp

# Create Flask app
app = Flask(__name__)

# Register routes
app.register_blueprint(categorise_bp)

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "healthy", "service": "ai-service"})

# Run server
if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
