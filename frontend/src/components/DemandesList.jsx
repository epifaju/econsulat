import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";

const DemandesList = ({ onRefresh, refreshTrigger }) => {
  const { token } = useAuth();
  const [demandes, setDemandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDemandes();
  }, [token, refreshTrigger]);

  const loadDemandes = async () => {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/demandes/my", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDemandes(data);
        // Notifier le parent que les données ont été chargées
        if (onRefresh) {
          onRefresh(data);
        }
      } else {
        setError("Erreur lors du chargement des demandes");
      }
    } catch (err) {
      setError("Erreur de connexion");
      console.error(err);
    } finally {
      setLoading(false);
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
                  onClick={() => {
                    /* TODO: Voir les détails */
                  }}
                >
                  Voir les détails
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DemandesList;
