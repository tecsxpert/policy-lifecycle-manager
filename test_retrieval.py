from sentence_transformers import SentenceTransformer
import chromadb

# load model
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

# connect to same DB used in ingestion
chroma_client = chromadb.PersistentClient(path="./chroma_db")
collection = chroma_client.get_or_create_collection(name="docs")

# query text
query = "policy lifecycle"

# embedding
query_embedding = embedding_model.encode(query).tolist()

# search
results = collection.query(
    query_embeddings=[query_embedding],
    n_results=3
)

# print results
print("🔍 Retrieved Documents:")
print(results["documents"])