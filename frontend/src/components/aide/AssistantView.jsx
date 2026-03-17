import React, { useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { ArrowLeftIcon, ArrowRightIcon } from "@heroicons/react/24/outline";
import axios from "axios";
import API_CONFIG from "../../config/api";
import assistantDataStatic from "../../data/assistant.json";

const AssistantView = () => {
  const { t } = useTranslation();
  const [assistantData, setAssistantData] = useState(assistantDataStatic);
  const [stepHistory, setStepHistory] = useState(["need"]);
  const [result, setResult] = useState(null);

  useEffect(() => {
    axios
      .get(`${API_CONFIG.BASE_URL}${API_CONFIG.ASSISTANT_STEPS}`)
      .then((res) => {
        if (res.data?.steps?.length) {
          setAssistantData({ steps: res.data.steps });
        }
      })
      .catch(() => {});
  }, []);

  const currentStepId = stepHistory[stepHistory.length - 1];
  const currentStep = useMemo(
    () => (assistantData.steps || []).find((s) => s.id === currentStepId),
    [currentStepId, assistantData]
  );

  const handleChoice = (choice) => {
    if (choice.result) {
      setResult(choice.result);
      return;
    }
    if (choice.nextStepId) {
      setStepHistory((prev) => [...prev, choice.nextStepId]);
    }
  };

  const handleBack = () => {
    if (stepHistory.length <= 1) return;
    setStepHistory((prev) => prev.slice(0, -1));
    setResult(null);
  };

  const handleRestart = () => {
    setStepHistory(["need"]);
    setResult(null);
  };

  if (result) {
    const summaryText = t(result.summaryKey);
    return (
      <div className="max-w-2xl mx-auto">
        <div className="bg-white rounded-lg shadow-lg p-6 border border-indigo-100">
          <h2 className="text-xl font-bold text-gray-900 mb-4">
            {t("aide.assistant.result.title")}
          </h2>
          <p className="text-gray-700 mb-6">{summaryText}</p>
          <div className="flex flex-wrap gap-4">
            <Link
              to={`/new-demande?documentTypeId=${result.documentTypeId}`}
              className="inline-flex items-center gap-2 px-5 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 font-medium"
            >
              {t("aide.faq.createDemande")}
              <ArrowRightIcon className="w-5 h-5" />
            </Link>
            <Link
              to="/aide?tab=faq"
              className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              {t("aide.assistant.result.seeFaq")}
            </Link>
          </div>
        </div>
        <div className="mt-6 flex gap-4">
          <button
            type="button"
            onClick={handleBack}
            className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium"
          >
            <ArrowLeftIcon className="w-5 h-5" />
            {t("aide.assistant.back")}
          </button>
          <button
            type="button"
            onClick={handleRestart}
            className="text-gray-600 hover:text-gray-900 font-medium"
          >
            {t("aide.assistant.restart")}
          </button>
        </div>
      </div>
    );
  }

  if (!currentStep) {
    return (
      <div className="max-w-2xl mx-auto">
        <p className="text-gray-500">{t("common.loading")}</p>
        <button
          type="button"
          onClick={handleRestart}
          className="mt-4 text-indigo-600 hover:text-indigo-800"
        >
          {t("aide.assistant.restart")}
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <p className="text-sm text-gray-500 mb-4">
        {t("aide.assistant.step")} {stepHistory.length} / …
      </p>
      <h2 className="text-xl font-bold text-gray-900 mb-6">
        {t(currentStep.questionKey)}
      </h2>
      <div className="space-y-3">
        {currentStep.choices.map((choice) => (
          <button
            key={choice.choiceKey}
            type="button"
            onClick={() => handleChoice(choice)}
            className="block w-full text-left px-5 py-4 bg-white border-2 border-gray-200 rounded-xl hover:border-indigo-400 hover:bg-indigo-50/50 transition font-medium text-gray-900"
          >
            {t(choice.choiceKey)}
          </button>
        ))}
      </div>
      <div className="mt-8 flex gap-4">
        {stepHistory.length > 1 && (
          <button
            type="button"
            onClick={handleBack}
            className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium"
          >
            <ArrowLeftIcon className="w-5 h-5" />
            {t("aide.assistant.back")}
          </button>
        )}
        <button
          type="button"
          onClick={handleRestart}
          className="text-gray-600 hover:text-gray-900 font-medium"
        >
          {t("aide.assistant.restart")}
        </button>
      </div>
    </div>
  );
};

export default AssistantView;
