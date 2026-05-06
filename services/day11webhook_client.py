import requests

def send_webhook(webhook_url, payload):
    if not webhook_url:
        return

    try:
        requests.post(webhook_url, json=payload, timeout=5)
    except Exception as e:
        print("Webhook failed:", str(e))