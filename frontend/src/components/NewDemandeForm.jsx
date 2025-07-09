import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";
import Step1PersonalInfo from "./demande/Step1PersonalInfo";
import Step2Address from "./demande/Step2Address";
import Step3Filiation from "./demande/Step3Filiation";
import Step4DocumentType from "./demande/Step4DocumentType";
import Step5Documents from "./demande/Step5Documents";
import Step6Summary from "./demande/Step6Summary";

const NewDemandeForm = ({ onClose, onSuccess }) => {
  const { token } = useAuth();
  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    // Étape 1: Informations personnelles
    civiliteId: "",
    firstName: "",
    lastName: "",
    birthDate: "",
    birthPlace: "",
    birthCountryId: "",

    // Étape 2: Adresse
    streetName: "",
    streetNumber: "",
    boxNumber: "",
    postalCode: "",
    city: "",
    countryId: "",

    // Étape 3: Filiation - Père
    fatherFirstName: "",
    fatherLastName: "",
    fatherBirthDate: "",
    fatherBirthPlace: "",
    fatherBirthCountryId: "",

    // Étape 3: Filiation - Mère
    motherFirstName: "",
    motherLastName: "",
    motherBirthDate: "",
    motherBirthPlace: "",
    motherBirthCountryId: "",

    // Étape 4: Type de document
    documentType: "",

    // Étape 5: Documents
    documentFiles: [],
  });

  const [civilites, setCivilites] = useState([]);
  const [pays, setPays] = useState([]);
  const [documentTypes, setDocumentTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Charger les données de référence
  useEffect(() => {
    const loadReferenceData = async () => {
      try {
        const [civilitesRes, paysRes, typesRes] = await Promise.all([
          fetch("http://localhost:8080/api/demandes/civilites", {
            headers: { Authorization: `Bearer ${token}` },
          }),
          fetch("http://localhost:8080/api/demandes/pays", {
            headers: { Authorization: `Bearer ${token}` },
          }),
          fetch("http://localhost:8080/api/demandes/document-types", {
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

        if (civilitesRes.ok) {
          const civilitesData = await civilitesRes.json();
          setCivilites(civilitesData);
        }

        if (paysRes.ok) {
          const paysData = await paysRes.json();
          setPays(paysData);
        }

        if (typesRes.ok) {
          const typesData = await typesRes.json();
          setDocumentTypes(typesData);
        }
      } catch (err) {
        setError("Erreur lors du chargement des données de référence");
        console.error(err);
      }
    };

    loadReferenceData();
  }, [token]);

  const updateFormData = (newData) => {
    setFormData((prev) => ({ ...prev, ...newData }));
  };

  const nextStep = () => {
    if (currentStep < 6) {
      setCurrentStep(currentStep + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const submitDemande = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/api/demandes", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        const result = await response.json();
        onSuccess(result);
        onClose();
      } else {
        const errorData = await response.json();
        setError(
          errorData.message || "Erreur lors de la soumission de la demande"
        );
      }
    } catch (err) {
      setError("Erreur de connexion");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const renderStep = () => {
    switch (currentStep) {
      case 1:
        return (
          <Step1PersonalInfo
            formData={formData}
            updateFormData={updateFormData}
            civilites={civilites}
            pays={pays}
          />
        );
      case 2:
        return (
          <Step2Address
            formData={formData}
            updateFormData={updateFormData}
            pays={pays}
          />
        );
      case 3:
        return (
          <Step3Filiation
            formData={formData}
            updateFormData={updateFormData}
            pays={pays}
          />
        );
      case 4:
        return (
          <Step4DocumentType
            formData={formData}
            updateFormData={updateFormData}
            documentTypes={documentTypes}
          />
        );
      case 5:
        return (
          <Step5Documents formData={formData} updateFormData={updateFormData} />
        );
      case 6:
        return (
          <Step6Summary
            formData={formData}
            civilites={civilites}
            pays={pays}
            documentTypes={documentTypes}
            onSubmit={submitDemande}
            loading={loading}
          />
        );
      default:
        return null;
    }
  };

  const getStepTitle = () => {
    const titles = {
      1: "Informations personnelles",
      2: "Adresse",
      3: "Filiation",
      4: "Type de document",
      5: "Documents justificatifs",
      6: "Récapitulatif",
    };
    return titles[currentStep] || "";
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="bg-blue-600 text-white p-6 rounded-t-lg">
          <div className="flex justify-between items-center">
            <h2 className="text-2xl font-bold">Nouvelle demande</h2>
            <button
              onClick={onClose}
              className="text-white hover:text-gray-200 text-xl"
            >
              ×
            </button>
          </div>

          {/* Progress bar */}
          <div className="mt-4">
            <div className="flex justify-between mb-2">
              {[1, 2, 3, 4, 5, 6].map((step) => (
                <div
                  key={step}
                  className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${
                    step <= currentStep
                      ? "bg-white text-blue-600"
                      : "bg-blue-400 text-white"
                  }`}
                >
                  {step}
                </div>
              ))}
            </div>
            <div className="w-full bg-blue-400 rounded-full h-2">
              <div
                className="bg-white h-2 rounded-full transition-all duration-300"
                style={{ width: `${(currentStep / 6) * 100}%` }}
              ></div>
            </div>
          </div>

          <p className="mt-2 text-blue-100">{getStepTitle()}</p>
        </div>

        {/* Content */}
        <div className="p-6">
          {error && (
            <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded">
              {error}
            </div>
          )}

          {renderStep()}

          {/* Navigation buttons */}
          <div className="flex justify-between mt-6 pt-6 border-t">
            <button
              onClick={prevStep}
              disabled={currentStep === 1}
              className={`px-6 py-2 rounded-lg font-medium ${
                currentStep === 1
                  ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                  : "bg-gray-500 text-white hover:bg-gray-600"
              }`}
            >
              Précédent
            </button>

            {currentStep < 6 ? (
              <button
                onClick={nextStep}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700"
              >
                Suivant
              </button>
            ) : (
              <button
                onClick={submitDemande}
                disabled={loading}
                className={`px-6 py-2 rounded-lg font-medium ${
                  loading
                    ? "bg-gray-400 text-gray-600 cursor-not-allowed"
                    : "bg-green-600 text-white hover:bg-green-700"
                }`}
              >
                {loading ? "Soumission..." : "Soumettre la demande"}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default NewDemandeForm;
