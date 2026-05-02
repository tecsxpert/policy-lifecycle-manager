# try to reuse existing chroma client safely
try:
    from services.chroma_client import collection
except:
    collection = None

def get_chroma_doc_count():
    try:
        if collection:
            return collection.count()
        return 0
    except:
        return 0