import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8080/api" });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export function errorMessage(err) {
  const d = err.response && err.response.data;
  if (!d) return "Cannot reach the server. Is the backend running?";
  if (d.error && d.error !== "Bad Request" && d.error !== "Forbidden") return d.error;
  const vals = Object.values(d).filter((v) => typeof v === "string");
  if (err.response.status === 403) return "You are not allowed to do this. Please log in again.";
  return vals.length ? vals.join(", ") : "Something went wrong";
}

export function money(n) {
  return n == null ? "-" : Number(n).toLocaleString("en-IN");
}

export default api;
