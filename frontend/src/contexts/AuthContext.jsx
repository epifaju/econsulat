import React, { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";
import i18n from "../i18n";
import API_CONFIG from "../config/api";

axios.defaults.baseURL = API_CONFIG.BASE_URL;

const syncUILocale = (preferredLocale) => {
  if (preferredLocale && ["fr", "pt"].includes(preferredLocale)) {
    i18n.changeLanguage(preferredLocale);
  }
};

// Valeur par défaut pour éviter l'erreur "useAuth must be used within AuthProvider"
// lors du hot-reload Vite (React Refresh) ; en prod le provider enveloppe toujours l'app.
const defaultAuthValue = {
  user: null,
  isAuthenticated: false,
  token: null,
  login: async () => ({ success: false, error: "Non initialisé" }),
  register: async () => ({ success: false, error: "Non initialisé" }),
  logout: () => {},
  updateUser: () => {},
  loading: true,
};

const AuthContext = createContext(defaultAuthValue);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userData = localStorage.getItem("user");

    if (token && userData) {
      setUser(JSON.parse(userData));
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      axios.get("/api/users/me").then((r) => syncUILocale(r.data?.preferredLocale)).catch(() => {});
    }

    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const response = await axios.post(
        "/api/auth/login",
        { email, password },
        { headers: { "Content-Type": "application/json" } }
      );
      const { token, id, firstName, lastName, email: userEmail, role } = response.data;

      const userData = { id, firstName, lastName, email: userEmail, role };

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(userData));
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      setUser(userData);
      axios.get("/api/users/me").then((r) => syncUILocale(r.data?.preferredLocale)).catch(() => {});
      return { success: true };
    } catch (error) {
      const data = error.response?.data;
      let message = data?.message || data?.error || "Erreur de connexion";
      if (data?.fields && typeof data.fields === "object") {
        const fieldErrors = Object.entries(data.fields).map(([k, v]) => `${k}: ${v}`);
        message = fieldErrors.length ? fieldErrors.join(" ; ") : message;
      }
      return { success: false, error: message };
    }
  };

  const updateUser = (userData) => {
    if (userData) {
      setUser(userData);
      localStorage.setItem("user", JSON.stringify(userData));
    }
  };

  const register = async (userData) => {
    try {
      const response = await axios.post("/api/auth/register", {
        firstName: userData.firstName,
        lastName: userData.lastName,
        email: userData.email,
        password: userData.password,
      });

      return {
        success: true,
        message: response.data.message,
        userId: response.data.userId,
        email: response.data.email,
      };
    } catch (error) {
      return {
        success: false,
        error:
          error.response?.data?.error || "Erreur lors de la création du compte",
      };
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    delete axios.defaults.headers.common["Authorization"];
    setUser(null);
  };

  const value = {
    user,
    isAuthenticated: !!user,
    token: localStorage.getItem("token"),
    login,
    register,
    logout,
    updateUser,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
