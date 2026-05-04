import os
import requests
from dotenv import load_dotenv

load_dotenv()

api_key = os.getenv("GROQ_API_KEY")

def call_groq(prompt):
    try:
        url = "https://api.groq.com/openai/v1/chat/completions"

        headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        }

        data = {
            "model": "llama-3.1-8b-instant",   # ✅ WORKING MODEL
            "messages": [
                {"role": "user", "content": prompt}
            ]
        }

        response = requests.post(url, headers=headers, json=data)

        print("STATUS:", response.status_code)
        print("RAW:", response.text)

        if response.status_code != 200:
            return None

        result = response.json()

        return result["choices"][0]["message"]["content"]

    except Exception as e:
        print("ERROR:", str(e))
        return None