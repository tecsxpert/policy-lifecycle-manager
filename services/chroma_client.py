import chromadb

# Persistent database client
client = chromadb.PersistentClient(path="./chroma_db")

collection = client.get_or_create_collection(name="documents")

def add_document(text, id):
    collection.add(
        documents=[text],
        ids=[id]
    )

def query_document(query_text):
    results = collection.query(
        query_texts=[query_text],
        n_results=1
    )
    return results
