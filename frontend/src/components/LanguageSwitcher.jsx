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

  const focusRing = "focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2 rounded";

  return (
    <div className={`flex items-center gap-0.5 sm:gap-1 ${className}`}>
      <button
        type="button"
        onClick={() => setLanguage("fr")}
        className={`inline-flex items-center justify-center gap-1.5 min-h-[44px] min-w-[44px] sm:min-w-0 px-2 sm:px-2.5 py-1 rounded text-sm font-medium transition-colors ${focusRing} ${
          i18n.language?.startsWith("fr")
            ? "bg-primary-600 text-white"
            : "text-gray-600 hover:text-primary-600 hover:bg-gray-100"
        }`}
        aria-label="Français"
      >
        <span className="text-base leading-none" aria-hidden>🇫🇷</span>
        <span>FR</span>
      </button>
      <span className="text-gray-300 px-0.5 self-center" aria-hidden>|</span>
      <button
        type="button"
        onClick={() => setLanguage("pt")}
        className={`inline-flex items-center justify-center gap-1.5 min-h-[44px] min-w-[44px] sm:min-w-0 px-2 sm:px-2.5 py-1 rounded text-sm font-medium transition-colors ${focusRing} ${
          i18n.language?.startsWith("pt")
            ? "bg-primary-600 text-white"
            : "text-gray-600 hover:text-primary-600 hover:bg-gray-100"
        }`}
        aria-label="Português"
      >
        <span className="text-base leading-none" aria-hidden>🇵🇹</span>
        <span>PT</span>
      </button>
    </div>
  );
};

export default LanguageSwitcher;
