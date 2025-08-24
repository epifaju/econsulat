import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";

const DemandesList = ({ onRefresh, refreshTrigger }) => {
  const { token } = useAuth();
  const [demandes, setDemandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedDemande, setSelectedDemande] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [generating, setGenerating] = useState(false);

  useEffect(() => {
    loadDemandes();
  }, [token, refreshTrigger]);

  const loadDemandes = async () => {
    try {
      setLoading(true);
      console.log("🔍 Debug - Token utilisé pour loadDemandes:", token);

      const response = await fetch("http://127.0.0.1:8080/api/demandes/my", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      console.log(
        "🔍 Debug - Réponse loadDemandes:",
        response.status,
        response.statusText
      );

      if (response.ok) {
        const data = await response.json();
        setDemandes(data);
        // Notifier le parent que les données ont été chargées
        if (onRefresh) {
          onRefresh(data);
        }
      } else if (response.status === 403) {
        setError("Erreur d'authentification. Veuillez vous reconnecter.");
        // Rediriger vers la page de connexion
        window.location.href = "/login";
      } else {
        // Essayer de récupérer le message d'erreur, sinon utiliser un message par défaut
        let errorMessage = "Erreur lors du chargement des demandes";
        try {
          const errorData = await response.json();
          if (errorData && errorData.message) {
            errorMessage = errorData.message;
          }
        } catch (parseError) {
          console.warn(
            "⚠️ Impossible de parser la réponse d'erreur:",
            parseError
          );
          // Utiliser le message par défaut
        }
        setError(errorMessage);
      }
    } catch (err) {
      console.error("❌ Erreur de connexion dans loadDemandes:", err);
      setError("Erreur de connexion au serveur");
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetails = async (demandeId) => {
    try {
      console.log("🔍 Debug - Token utilisé pour handleViewDetails:", token);

      const response = await fetch(
        `http://127.0.0.1:8080/api/demandes/${demandeId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log(
        "🔍 Debug - Réponse handleViewDetails:",
        response.status,
        response.statusText
      );

      if (response.ok) {
        const demande = await response.json();
        setSelectedDemande(demande);
        setShowModal(true);
      } else if (response.status === 403) {
        alert("Erreur d'authentification. Veuillez vous reconnecter.");
        window.location.href = "/login";
      } else {
        // Essayer de récupérer le message d'erreur, sinon utiliser un message par défaut
        let errorMessage =
          "Erreur lors du chargement des détails de la demande";
        try {
          const errorData = await response.json();
          if (errorData && errorData.message) {
            errorMessage = errorData.message;
          }
        } catch (parseError) {
          console.warn(
            "⚠️ Impossible de parser la réponse d'erreur:",
            parseError
          );
          // Utiliser le message par défaut
        }
        alert(errorMessage);
      }
    } catch (err) {
      console.error("❌ Erreur dans handleViewDetails:", err);
      alert("Erreur de connexion au serveur");
    }
  };

  const handleGenerateDocument = async (demandeId) => {
    try {
      setGenerating(true);
      const response = await fetch(
        `http://localhost:8080/api/user/documents/generate?demandeId=${demandeId}&documentTypeId=1`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        // Télécharger le document
        const downloadResponse = await fetch(
          `http://localhost:8080/api/user/documents/download/${data.id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (downloadResponse.ok) {
          const blob = await downloadResponse.blob();
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement("a");
          a.href = url;
          a.download = data.fileName || "document.docx";
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);

          onNotification(
            "success",
            "Succès",
            "Document généré et téléchargé avec succès !"
          );
        } else {
          onNotification(
            "error",
            "Erreur",
            "Impossible de télécharger le document"
          );
        }
      } else {
        const errorData = await response.json();
        onNotification(
          "error",
          "Erreur",
          errorData.error || "Impossible de générer le document"
        );
      }
    } catch (err) {
      console.error("Erreur:", err);
      onNotification(
        "error",
        "Erreur",
        "Problème de connexion lors de la génération"
      );
    } finally {
      setGenerating(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-yellow-100 text-yellow-800";
      case "APPROVED":
        return "bg-green-100 text-green-800";
      case "REJECTED":
        return "bg-red-100 text-red-800";
      case "COMPLETED":
        return "bg-blue-100 text-blue-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("fr-FR", {
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedDemande(null);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4">
        <div className="flex">
          <div className="flex-shrink-0">
            <svg
              className="h-5 w-5 text-red-400"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                clipRule="evenodd"
              />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-red-800">Erreur</h3>
            <div className="mt-2 text-sm text-red-700">
              <p>{error}</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (demandes.length === 0) {
    return (
      <div className="text-center py-8">
        <svg
          className="mx-auto h-12 w-12 text-gray-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
          />
        </svg>
        <h3 className="mt-2 text-sm font-medium text-gray-900">
          Aucune demande
        </h3>
        <p className="mt-1 text-sm text-gray-500">
          Vous n'avez pas encore soumis de demande de document.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-lg font-medium text-gray-900">Mes demandes</h2>
        <button
          onClick={loadDemandes}
          className="text-sm text-blue-600 hover:text-blue-800"
        >
          Actualiser
        </button>
      </div>

      <div className="space-y-4">
        {demandes.map((demande) => (
          <div
            key={demande.id}
            className="bg-white border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow"
          >
            <div className="flex justify-between items-start">
              <div className="flex-1">
                <div className="flex items-center space-x-3 mb-2">
                  <h3 className="text-lg font-medium text-gray-900">
                    {demande.documentTypeDisplay}
                  </h3>
                  <span
                    className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                      demande.status
                    )}`}
                  >
                    {demande.statusDisplay}
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-600">
                  <div>
                    <span className="font-medium">Demandeur:</span>{" "}
                    {demande.firstName} {demande.lastName}
                  </div>
                  <div>
                    <span className="font-medium">Soumis le:</span>{" "}
                    {formatDate(demande.createdAt)}
                  </div>
                  <div>
                    <span className="font-medium">Dernière mise à jour:</span>{" "}
                    {formatDate(demande.updatedAt)}
                  </div>
                  <div>
                    <span className="font-medium">Numéro de demande:</span> #
                    {demande.id}
                  </div>
                </div>

                {demande.documentFiles && demande.documentFiles.length > 0 && (
                  <div className="mt-3">
                    <span className="text-sm font-medium text-gray-600">
                      Documents joints:
                    </span>
                    <div className="mt-1 flex flex-wrap gap-2">
                      {demande.documentFiles.map((file, index) => (
                        <span
                          key={index}
                          className="inline-flex items-center px-2 py-1 rounded-md text-xs font-medium bg-gray-100 text-gray-800"
                        >
                          <svg
                            className="w-3 h-3 mr-1"
                            fill="currentColor"
                            viewBox="0 0 20 20"
                          >
                            <path
                              fillRule="evenodd"
                              d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z"
                              clipRule="evenodd"
                            />
                          </svg>
                          {file}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              <div className="flex flex-col items-end space-y-2">
                <button
                  className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                  onClick={() => handleViewDetails(demande.id)}
                >
                  Voir les détails
                </button>

                {demande.status === "APPROVED" && (
                  <button
                    className={`text-sm font-medium px-3 py-1 rounded-md ${
                      generating
                        ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                        : "bg-green-600 text-white hover:bg-green-700"
                    }`}
                    onClick={() => handleGenerateDocument(demande.id)}
                    disabled={generating}
                  >
                    {generating ? (
                      <span className="flex items-center">
                        <svg
                          className="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                        >
                          <circle
                            className="opacity-25"
                            cx="12"
                            cy="12"
                            r="10"
                            stroke="currentColor"
                            strokeWidth="4"
                          ></circle>
                          <path
                            className="opacity-75"
                            fill="currentColor"
                            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                          ></path>
                        </svg>
                        Génération...
                      </span>
                    ) : (
                      "Générer document"
                    )}
                  </button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Modal de détails */}
      {showModal && selectedDemande && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900">
                  Détails de la demande #{selectedDemande.id}
                </h3>
                <button
                  onClick={closeModal}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <svg
                    className="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M6 18L18 6M6 6l12 12"
                    />
                  </svg>
                </button>
              </div>

              <div className="space-y-4">
                {/* Informations générales */}
                <div className="bg-gray-50 p-4 rounded-lg">
                  <h4 className="font-medium text-gray-900 mb-2">
                    Informations générales
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="font-medium">Type de document:</span>{" "}
                      {selectedDemande.documentTypeDisplay}
                    </div>
                    <div>
                      <span className="font-medium">Statut:</span>
                      <span
                        className={`ml-2 inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(
                          selectedDemande.status
                        )}`}
                      >
                        {selectedDemande.statusDisplay}
                      </span>
                    </div>
                    <div>
                      <span className="font-medium">Date de création:</span>{" "}
                      {formatDate(selectedDemande.createdAt)}
                    </div>
                    <div>
                      <span className="font-medium">Dernière mise à jour:</span>{" "}
                      {formatDate(selectedDemande.updatedAt)}
                    </div>
                  </div>
                </div>

                {/* Informations personnelles */}
                <div className="bg-gray-50 p-4 rounded-lg">
                  <h4 className="font-medium text-gray-900 mb-2">
                    Informations personnelles
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="font-medium">Nom:</span>{" "}
                      {selectedDemande.lastName}
                    </div>
                    <div>
                      <span className="font-medium">Prénom:</span>{" "}
                      {selectedDemande.firstName}
                    </div>
                    <div>
                      <span className="font-medium">Date de naissance:</span>{" "}
                      {selectedDemande.birthDate}
                    </div>
                    <div>
                      <span className="font-medium">Lieu de naissance:</span>{" "}
                      {selectedDemande.birthPlace}
                    </div>
                    <div>
                      <span className="font-medium">Pays de naissance:</span>{" "}
                      {selectedDemande.birthCountry}
                    </div>
                    <div>
                      <span className="font-medium">Civilité:</span>{" "}
                      {selectedDemande.civilite}
                    </div>
                  </div>
                </div>

                {/* Adresse */}
                {selectedDemande.adresse && (
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <h4 className="font-medium text-gray-900 mb-2">Adresse</h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                      <div>
                        <span className="font-medium">Rue:</span>{" "}
                        {selectedDemande.adresse.streetNumber}{" "}
                        {selectedDemande.adresse.streetName}
                      </div>
                      <div>
                        <span className="font-medium">Ville:</span>{" "}
                        {selectedDemande.adresse.city}
                      </div>
                      <div>
                        <span className="font-medium">Code postal:</span>{" "}
                        {selectedDemande.adresse.postalCode}
                      </div>
                      <div>
                        <span className="font-medium">Pays:</span>{" "}
                        {selectedDemande.adresse.country}
                      </div>
                      {selectedDemande.adresse.boxNumber && (
                        <div>
                          <span className="font-medium">Boîte:</span>{" "}
                          {selectedDemande.adresse.boxNumber}
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Filiation */}
                {(selectedDemande.fatherFirstName ||
                  selectedDemande.motherFirstName) && (
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <h4 className="font-medium text-gray-900 mb-2">
                      Filiation
                    </h4>
                    {selectedDemande.fatherFirstName && (
                      <div className="mb-3">
                        <h5 className="font-medium text-sm text-gray-700 mb-1">
                          Père
                        </h5>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
                          <div>
                            <span className="font-medium">Nom:</span>{" "}
                            {selectedDemande.fatherLastName}
                          </div>
                          <div>
                            <span className="font-medium">Prénom:</span>{" "}
                            {selectedDemande.fatherFirstName}
                          </div>
                          <div>
                            <span className="font-medium">
                              Date de naissance:
                            </span>{" "}
                            {selectedDemande.fatherBirthDate}
                          </div>
                          <div>
                            <span className="font-medium">
                              Lieu de naissance:
                            </span>{" "}
                            {selectedDemande.fatherBirthPlace}
                          </div>
                        </div>
                      </div>
                    )}
                    {selectedDemande.motherFirstName && (
                      <div>
                        <h5 className="font-medium text-sm text-gray-700 mb-1">
                          Mère
                        </h5>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
                          <div>
                            <span className="font-medium">Nom:</span>{" "}
                            {selectedDemande.motherLastName}
                          </div>
                          <div>
                            <span className="font-medium">Prénom:</span>{" "}
                            {selectedDemande.motherFirstName}
                          </div>
                          <div>
                            <span className="font-medium">
                              Date de naissance:
                            </span>{" "}
                            {selectedDemande.motherBirthDate}
                          </div>
                          <div>
                            <span className="font-medium">
                              Lieu de naissance:
                            </span>{" "}
                            {selectedDemande.motherBirthPlace}
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                )}

                {/* Documents joints */}
                {selectedDemande.documentFiles &&
                  selectedDemande.documentFiles.length > 0 && (
                    <div className="bg-gray-50 p-4 rounded-lg">
                      <h4 className="font-medium text-gray-900 mb-2">
                        Documents joints
                      </h4>
                      <div className="flex flex-wrap gap-2">
                        {selectedDemande.documentFiles.map((file, index) => (
                          <span
                            key={index}
                            className="inline-flex items-center px-3 py-1 rounded-md text-sm font-medium bg-blue-100 text-blue-800"
                          >
                            <svg
                              className="w-4 h-4 mr-2"
                              fill="currentColor"
                              viewBox="0 0 20 20"
                            >
                              <path
                                fillRule="evenodd"
                                d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z"
                                clipRule="evenodd"
                              />
                            </svg>
                            {file}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
              </div>

              <div className="flex justify-end mt-6 space-x-3">
                <button
                  onClick={closeModal}
                  className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
                >
                  Fermer
                </button>
                {selectedDemande.status === "APPROVED" && (
                  <button
                    className={`px-4 py-2 rounded-md font-medium ${
                      generating
                        ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                        : "bg-green-600 text-white hover:bg-green-700"
                    }`}
                    onClick={() => handleGenerateDocument(selectedDemande.id)}
                    disabled={generating}
                  >
                    {generating ? "Génération..." : "Générer document"}
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DemandesList;
