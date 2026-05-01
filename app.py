from flask import Flask
from routes.categorise import categorise_bp

app = Flask(__name__)   # ✅ create app FIRST

app.register_blueprint(categorise_bp)  # ✅ then use it

if __name__ == "__main__":
    app.run(debug=True)