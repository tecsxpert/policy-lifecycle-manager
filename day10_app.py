from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route("/query", methods=["POST"])
def query():
    data = request.get_json()
    prompt = data.get("prompt", "")

    return jsonify({
        "response": f"Processed: {prompt}"
    })

if __name__ == "__main__":
    print("Server starting...")
    app.run(debug=True, port=5001)