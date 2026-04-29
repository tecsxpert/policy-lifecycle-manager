from services.chroma_client import add_document, query_document

# Add sample data
add_document("1", "Employee data must be protected using encryption")
add_document("2", "All users must use strong passwords")
add_document("3", "Access should be role-based")

# Query
result = query_document("How to secure employee data?")

print(result)