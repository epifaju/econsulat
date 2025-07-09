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

const UserDashboard = () => {
  const { user, token } = useAuth();
  const { demandes, loading, error, refreshDemandes, addDemande } =
    useDemandes();
  const [notification, setNotification] = useState(null);
  const [showNewDemandeForm, setShowNewDemandeForm] = useState(false);

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

  const handleGenerateDocument = async (citizenId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/passport/generate/${citizenId}`,
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
          `http://localhost:8080/api/passport/download/${data.fileName}`,
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
          a.download = data.fileName;
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
        }
      } else {
        showNotification(
          "error",
          "Erreur",
          "Impossible de générer le document"
        );
      }
    } catch (err) {
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
      </div>

      {/* Modal Nouvelle Demande */}
      {showNewDemandeForm && (
        <NewDemandeForm
          onClose={handleNewDemandeClose}
          onSuccess={handleNewDemandeSuccess}
        />
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
