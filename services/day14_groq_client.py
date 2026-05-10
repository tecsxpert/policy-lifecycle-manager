import os
import time
import requests
from dotenv import load_dotenv

load_dotenv()

GROQ_API_KEY = os.getenv("GROQ_API_KEY")


def call_day14_groq(prompt):

    if not GROQ_API_KEY:
        raise Exception("GROQ_API_KEY not found")

    url = "https://api.groq.com/openai/v1/chat/completions"

    headers = {
        "Authorization": f"Bearer {GROQ_API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": "llama-3.1-8b-instant",
        "messages": [
            {
                "role": "user",
                "content": prompt
            }
        ],
        "temperature": 0.3,
        "max_tokens": 800
    }

    retries = 3

    for attempt in range(retries):

        try:
            response = requests.post(
                url,
                headers=headers,
                json=payload,
                timeout=60
            )

            if response.status_code == 429:
                wait_time = 5 * (attempt + 1)
                print(f"Rate limited. Waiting {wait_time} seconds...")
                time.sleep(wait_time)
                continue

            response.raise_for_status()

            data = response.json()

            content = data["choices"][0]["message"]["content"]

            if not content or not content.strip():
                raise Exception("Empty Groq response")

            return content.strip()

        except requests.exceptions.RequestException as e:

            if attempt == retries - 1:
                raise Exception(str(e))

            time.sleep(3)

    raise Exception("Groq request failed after retries")