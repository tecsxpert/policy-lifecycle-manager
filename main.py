from fastapi import FastAPI
from routes.query_api import router as query_router

app = FastAPI(
    title="Query API",
    description="Simple API for handling questions",
    version="1.0"
)

# include your route
app.include_router(query_router)


@app.get("/")
def home():
    return {"message": "API is running successfully"}