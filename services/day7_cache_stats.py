# simple independent cache (no touching old one)

cache_store = {}

def get_cache_stats():
    return {
        "items": len(cache_store)
    }