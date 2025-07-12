import React, { useState, useEffect } from "react";
import {
  XMarkIcon,
  UserIcon,
  MapPinIcon,
  DocumentTextIcon,
  PaperClipIcon,
} from "@heroicons/react/24/outline";

const AdminDemandeEditModal = ({
  demande,
  isOpen,
  onClose,
  onSave,
  token,
  onNotification,
}) => {
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);
  const [referenceData, setReferenceData] = useState({
    civilites: [],
    pays: [],
    documentTypes: [],
  });

  useEffect(() => {
    if (isOpen && demande) {
      // Convertir la demande en format de formulaire
      const formDataFromDemande = {
        civiliteId: demande.civiliteId,
        firstName: demande.firstName,
        lastName: demande.lastName,
        birthDate: demande.birthDate,
        birthPlace: demande.birthPlace,
        birthCountryId: demande.birthCountryId,
        streetName: demande.streetName,
        streetNumber: demande.streetNumber,
        boxNumber: demande.boxNumber,
        postalCode: demande.postalCode,
        city: demande.city,
        countryId: demande.countryId,
        fatherFirstName: demande.fatherFirstName,
        fatherLastName: demande.fatherLastName,
        fatherBirthDate: demande.fatherBirthDate,
        fatherBirthPlace: demande.fatherBirthPlace,
        fatherBirthCountryId: demande.fatherBirthCountryId,
        motherFirstName: demande.motherFirstName,
        motherLastName: demande.motherLastName,
        motherBirthDate: demande.motherBirthDate,
        motherBirthPlace: demande.motherBirthPlace,
        motherBirthCountryId: demande.motherBirthCountryId,
        documentType: demande.documentType,
        documentFiles: demande.documentFiles || [],
      };

      console.log("🔍 Debug - Demande reçue:", demande);
      console.log(
        "🔍 Debug - DocumentType de la demande:",
        demande.documentType
      );
      console.log("🔍 Debug - FormData créé:", formDataFromDemande);

      setFormData(formDataFromDemande);
    }
  }, [isOpen, demande]);

  useEffect(() => {
    if (isOpen) {
      loadReferenceData();
    }
  }, [isOpen]);

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
        setReferenceData((prev) => ({ ...prev, civilites: civilitesData }));
      }

      if (paysRes.ok) {
        const paysData = await paysRes.json();
        setReferenceData((prev) => ({ ...prev, pays: paysData }));
      }

      if (typesRes.ok) {
        const typesData = await typesRes.json();
        console.log("🔍 Debug - Types de documents chargés:", typesData);
        setReferenceData((prev) => ({ ...prev, documentTypes: typesData }));
      }
    } catch (err) {
      onNotification(
        "error",
        "Erreur",
        "Impossible de charger les données de référence"
      );
      console.error(err);
    }
  };

  const updateFormData = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/api/admin/demandes/${demande.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(formData),
        }
      );

      if (response.ok) {
        const updatedDemande = await response.json();
        onNotification("success", "Succès", "Demande mise à jour avec succès");
        onSave(updatedDemande);
        onClose();
      } else {
        const errorData = await response.json().catch(() => ({}));
        const errorMessage =
          errorData?.message || "Erreur lors de la mise à jour";
        onNotification("error", "Erreur", errorMessage);
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème de connexion");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen || !demande) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-4xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-semibold text-gray-900">
            Modifier la demande
          </h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <XMarkIcon className="h-6 w-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Informations personnelles */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium text-gray-900 mb-4 flex items-center">
              <UserIcon className="h-5 w-5 mr-2" />
              Informations personnelles
            </h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Civilité
                </label>
                <select
                  value={formData.civiliteId || ""}
                  onChange={(e) =>
                    updateFormData("civiliteId", parseInt(e.target.value))
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Sélectionner</option>
                  {referenceData.civilites.map((civilite) => (
                    <option key={civilite.id} value={civilite.id}>
                      {civilite.libelle}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Prénom
                </label>
                <input
                  type="text"
                  value={formData.firstName || ""}
                  onChange={(e) => updateFormData("firstName", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nom
                </label>
                <input
                  type="text"
                  value={formData.lastName || ""}
                  onChange={(e) => updateFormData("lastName", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Date de naissance
                </label>
                <input
                  type="date"
                  value={formData.birthDate || ""}
                  onChange={(e) => updateFormData("birthDate", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Lieu de naissance
                </label>
                <input
                  type="text"
                  value={formData.birthPlace || ""}
                  onChange={(e) => updateFormData("birthPlace", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Pays de naissance
                </label>
                <select
                  value={formData.birthCountryId || ""}
                  onChange={(e) =>
                    updateFormData("birthCountryId", parseInt(e.target.value))
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Sélectionner</option>
                  {referenceData.pays.map((pays) => (
                    <option key={pays.id} value={pays.id}>
                      {pays.libelle}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Adresse */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium text-gray-900 mb-4 flex items-center">
              <MapPinIcon className="h-5 w-5 mr-2" />
              Adresse
            </h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Rue
                </label>
                <input
                  type="text"
                  value={formData.streetName || ""}
                  onChange={(e) => updateFormData("streetName", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Numéro
                </label>
                <input
                  type="text"
                  value={formData.streetNumber || ""}
                  onChange={(e) =>
                    updateFormData("streetNumber", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Boîte
                </label>
                <input
                  type="text"
                  value={formData.boxNumber || ""}
                  onChange={(e) => updateFormData("boxNumber", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Code postal
                </label>
                <input
                  type="text"
                  value={formData.postalCode || ""}
                  onChange={(e) => updateFormData("postalCode", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Ville
                </label>
                <input
                  type="text"
                  value={formData.city || ""}
                  onChange={(e) => updateFormData("city", e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Pays
                </label>
                <select
                  value={formData.countryId || ""}
                  onChange={(e) =>
                    updateFormData("countryId", parseInt(e.target.value))
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Sélectionner</option>
                  {referenceData.pays.map((pays) => (
                    <option key={pays.id} value={pays.id}>
                      {pays.libelle}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Filiation - Père */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium text-gray-900 mb-4">Filiation - Père</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Prénom du père
                </label>
                <input
                  type="text"
                  value={formData.fatherFirstName || ""}
                  onChange={(e) =>
                    updateFormData("fatherFirstName", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nom du père
                </label>
                <input
                  type="text"
                  value={formData.fatherLastName || ""}
                  onChange={(e) =>
                    updateFormData("fatherLastName", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Date de naissance du père
                </label>
                <input
                  type="date"
                  value={formData.fatherBirthDate || ""}
                  onChange={(e) =>
                    updateFormData("fatherBirthDate", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Lieu de naissance du père
                </label>
                <input
                  type="text"
                  value={formData.fatherBirthPlace || ""}
                  onChange={(e) =>
                    updateFormData("fatherBirthPlace", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Pays de naissance du père
                </label>
                <select
                  value={formData.fatherBirthCountryId || ""}
                  onChange={(e) =>
                    updateFormData(
                      "fatherBirthCountryId",
                      parseInt(e.target.value)
                    )
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Sélectionner</option>
                  {referenceData.pays.map((pays) => (
                    <option key={pays.id} value={pays.id}>
                      {pays.libelle}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Filiation - Mère */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium text-gray-900 mb-4">Filiation - Mère</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Prénom de la mère
                </label>
                <input
                  type="text"
                  value={formData.motherFirstName || ""}
                  onChange={(e) =>
                    updateFormData("motherFirstName", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nom de la mère
                </label>
                <input
                  type="text"
                  value={formData.motherLastName || ""}
                  onChange={(e) =>
                    updateFormData("motherLastName", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Date de naissance de la mère
                </label>
                <input
                  type="date"
                  value={formData.motherBirthDate || ""}
                  onChange={(e) =>
                    updateFormData("motherBirthDate", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Lieu de naissance de la mère
                </label>
                <input
                  type="text"
                  value={formData.motherBirthPlace || ""}
                  onChange={(e) =>
                    updateFormData("motherBirthPlace", e.target.value)
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Pays de naissance de la mère
                </label>
                <select
                  value={formData.motherBirthCountryId || ""}
                  onChange={(e) =>
                    updateFormData(
                      "motherBirthCountryId",
                      parseInt(e.target.value)
                    )
                  }
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Sélectionner</option>
                  {referenceData.pays.map((pays) => (
                    <option key={pays.id} value={pays.id}>
                      {pays.libelle}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Type de document */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium text-gray-900 mb-4 flex items-center">
              <DocumentTextIcon className="h-5 w-5 mr-2" />
              Type de document
            </h4>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Type de document
              </label>
              <select
                value={formData.documentType || ""}
                onChange={(e) => updateFormData("documentType", e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                required
              >
                <option value="">Sélectionner</option>
                {referenceData.documentTypes.map((type) => (
                  <option key={type.value} value={type.value}>
                    {type.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Documents */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium text-gray-900 mb-4 flex items-center">
              <PaperClipIcon className="h-5 w-5 mr-2" />
              Documents joints
            </h4>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Documents (séparés par des virgules)
              </label>
              <input
                type="text"
                value={formData.documentFiles?.join(", ") || ""}
                onChange={(e) =>
                  updateFormData(
                    "documentFiles",
                    e.target.value.split(", ").filter((f) => f.trim())
                  )
                }
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                placeholder="document1.pdf, document2.jpg"
              />
            </div>
          </div>

          {/* Boutons */}
          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50"
            >
              Annuler
            </button>
            <button
              type="submit"
              disabled={loading}
              className={`px-4 py-2 text-sm text-white rounded-md ${
                loading
                  ? "bg-gray-400 cursor-not-allowed"
                  : "bg-blue-600 hover:bg-blue-700"
              }`}
            >
              {loading ? "Mise à jour..." : "Mettre à jour"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AdminDemandeEditModal;
