# Day 20 - Demo Script

## 60-Second Stack Explanation

This project is an AI-powered policy lifecycle manager built using Flask, Groq, ChromaDB, and RAG.

Flask is used to create backend API endpoints like categorise, query, generate report, and health check. Groq is used as the AI model service to generate fast responses for policy classification, recommendations, and reports.

ChromaDB is used as the vector database to store policy-related documents as embeddings. When a user asks a question, the system retrieves the most relevant policy content from ChromaDB.

This process is called RAG, which means Retrieval-Augmented Generation. It improves AI answers by giving the model useful context before generating the final response.

Finally, the /health endpoint shows whether the system is running correctly by returning metrics like uptime, cache status, model name, and ChromaDB document count.

## Health Endpoint Demo

Method: GET

URL:
http://127.0.0.1:5000/health

Expected Output:
{
  "avg_response_time": 0.5,
  "cache": {
    "items": 0
  },
  "chroma_doc_count": 2,
  "model": "llama3-8b-8192",
  "uptime": 107.29
}

## What I Will Say During Demo

First, I will open Postman and run the /health endpoint.  
Then I will explain that the response proves the backend service is live and returning system metrics.

This confirms that the Flask API, cache layer, model configuration, and ChromaDB connection are ready for demo.