import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const STORAGE_KEY = "econsulat_cookie_consent";

const CookieBanner = () => {
  const { t } = useTranslation();
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (!stored) setVisible(true);
    } catch (_) {
      setVisible(true);
    }
  }, []);

  const accept = () => {
    try {
      localStorage.setItem(STORAGE_KEY, "accepted");
    } catch (_) {}
    setVisible(false);
  };

  const refuse = () => {
    try {
      localStorage.setItem(STORAGE_KEY, "refused");
    } catch (_) {}
    setVisible(false);
  };

  if (!visible) return null;

  return (
    <div
      role="dialog"
      aria-label={t("cookies.banner.a11y")}
      className="fixed bottom-0 left-0 right-0 z-[100] p-4 bg-gray-900 text-white shadow-lg border-t border-gray-700 safe-area-pb"
      style={{ paddingBottom: "max(1rem, env(safe-area-inset-bottom))" }}
    >
      <div className="max-w-4xl mx-auto flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <p className="text-sm text-gray-200">
          {t("cookies.banner.message")}{" "}
          <Link to="/confidentialite" className="underline hover:text-white focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-offset-2 focus-visible:ring-offset-gray-900 rounded">
            {t("cookies.banner.privacy")}
          </Link>
        </p>
        <div className="flex flex-wrap gap-3 flex-shrink-0">
          <button
            type="button"
            onClick={refuse}
            className="px-4 py-2 text-sm font-medium text-gray-300 hover:text-white border border-gray-600 rounded-lg focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-offset-2 focus-visible:ring-offset-gray-900"
          >
            {t("cookies.banner.refuse")}
          </button>
          <button
            type="button"
            onClick={accept}
            className="px-4 py-2 text-sm font-medium bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-offset-2 focus-visible:ring-offset-gray-900"
          >
            {t("cookies.banner.accept")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default CookieBanner;
