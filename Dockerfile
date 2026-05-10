FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --upgrade pip

RUN pip install --no-cache-dir \
    torch==2.11.0+cpu \
    --index-url https://download.pytorch.org/whl/cpu

RUN pip install --no-cache-dir \
    Flask==3.0.3 \
    requests==2.32.3 \
    python-dotenv==1.0.1 \
    sentence-transformers==3.0.1 \
    flask-limiter==3.8.0 \
    gunicorn==22.0.0 \
    chromadb

COPY . .

EXPOSE 5000

CMD ["python", "day14app.py"]