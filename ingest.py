import chromadb
from sentence_transformers import SentenceTransformer

# ------------------------
# INIT
# ------------------------
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

chroma_client = chromadb.PersistentClient(path="./chroma_db")
collection = chroma_client.get_or_create_collection(name="docs")


# ------------------------
# SAMPLE DOCUMENTS (you can replace with PDF/text)
# ------------------------
documents = [
    "Policy lifecycle includes creation, approval, implementation, review, and retirement.",
    "Machine learning is a subset of AI that allows systems to learn from data.",
    "FastAPI is a modern Python framework for building APIs quickly and efficiently."
]


# ------------------------
# INGEST FUNCTION
# ------------------------
def ingest_documents():
    for i, doc in enumerate(documents):
        embedding = embedding_model.encode(doc).tolist()

        collection.add(
            documents=[doc],
            embeddings=[embedding],
            ids=[f"doc_{i}"]
        )

    print("✅ Documents ingested successfully!")


# ------------------------
# RUN
# ------------------------
if __name__ == "__main__":
    ingest_documents()