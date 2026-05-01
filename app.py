from flask import Flask

from routes.categorise import categorise_bp
 (Day 4 - Implemented query endpoint with ChromaDB vector search)
from routes.query import query_bp

app = Flask(__name__)


app.register_blueprint(categorise_bp)

(Day 4 - Implemented query endpoint with ChromaDB vector search)
app.register_blueprint(query_bp)

if __name__ == "__main__":
    app.run(debug=True)
