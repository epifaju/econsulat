import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useDemandes } from "../hooks/useDemandes";
import {
  DocumentTextIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  EyeIcon,
  DocumentArrowDownIcon,
  PlusIcon,
} from "@heroicons/react/24/outline";
import StatsCard from "./StatsCard";
import SearchAndFilters from "./SearchAndFilters";
import Pagination from "./Pagination";
import Notification from "./Notification";
import NewDemandeForm from "./NewDemandeForm";
import DemandesList from "./DemandesList";
import UserNotifications from "./UserNotifications";

const UserDashboard = () => {
  const { user, token } = useAuth();
  const { demandes, loading, error, refreshDemandes, addDemande } =
    useDemandes();
  const [notification, setNotification] = useState(null);
  const [showNewDemandeForm, setShowNewDemandeForm] = useState(false);
  const [selectedDemande, setSelectedDemande] = useState(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);

  // États pour la pagination et filtres
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(5);
  const [searchTerm, setSearchTerm] = useState("");
  const [filters, setFilters] = useState({});
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  const showNotification = (type, title, message) => {
    setNotification({ type, title, message });
    setTimeout(() => setNotification(null), 5000);
  };

  const handleNewDemandeSuccess = (demande) => {
    setShowNewDemandeForm(false);
    showNotification(
      "success",
      "Succès",
      "Votre demande a été soumise avec succès"
    );
    // Ajouter la nouvelle demande à la liste et rafraîchir
    addDemande(demande);
    refreshDemandes();
  };

  const handleNewDemandeClose = () => {
    setShowNewDemandeForm(false);
  };

  const handleRefreshData = () => {
    refreshDemandes();
    showNotification("info", "Info", "Données actualisées");
  };

  const handleViewDetails = async (demandeId) => {
    try {
      const response = await fetch(
        `http://127.0.0.1:8080/api/demandes/${demandeId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const demande = await response.json();
        setSelectedDemande(demande);
        setShowDetailsModal(true);
      } else {
        showNotification(
          "error",
          "Erreur",
          "Impossible de charger les détails de la demande"
        );
      }
    } catch (err) {
      console.error("Erreur:", err);
      showNotification("error", "Erreur", "Problème de connexion");
    }
  };

  const closeDetailsModal = () => {
    setShowDetailsModal(false);
    setSelectedDemande(null);
  };

  // Calcul des statistiques
  const stats = {
    totalRequests: demandes.length,
    pendingRequests: demandes.filter((d) => d.status === "PENDING").length,
    approvedRequests: demandes.filter((d) => d.status === "APPROVED").length,
    rejectedRequests: demandes.filter((d) => d.status === "REJECTED").length,
  };

  // Filtrage et recherche
  const filteredDemandes = demandes.filter((demande) => {
    const matchesSearch =
      searchTerm === "" ||
      demande.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      demande.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (demande.documentTypeDisplay || demande.documentType)
        .toLowerCase()
        .includes(searchTerm.toLowerCase());

    const matchesFilters = Object.keys(filters).every((key) => {
      if (!filters[key]) return true;
      return demande[key] === filters[key];
    });

    return matchesSearch && matchesFilters;
  });

  // Tri
  const sortedDemandes = [...filteredDemandes].sort((a, b) => {
    if (!sortConfig.key) return 0;

    const aValue = a[sortConfig.key];
    const bValue = b[sortConfig.key];

    if (aValue < bValue) return sortConfig.direction === "asc" ? -1 : 1;
    if (aValue > bValue) return sortConfig.direction === "asc" ? 1 : -1;
    return 0;
  });

  // Pagination
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentDemandes = sortedDemandes.slice(
    indexOfFirstItem,
    indexOfLastItem
  );

  const handleSort = (key) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === "asc" ? "desc" : "asc",
    }));
  };

  const handleGenerateDocument = async (demandeId) => {
    try {
      const response = await fetch(
        `http://127.0.0.1:8080/api/demandes/${demandeId}/generate-document`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        showNotification("success", "Succès", "Document généré avec succès");

        // Télécharger le document
        const downloadResponse = await fetch(
          `http://127.0.0.1:8080/api/demandes/${demandeId}/download-document`,
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

          showNotification(
            "success",
            "Succès",
            "Document téléchargé avec succès"
          );
        } else {
          showNotification(
            "error",
            "Erreur",
            "Impossible de télécharger le document"
          );
        }
      } else {
        const errorData = await response.json();
        showNotification(
          "error",
          "Erreur",
          errorData.error || "Impossible de générer le document"
        );
      }
    } catch (err) {
      console.error("Erreur:", err);
      showNotification("error", "Erreur", "Problème lors de la génération");
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "PENDING":
        return <span className="badge badge-pending">En attente</span>;
      case "APPROVED":
        return <span className="badge badge-approved">Approuvé</span>;
      case "REJECTED":
        return <span className="badge badge-rejected">Rejeté</span>;
      default:
        return <span className="badge badge-pending">{status}</span>;
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

  const filterOptions = {
    status: {
      label: "Statut",
      values: [
        { value: "PENDING", label: "En attente" },
        { value: "APPROVED", label: "Approuvé" },
        { value: "REJECTED", label: "Rejeté" },
      ],
    },
    documentType: {
      label: "Type de document",
      values: [
        { value: "PASSPORT", label: "Passeport" },
        { value: "ID_CARD", label: "Carte d'identité" },
        { value: "DRIVERS_LICENSE", label: "Permis de conduire" },
      ],
    },
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Chargement...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <XCircleIcon className="h-12 w-12 text-red-500 mx-auto" />
          <p className="mt-4 text-gray-600">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Notifications */}
      {notification && (
        <Notification
          type={notification.type}
          title={notification.title}
          message={notification.message}
          onClose={() => setNotification(null)}
        />
      )}

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* En-tête */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            Mon tableau de bord
          </h1>
          <p className="mt-2 text-gray-600">
            Suivez vos demandes de documents et gérez vos informations
            personnelles.
          </p>
        </div>

        {/* Cartes de statistiques */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatsCard
            title="Total Demandes"
            value={stats.totalRequests}
            icon={DocumentTextIcon}
            change="+2"
            changeType="positive"
            description="ce mois"
          />
          <StatsCard
            title="En attente"
            value={stats.pendingRequests}
            icon={ClockIcon}
            change="+1"
            changeType="neutral"
            description="ce mois"
          />
          <StatsCard
            title="Approuvées"
            value={stats.approvedRequests}
            icon={CheckCircleIcon}
            change="+1"
            changeType="positive"
            description="ce mois"
          />
          <StatsCard
            title="Rejetées"
            value={stats.rejectedRequests}
            icon={XCircleIcon}
            change="0"
            changeType="neutral"
            description="ce mois"
          />
        </div>

        {/* Section Demandes */}
        <div className="card">
          <div className="card-header">
            <div className="flex items-center justify-between">
              <h2 className="card-title">Mes Demandes</h2>
              <div className="flex space-x-2">
                <button
                  className="btn-secondary"
                  onClick={handleRefreshData}
                  title="Actualiser les données"
                >
                  <svg
                    className="h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                    />
                  </svg>
                </button>
                <button
                  className="btn-primary"
                  onClick={() => setShowNewDemandeForm(true)}
                >
                  <PlusIcon className="h-4 w-4 mr-2" />
                  Nouvelle demande
                </button>
              </div>
            </div>
          </div>

          {/* Recherche et filtres */}
          <SearchAndFilters
            searchTerm={searchTerm}
            onSearchChange={setSearchTerm}
            filters={filters}
            onFiltersChange={setFilters}
            filterOptions={filterOptions}
            placeholder="Rechercher dans mes demandes..."
          />

          {/* Tableau */}
          <div className="table-container">
            <table className="table">
              <thead className="table-header">
                <tr>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("firstName")}
                  >
                    Nom complet
                    {sortConfig.key === "firstName" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("documentType")}
                  >
                    Type de document
                    {sortConfig.key === "documentType" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("status")}
                  >
                    Statut
                    {sortConfig.key === "status" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("createdAt")}
                  >
                    Date de création
                    {sortConfig.key === "createdAt" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th className="table-header-cell">Actions</th>
                </tr>
              </thead>
              <tbody className="table-body">
                {currentDemandes.map((demande) => (
                  <tr key={demande.id} className="table-row">
                    <td className="table-cell">
                      <div>
                        <div className="font-medium text-gray-900">
                          {demande.firstName} {demande.lastName}
                        </div>
                        <div className="text-gray-500">
                          Demande #{demande.id}
                        </div>
                      </div>
                    </td>
                    <td className="table-cell">
                      <span className="badge badge-user">
                        {demande.documentTypeDisplay || demande.documentType}
                      </span>
                    </td>
                    <td className="table-cell">
                      {getStatusBadge(demande.status)}
                    </td>
                    <td className="table-cell">
                      {new Date(demande.createdAt).toLocaleDateString("fr-FR", {
                        year: "numeric",
                        month: "long",
                        day: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </td>
                    <td className="table-cell">
                      <div className="flex space-x-2">
                        {demande.status === "APPROVED" && (
                          <button
                            onClick={() => handleGenerateDocument(demande.id)}
                            className="btn-success btn-sm"
                            title="Télécharger document"
                          >
                            <DocumentArrowDownIcon className="h-4 w-4" />
                          </button>
                        )}
                        <button
                          className="btn-secondary btn-sm"
                          title="Voir détails"
                          onClick={() => handleViewDetails(demande.id)}
                        >
                          <EyeIcon className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {filteredDemandes.length > 0 && (
            <Pagination
              currentPage={currentPage}
              totalPages={Math.ceil(filteredDemandes.length / itemsPerPage)}
              onPageChange={setCurrentPage}
              itemsPerPage={itemsPerPage}
              totalItems={filteredDemandes.length}
            />
          )}

          {filteredDemandes.length === 0 && (
            <div className="text-center py-8">
              <DocumentTextIcon className="h-12 w-12 text-gray-400 mx-auto" />
              <p className="mt-2 text-gray-500">Aucune demande trouvée</p>
              <button
                className="btn-primary mt-4"
                onClick={() => setShowNewDemandeForm(true)}
              >
                <PlusIcon className="h-4 w-4 mr-2" />
                Créer votre première demande
              </button>
            </div>
          )}
        </div>

        {/* Section Notifications */}
        <div className="mt-8">
          <UserNotifications />
        </div>
      </div>

      {/* Modal Nouvelle Demande */}
      {showNewDemandeForm && (
        <NewDemandeForm
          onClose={handleNewDemandeClose}
          onSuccess={handleNewDemandeSuccess}
        />
      )}

      {/* Modal Détails Demande */}
      {showDetailsModal && selectedDemande && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-medium text-gray-900">
                  Détails de la demande #{selectedDemande.id}
                </h3>
                <button
                  onClick={closeDetailsModal}
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
                      {new Date(selectedDemande.createdAt).toLocaleDateString(
                        "fr-FR",
                        {
                          year: "numeric",
                          month: "long",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        }
                      )}
                    </div>
                    <div>
                      <span className="font-medium">Dernière mise à jour:</span>{" "}
                      {new Date(selectedDemande.updatedAt).toLocaleDateString(
                        "fr-FR",
                        {
                          year: "numeric",
                          month: "long",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        }
                      )}
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
                <div className="bg-gray-50 p-4 rounded-lg">
                  <h4 className="font-medium text-gray-900 mb-2">Adresse</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="font-medium">Rue:</span>{" "}
                      {selectedDemande.streetNumber}{" "}
                      {selectedDemande.streetName}
                    </div>
                    <div>
                      <span className="font-medium">Boîte:</span>{" "}
                      {selectedDemande.boxNumber || "N/A"}
                    </div>
                    <div>
                      <span className="font-medium">Code postal:</span>{" "}
                      {selectedDemande.postalCode}
                    </div>
                    <div>
                      <span className="font-medium">Ville:</span>{" "}
                      {selectedDemande.city}
                    </div>
                    <div>
                      <span className="font-medium">Pays:</span>{" "}
                      {selectedDemande.country}
                    </div>
                  </div>
                </div>

                {/* Filiation */}
                {(selectedDemande.fatherFirstName ||
                  selectedDemande.motherFirstName) && (
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <h4 className="font-medium text-gray-900 mb-2">
                      Filiation
                    </h4>
                    <div className="space-y-4">
                      {selectedDemande.fatherFirstName && (
                        <div>
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
                  onClick={closeDetailsModal}
                  className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
                >
                  Fermer
                </button>
                {selectedDemande.status === "APPROVED" && (
                  <button
                    className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 font-medium"
                    onClick={() => handleGenerateDocument(selectedDemande.id)}
                  >
                    Générer document
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Notification */}
      {notification && (
        <Notification
          type={notification.type}
          title={notification.title}
          message={notification.message}
          onClose={() => setNotification(null)}
        />
      )}
    </div>
  );
};

export default UserDashboard;
