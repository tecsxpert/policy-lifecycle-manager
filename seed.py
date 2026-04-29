from services.chroma_client import add_document

print("Seeding data...")

add_document("1", "AI stands for Artificial Intelligence.")
add_document("2", "Machine learning is a subset of AI.")
add_document("3", "Deep learning uses neural networks.")

print("Done!")