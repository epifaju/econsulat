import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";
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

const UserDashboard = () => {
  const { user, token } = useAuth();
  const [citizens, setCitizens] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [notification, setNotification] = useState(null);

  // États pour la pagination et filtres
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(5);
  const [searchTerm, setSearchTerm] = useState("");
  const [filters, setFilters] = useState({});
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        "http://localhost:8080/api/citizens/my-requests",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        setCitizens(data);
      } else {
        setError("Erreur lors du chargement des données");
        showNotification(
          "error",
          "Erreur",
          "Impossible de charger les données"
        );
      }
    } catch (err) {
      setError("Erreur de connexion");
      showNotification("error", "Erreur", "Problème de connexion au serveur");
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (type, title, message) => {
    setNotification({ type, title, message });
    setTimeout(() => setNotification(null), 5000);
  };

  // Calcul des statistiques
  const stats = {
    totalRequests: citizens.length,
    pendingRequests: citizens.filter((c) => c.status === "PENDING").length,
    approvedRequests: citizens.filter((c) => c.status === "APPROVED").length,
    rejectedRequests: citizens.filter((c) => c.status === "REJECTED").length,
  };

  // Filtrage et recherche
  const filteredCitizens = citizens.filter((citizen) => {
    const matchesSearch =
      searchTerm === "" ||
      citizen.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      citizen.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      citizen.documentType.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesFilters = Object.keys(filters).every((key) => {
      if (!filters[key]) return true;
      return citizen[key] === filters[key];
    });

    return matchesSearch && matchesFilters;
  });

  // Tri
  const sortedCitizens = [...filteredCitizens].sort((a, b) => {
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
  const currentCitizens = sortedCitizens.slice(
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
              <button className="btn-primary">
                <PlusIcon className="h-4 w-4 mr-2" />
                Nouvelle demande
              </button>
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
                {currentCitizens.map((citizen) => (
                  <tr key={citizen.id} className="table-row">
                    <td className="table-cell">
                      <div>
                        <div className="font-medium text-gray-900">
                          {citizen.firstName} {citizen.lastName}
                        </div>
                        <div className="text-gray-500">
                          {citizen.birthPlace}
                        </div>
                      </div>
                    </td>
                    <td className="table-cell">
                      <span className="badge badge-user">
                        {citizen.documentType}
                      </span>
                    </td>
                    <td className="table-cell">
                      {getStatusBadge(citizen.status)}
                    </td>
                    <td className="table-cell">
                      {new Date(citizen.createdAt).toLocaleDateString("fr-FR")}
                    </td>
                    <td className="table-cell">
                      <div className="flex space-x-2">
                        {citizen.status === "APPROVED" && (
                          <button
                            onClick={() => handleGenerateDocument(citizen.id)}
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
          {filteredCitizens.length > 0 && (
            <Pagination
              currentPage={currentPage}
              totalPages={Math.ceil(filteredCitizens.length / itemsPerPage)}
              onPageChange={setCurrentPage}
              itemsPerPage={itemsPerPage}
              totalItems={filteredCitizens.length}
            />
          )}

          {filteredCitizens.length === 0 && (
            <div className="text-center py-8">
              <DocumentTextIcon className="h-12 w-12 text-gray-400 mx-auto" />
              <p className="mt-2 text-gray-500">Aucune demande trouvée</p>
              <button className="btn-primary mt-4">
                <PlusIcon className="h-4 w-4 mr-2" />
                Créer votre première demande
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserDashboard;
