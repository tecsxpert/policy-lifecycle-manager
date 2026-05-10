from fastapi import APIRouter
from pydantic import BaseModel
from services.day9_meta_service import build_ai_response

router = APIRouter()

class QueryRequest(BaseModel):
    prompt: str

@router.post("/query")
def query(request: QueryRequest):
    return build_ai_response(request.prompt, cached=False)