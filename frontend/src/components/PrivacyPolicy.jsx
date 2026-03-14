import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const PrivacyPolicy = () => {
  const { t } = useTranslation();

  return (
    <div className="min-h-screen bg-white">
      <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <Link to="/" className="flex items-center gap-2 rounded focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2">
              <img src="/images/logo.svg" alt="" className="h-9 w-9 rounded-lg" width="36" height="36" />
              <span className="text-xl font-semibold text-gray-900">{t("common.appName")}</span>
            </Link>
            <Link
              to="/"
              className="text-sm font-medium text-primary-600 hover:text-primary-700 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2 rounded"
            >
              {t("common.backToHome")}
            </Link>
          </div>
        </div>
      </header>
      <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <h1 className="text-3xl font-bold text-gray-900">{t("legal.privacy.title")}</h1>
        <div className="mt-8 space-y-4 text-gray-600">
          <p>{t("legal.privacy.p1")}</p>
          <p>{t("legal.privacy.p2")}</p>
          <p>{t("legal.privacy.p3")}</p>
          <p>{t("legal.privacy.p4")}</p>
        </div>
      </main>
    </div>
  );
};

export default PrivacyPolicy;
