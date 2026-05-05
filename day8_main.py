from fastapi import FastAPI
from routes.day8_query_route import router

app = FastAPI()

app.include_router(router)
