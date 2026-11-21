import { createContext, useContext, useState } from "react";
import api from "./api";

const AuthContext = createContext(null);

export function homeFor(role) {
  if (role === "RECRUITER") return "/recruiter";
  if (role === "ADMIN") return "/admin";
  return "/jobs";
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("user"));
    } catch {
      return null;
    }
  });

  const save = (data) => {
    const u = { name: data.name, email: data.email, role: data.role };
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify(u));
    setUser(u);
    return u;
  };

  const login = async (email, password) =>
    save((await api.post("/auth/login", { email, password })).data);

  const register = async (body) =>
    save((await api.post("/auth/register", body)).data);

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
