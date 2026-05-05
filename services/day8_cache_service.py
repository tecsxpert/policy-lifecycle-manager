import hashlib
import json
from utils.day8_redis_client import r

TTL = 900  # 15 minutes

HIT_KEY = "cache_hits"
MISS_KEY = "cache_misses"


def make_key(prompt: str) -> str:
    normalized_prompt = prompt.strip().lower()
    return hashlib.sha256(normalized_prompt.encode()).hexdigest()


def get_cache(prompt: str, fresh: bool = False):

    if fresh:
        r.incr(MISS_KEY)
        return None

    key = make_key(prompt)

    cached_data = r.get(key)

    if cached_data:
        r.incr(HIT_KEY)
        return json.loads(cached_data)

    r.incr(MISS_KEY)
    return None


def set_cache(prompt: str, response: dict):
    key = make_key(prompt)

    r.setex(
        key,
        TTL,
        json.dumps(response)
    )


def get_cache_stats():
    return {
        "hits": int(r.get(HIT_KEY) or 0),
        "misses": int(r.get(MISS_KEY) or 0)
    }
