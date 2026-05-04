import axios from "axios";

const BASE_URL = "http://localhost:8080";

// Get token from localStorage
const getToken = () => localStorage.getItem("token");

// Axios instance with JWT header
const api = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// Add JWT token to every request
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// AUTH
export const loginUser = async (username, password) => {
  const res = await api.post("/api/auth/login", { username, password });
  return res.data; // { token, username }
};

// POLICIES
export const getAllPolicies = async (page = 0, size = 10) => {
  const res = await api.get(`/api/policies/all?page=${page}&size=${size}`);
  return res.data; // { content, totalElements, totalPages, currentPage }
};

export const createPolicy = async (policy) => {
  const res = await api.post("/api/policies/create", policy);
  return res.data;
};

export const updatePolicy = async (id, policy) => {
  const res = await api.put(`/api/policies/${id}`, policy);
  return res.data;
};

export const deletePolicy = async (id) => {
  const res = await api.delete(`/api/policies/${id}`);
  return res.data;
};

export const searchPolicies = async (q) => {
  const res = await api.get(`/api/policies/search?q=${q}`);
  return res.data;
};

export const getPolicyStats = async () => {
  const res = await api.get("/api/policies/stats");
  return res.data; // { totalPolicies, totalActivePolicies }
};