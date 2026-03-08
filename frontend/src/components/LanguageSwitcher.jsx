import React from "react";
import axios from "axios";
import { useTranslation } from "react-i18next";

const LanguageSwitcher = ({ className = "" }) => {
  const { i18n } = useTranslation();

  const setLanguage = (lng) => {
    i18n.changeLanguage(lng);
    const token = localStorage.getItem("token");
    if (token) {
      axios
        .put(
          "/api/users/me",
          { preferredLocale: lng },
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .catch(() => {});
    }
  };

  return (
    <div className={`flex items-center gap-1 ${className}`}>
      <button
        type="button"
        onClick={() => setLanguage("fr")}
        className={`px-2 py-1 rounded text-sm font-medium transition-colors ${
          i18n.language?.startsWith("fr")
            ? "bg-primary-600 text-white"
            : "text-gray-600 hover:text-primary-600 hover:bg-gray-100"
        }`}
        aria-label="Français"
      >
        FR
      </button>
      <span className="text-gray-300">|</span>
      <button
        type="button"
        onClick={() => setLanguage("pt")}
        className={`px-2 py-1 rounded text-sm font-medium transition-colors ${
          i18n.language?.startsWith("pt")
            ? "bg-primary-600 text-white"
            : "text-gray-600 hover:text-primary-600 hover:bg-gray-100"
        }`}
        aria-label="Português"
      >
        PT
      </button>
    </div>
  );
};

export default LanguageSwitcher;
