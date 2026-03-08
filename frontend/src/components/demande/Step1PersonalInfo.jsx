import React from "react";
import { useTranslation } from "react-i18next";

const Step1PersonalInfo = ({ formData, updateFormData, civilites, pays }) => {
  const { t } = useTranslation();
  const handleChange = (field, value) => {
    updateFormData({ [field]: value });
  };

  return (
    <div className="space-y-6">
      <div className="text-center">
        <h3 className="text-xl font-semibold text-gray-800 mb-2">
          {t("newDemande.step1.title")}
        </h3>
        <p className="text-gray-600">
          {t("newDemande.step1.subtitle")}
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t("newDemande.step1.civility")} <span className="text-red-500">*</span>
          </label>
          <select
            value={formData.civiliteId}
            onChange={(e) => handleChange("civiliteId", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            required
          >
            <option value="">{t("newDemande.step1.selectCivility")}</option>
            {civilites.map((civilite) => (
              <option key={civilite.id} value={civilite.id}>
                {civilite.libelle}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t("newDemande.step1.firstName")} <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.firstName}
            onChange={(e) => handleChange("firstName", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder={t("newDemande.step1.firstNamePlaceholder")}
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t("newDemande.step1.lastName")} <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.lastName}
            onChange={(e) => handleChange("lastName", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder={t("newDemande.step1.lastNamePlaceholder")}
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t("newDemande.step1.birthDate")} <span className="text-red-500">*</span>
          </label>
          <input
            type="date"
            value={formData.birthDate}
            onChange={(e) => handleChange("birthDate", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t("newDemande.step1.birthPlace")} <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.birthPlace}
            onChange={(e) => handleChange("birthPlace", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder={t("newDemande.step1.birthPlacePlaceholder")}
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            {t("newDemande.step1.birthCountry")} <span className="text-red-500">*</span>
          </label>
          <select
            value={formData.birthCountryId}
            onChange={(e) => handleChange("birthCountryId", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            required
          >
            <option value="">{t("newDemande.step1.selectCountry")}</option>
            {pays.map((pays) => (
              <option key={pays.id} value={pays.id}>
                {pays.libelle}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="bg-blue-50 p-4 rounded-lg">
        <div className="flex items-start">
          <div className="flex-shrink-0">
            <svg
              className="h-5 w-5 text-blue-400"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                clipRule="evenodd"
              />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-blue-800">
              {t("newDemande.step1.infoTitle")}
            </h3>
            <div className="mt-2 text-sm text-blue-700">
              <p>{t("newDemande.step1.infoRequired")}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Step1PersonalInfo;
