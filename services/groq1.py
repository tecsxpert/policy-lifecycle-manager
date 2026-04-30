import os
import requests
from dotenv import load_dotenv

# Load .env file
load_dotenv(dotenv_path=".env")

# Get API key from .env
api_key = os.getenv("GROQ_API_KEY")
print(api_key)
# API endpoint
url = "https://api.groq.com/openai/v1/chat/completions"

# Headers
headers = {
    "Authorization": f"Bearer {api_key}",
    "Content-Type": "application/json"
}

# Test prompt
payload = {
    "model": "llama-3.1-8b-instant",
    "messages": [
        {"role": "user", "content": "Hello"}
    ]
}

# Send request
response = requests.post(url, headers=headers, json=payload)

# Output
if response.status_code == 200:
    print("✅ Success")
    print(response.json())
else:
    print("❌ API Error:", response.text)