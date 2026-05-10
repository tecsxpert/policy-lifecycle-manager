from fastapi import FastAPI
from routes.day9_meta_route import router

app = FastAPI()

app.include_router(router)