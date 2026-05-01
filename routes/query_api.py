from fastapi import APIRouter
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import chromadb
from groq import Groq
from dotenv import load_dotenv
import os

# -------------------
# LOAD ENV
# -------------------
load_dotenv()
GROQ_API_KEY = os.getenv("GROQ_API_KEY")

# -------------------
# INIT ROUTER
# -------------------
router = APIRouter()

# -------------------
# REQUEST MODEL
# -------------------
class QueryRequest(BaseModel):
    question: str


# -------------------
# MODELS + DB
# -------------------
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

chroma_client = chromadb.PersistentClient(path="./chroma_db")
collection = chroma_client.get_or_create_collection(name="docs")

groq_client = Groq(api_key=GROQ_API_KEY)


# -------------------
# QUERY ENDPOINT
# -------------------
@router.post("/query")
def query_api(request: QueryRequest):

    try:
        print("🔥 API HIT")

        question = request.question

        # 1. EMBEDDING
        q_emb = embedding_model.encode(question).tolist()

        # 2. CHROMA QUERY
        results = collection.query(
            query_embeddings=[q_emb],
            n_results=3
        )

        # 3. SAFE DOCUMENT HANDLING
        docs = results.get("documents", [])

        if len(docs) > 0 and len(docs[0]) > 0:
            docs = docs[0]
        else:
            docs = ["No relevant context found"]

        context = "\n".join(docs)

        # 4. GROQ CHECK
        if not GROQ_API_KEY:
            return {"error": "GROQ_API_KEY missing in .env"}

        # 5. GROQ CALL
        response = groq_client.chat.completions.create(
            model="llama-3.1-8b-instant",
            messages=[
                {
                    "role": "user",
                    "content": f"Context:\n{context}\n\nQuestion:\n{question}"
                }
            ]
        )

        return {
            "question": question,
            "answer": response.choices[0].message.content,
            "sources": docs
        }

    except Exception as e:
        print("❌ ERROR:", str(e))
        return {"error": str(e)}