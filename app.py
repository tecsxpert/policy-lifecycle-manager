from flask import Flask
from routes.categorise import categorise_bp

# Create Flask app
app = Flask(__name__)

# Register routes
app.register_blueprint(categorise_bp)

# Run server
if __name__ == "__main__":
    app.run(debug=True)