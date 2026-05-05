from fastapi import APIRouter
from services.day8_cache_service import (
    get_cache,
    set_cache,
    get_cache_stats
)

router = APIRouter()


@router.get("/query")
def query(prompt: str, fresh: bool = False):
    try:

        cached_response = get_cache(prompt, fresh)

        if cached_response:
            return {
                "success": True,
                "source": "cache",
                "data": cached_response,
                "stats": get_cache_stats()
            }

        ai_response = {
            "answer": f"You asked: {prompt}"
        }

        set_cache(prompt, ai_response)

        return {
            "success": True,
            "source": "ai",
            "data": ai_response,
            "stats": get_cache_stats()
        }

    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }
