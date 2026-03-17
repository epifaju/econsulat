import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useSearchParams } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import FaqView from "./FaqView";
import AssistantView from "./AssistantView";

const TAB_FAQ = "faq";
const TAB_ASSISTANT = "assistant";

const AidePage = () => {
  const { t } = useTranslation();
  const { isAuthenticated } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const tabParam = searchParams.get("tab");
  const [activeTab, setActiveTab] = useState(
    tabParam === TAB_ASSISTANT ? TAB_ASSISTANT : TAB_FAQ
  );

  useEffect(() => {
    if (tabParam === TAB_ASSISTANT) setActiveTab(TAB_ASSISTANT);
    else if (tabParam === TAB_FAQ) setActiveTab(TAB_FAQ);
  }, [tabParam]);

  const setActiveTabAndUrl = (tab) => {
    setActiveTab(tab);
    setSearchParams({ tab });
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4">
      <div className="max-w-4xl mx-auto">
        {!isAuthenticated && (
          <div className="mb-6">
            <Link
              to="/"
              className="inline-flex items-center text-gray-600 hover:text-gray-900 font-medium"
            >
              ← {t("common.backToHome")}
            </Link>
          </div>
        )}
        <h1 className="text-2xl font-bold text-gray-900 mb-2">{t("aide.title")}</h1>
        <p className="text-gray-600 mb-8">{t("aide.subtitle")}</p>

        <div className="flex border-b border-gray-200 mb-8">
          <button
            type="button"
            onClick={() => setActiveTabAndUrl(TAB_FAQ)}
            className={`px-6 py-3 font-medium border-b-2 -mb-px transition ${
              activeTab === TAB_FAQ
                ? "border-indigo-600 text-indigo-600"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            {t("aide.tabFaq")}
          </button>
          <button
            type="button"
            onClick={() => setActiveTabAndUrl(TAB_ASSISTANT)}
            className={`px-6 py-3 font-medium border-b-2 -mb-px transition ${
              activeTab === TAB_ASSISTANT
                ? "border-indigo-600 text-indigo-600"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            {t("aide.tabAssistant")}
          </button>
        </div>

        {activeTab === TAB_FAQ && <FaqView />}
        {activeTab === TAB_ASSISTANT && <AssistantView />}
      </div>
    </div>
  );
};

export default AidePage;
