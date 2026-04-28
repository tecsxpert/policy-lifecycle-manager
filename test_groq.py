from services.groq_client import call_groq

if __name__ == "__main__":
    prompt = "Explain Artificial Intelligence in one line"

    result = call_groq(prompt)

    print("Response:\n")
    print(result)