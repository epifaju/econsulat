import React from "react";
import { useTranslation } from "react-i18next";

const Step6Summary = ({
  formData,
  civilites,
  pays,
  documentTypes,
  onSubmit,
  loading,
}) => {
  const { t, i18n } = useTranslation();
  const dateLocale = i18n.language?.startsWith("pt") ? "pt-PT" : "fr-FR";
  const notSpecified = t("newDemande.step6.notSpecified");

  const getCiviliteLabel = (id) => {
    const civilite = civilites.find((c) => c.id == id);
    return civilite ? civilite.libelle : notSpecified;
  };

  const getPaysLabel = (id) => {
    const paysItem = pays.find((p) => p.id == id);
    return paysItem ? paysItem.libelle : notSpecified;
  };

  const getDocumentTypeLabel = (typeId) => {
    const docType = documentTypes.find((doc) => (doc.id || doc.value) == typeId);
    return docType
      ? docType.libelle || docType.label || docType.displayName
      : notSpecified;
  };

  const formatDate = (dateString) => {
    if (!dateString) return notSpecified;
    return new Date(dateString).toLocaleDateString(dateLocale);
  };

  return (
    <div className="space-y-6">
      <div className="text-center">
        <h3 className="text-xl font-semibold text-gray-800 mb-2">
          {t("newDemande.step6.title")}
        </h3>
        <p className="text-gray-600">
          {t("newDemande.step6.subtitle")}
        </p>
      </div>

      <div className="space-y-6">
        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
            <svg className="w-5 h-5 mr-2 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
            </svg>
            {t("newDemande.step6.personalInfo")}
          </h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.civility")}:</span>
              <p className="text-gray-800">{getCiviliteLabel(formData.civiliteId)}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.firstName")}:</span>
              <p className="text-gray-800">{formData.firstName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.lastName")}:</span>
              <p className="text-gray-800">{formData.lastName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthDate")}:</span>
              <p className="text-gray-800">{formatDate(formData.birthDate)}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthPlace")}:</span>
              <p className="text-gray-800">{formData.birthPlace || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthCountry")}:</span>
              <p className="text-gray-800">{getPaysLabel(formData.birthCountryId)}</p>
            </div>
          </div>
        </div>

        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
            <svg className="w-5 h-5 mr-2 text-green-600" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
            </svg>
            {t("newDemande.step6.address")}
          </h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.street")}:</span>
              <p className="text-gray-800">{formData.streetName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.number")}:</span>
              <p className="text-gray-800">{formData.streetNumber || notSpecified}</p>
            </div>
            {formData.boxNumber && (
              <div>
                <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.box")}:</span>
                <p className="text-gray-800">{formData.boxNumber}</p>
              </div>
            )}
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.postalCode")}:</span>
              <p className="text-gray-800">{formData.postalCode || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.city")}:</span>
              <p className="text-gray-800">{formData.city || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.country")}:</span>
              <p className="text-gray-800">{getPaysLabel(formData.countryId)}</p>
            </div>
          </div>
        </div>

        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
            <svg className="w-5 h-5 mr-2 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
            </svg>
            {t("newDemande.step6.fatherInfo")}
          </h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.firstName")}:</span>
              <p className="text-gray-800">{formData.fatherFirstName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.lastName")}:</span>
              <p className="text-gray-800">{formData.fatherLastName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthDate")}:</span>
              <p className="text-gray-800">{formatDate(formData.fatherBirthDate)}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthPlace")}:</span>
              <p className="text-gray-800">{formData.fatherBirthPlace || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthCountry")}:</span>
              <p className="text-gray-800">{getPaysLabel(formData.fatherBirthCountryId)}</p>
            </div>
          </div>
        </div>

        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
            <svg className="w-5 h-5 mr-2 text-pink-600" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
            </svg>
            {t("newDemande.step6.motherInfo")}
          </h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.firstName")}:</span>
              <p className="text-gray-800">{formData.motherFirstName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.lastName")}:</span>
              <p className="text-gray-800">{formData.motherLastName || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthDate")}:</span>
              <p className="text-gray-800">{formatDate(formData.motherBirthDate)}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthPlace")}:</span>
              <p className="text-gray-800">{formData.motherBirthPlace || notSpecified}</p>
            </div>
            <div>
              <span className="text-sm font-medium text-gray-500">{t("newDemande.step6.birthCountry")}:</span>
              <p className="text-gray-800">{getPaysLabel(formData.motherBirthCountryId)}</p>
            </div>
          </div>
        </div>

        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
            <svg className="w-5 h-5 mr-2 text-purple-600" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clipRule="evenodd" />
            </svg>
            {t("newDemande.step6.documentType")}
          </h4>
          <div>
            <span className="text-sm font-medium text-gray-500">
              {t("newDemande.step6.documentRequested")}
            </span>
            <p className="text-gray-800 text-lg font-medium">
              {getDocumentTypeLabel(formData.documentTypeId)}
            </p>
          </div>
        </div>

        {formData.documentFiles.length > 0 && (
          <div className="bg-white border border-gray-200 rounded-lg p-6">
            <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
              <svg className="w-5 h-5 mr-2 text-orange-600" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM6.293 6.707a1 1 0 010-1.414l3-3a1 1 0 011.414 0l3 3a1 1 0 01-1.414 1.414L11 5.414V13a1 1 0 11-2 0V5.414L7.707 6.707a1 1 0 01-1.414 0z" clipRule="evenodd" />
              </svg>
              {t("newDemande.step6.supportingDocs")}
            </h4>
            <div className="space-y-2">
              {formData.documentFiles.map((fileName, index) => (
                <div key={index} className="flex items-center space-x-2">
                  <svg
                    className="w-4 h-4 text-green-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <span className="text-gray-800">{fileName}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      <div className="bg-green-50 p-4 rounded-lg">
        <div className="flex items-start">
          <div className="flex-shrink-0">
            <svg className="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-green-800">
              {t("newDemande.step6.readyToSubmit")}
            </h3>
            <p className="mt-2 text-sm text-green-700">
              {t("newDemande.step6.submitInfo")}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Step6Summary;
