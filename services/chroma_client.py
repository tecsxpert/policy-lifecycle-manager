import chromadb
from sentence_transformers import SentenceTransformer

# embedding model
model = SentenceTransformer("all-MiniLM-L6-v2")

# 🔥 persistent DB (VERY IMPORTANT)
client = chromadb.PersistentClient(path="./chroma_db")

collection = client.get_or_create_collection(name="policy_docs")


# -------------------------
# ADD DATA
# -------------------------
def add_document(doc_id, text):
    embedding = model.encode(text).tolist()

    collection.add(
        ids=[doc_id],
        embeddings=[embedding],
        documents=[text]
    )


# -------------------------
# QUERY DATA
# -------------------------
def query_chroma(query_text, top_k=3):
    query_embedding = model.encode(query_text).tolist()

    results = collection.query(
        query_embeddings=[query_embedding],
        n_results=top_k
    )

    return results.get("documents", [[]])[0]