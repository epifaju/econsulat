import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useAuth } from "../contexts/AuthContext";
import { useDemandes } from "../hooks/useDemandes";
import API_CONFIG from "../config/api";
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
  const { t, i18n } = useTranslation();
  const dateLocale = i18n.language?.startsWith("pt") ? "pt-PT" : "fr-FR";
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

  // Nouvel état pour les types de documents
  const [documentTypes, setDocumentTypes] = useState([]);
  const [documentTypesLoading, setDocumentTypesLoading] = useState(false);

  const getDefaultDocumentTypes = () => [
    { value: "PASSEPORT", label: t("dashboard.filters.passport"), translationKey: "dashboard.filters.passport", aliases: ["passeport", "passport"] },
    { value: "ACTE_NAISSANCE", label: t("dashboard.filters.birthCertificate"), translationKey: "dashboard.filters.birthCertificate", aliases: ["acte de naissance", "actenaissance", "naissance"] },
    { value: "CERTIFICAT_MARIAGE", label: t("dashboard.filters.marriageCertificate"), translationKey: "dashboard.filters.marriageCertificate", aliases: ["certificat de mariage", "certificatmariage", "mariage"] },
    { value: "CARTE_IDENTITE", label: t("dashboard.filters.idCard"), translationKey: "dashboard.filters.idCard", aliases: ["carte d'identite", "carteidentite", "identite"] },
    { value: "AUTRE", label: t("dashboard.filters.other"), translationKey: "dashboard.filters.other", aliases: ["autre"] },
  ];

  const showNotification = (type, title, message) => {
    setNotification({ type, title, message });
    setTimeout(() => setNotification(null), 5000);
  };

  const handleNewDemandeSuccess = (demande) => {
    setShowNewDemandeForm(false);
    showNotification(
      "success",
      t("common.success"),
      t("dashboard.notifications.requestSubmitted")
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
    showNotification("info", t("dashboard.notifications.info"), t("dashboard.notifications.dataRefreshed"));
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
          t("common.error"),
          t("dashboard.notifications.cannotLoadDetails")
        );
      }
    } catch (err) {
      console.error("Erreur:", err);
      showNotification("error", t("common.error"), t("dashboard.notifications.connectionError"));
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

  // Charger les types de documents depuis la base de données
  useEffect(() => {
    const fetchDocumentTypes = async () => {
      try {
        setDocumentTypesLoading(true);
        const response = await fetch(
          "http://127.0.0.1:8080/api/demandes/document-types",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (response.ok) {
          const data = await response.json();

          // Normaliser les données pour gérer différents formats
          const normalizedTypes = data.map((type) => {
            // Gérer le format { value, label } de l'API demandes
            if (type.value && type.label) {
              return {
                value: type.value,
                label: type.label,
                // Ajouter des alias pour améliorer la correspondance
                aliases: [
                  type.label.toLowerCase(),
                  type.value.toLowerCase(),
                  // Ajouter des variations courantes
                  type.label.replace(/\s+/g, "").toLowerCase(),
                  type.label.replace(/[éèê]/g, "e").toLowerCase(),
                  type.label.replace(/[àâ]/g, "a").toLowerCase(),
                ],
              };
            }
            // Gérer le format { id, libelle } de l'API admin
            else if (type.id && type.libelle) {
              return {
                value: type.libelle,
                label: type.libelle,
                aliases: [
                  type.libelle.toLowerCase(),
                  type.id.toString().toLowerCase(),
                  type.libelle.replace(/\s+/g, "").toLowerCase(),
                  type.libelle.replace(/[éèê]/g, "e").toLowerCase(),
                  type.libelle.replace(/[àâ]/g, "a").toLowerCase(),
                ],
              };
            }
            // Gérer le format { libelle } simple
            else if (type.libelle) {
              return {
                value: type.libelle,
                label: type.libelle,
                aliases: [
                  type.libelle.toLowerCase(),
                  type.libelle.replace(/\s+/g, "").toLowerCase(),
                  type.libelle.replace(/[éèê]/g, "e").toLowerCase(),
                  type.libelle.replace(/[àâ]/g, "a").toLowerCase(),
                ],
              };
            }
            // Fallback pour les autres formats
            else {
              const fallbackValue = String(type);
              return {
                value: fallbackValue,
                label: fallbackValue,
                aliases: [fallbackValue.toLowerCase()],
              };
            }
          });

          setDocumentTypes(normalizedTypes);

          // Debug : afficher les types normalisés
          console.log("🔍 Types de documents normalisés:", normalizedTypes);
        } else {
          console.error(
            "Erreur lors du chargement des types de documents:",
            response.status
          );
          setDocumentTypes(getDefaultDocumentTypes());
        }
      } catch (err) {
        console.error("Erreur lors du chargement des types de documents:", err);
        setDocumentTypes(getDefaultDocumentTypes());
      } finally {
        setDocumentTypesLoading(false);
      }
    };

    if (token) {
      fetchDocumentTypes();
    }
  }, [token]);

  // Filtrage et recherche amélioré
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

      // Gestion spéciale pour le filtre documentType
      if (key === "documentType") {
        const filterValue = filters[key];
        const demandeType = demande.documentTypeDisplay || demande.documentType;

        // Si le filtre est vide, accepter toutes les demandes
        if (!filterValue) return true;

        // Correspondance exacte
        if (demandeType === filterValue) {
          return true;
        }

        // Correspondance insensible à la casse
        if (
          demandeType &&
          filterValue &&
          demandeType.toLowerCase() === filterValue.toLowerCase()
        ) {
          return true;
        }

        // Correspondance partielle (pour gérer les variations)
        if (
          demandeType &&
          filterValue &&
          (demandeType.toLowerCase().includes(filterValue.toLowerCase()) ||
            filterValue.toLowerCase().includes(demandeType.toLowerCase()))
        ) {
          return true;
        }

        // NOUVEAU : Correspondance par ID numérique
        // Chercher le type de document correspondant par son label
        const matchingDocumentType = documentTypes.find(
          (type) => type.label === filterValue || type.value === filterValue
        );

        if (matchingDocumentType) {
          // Vérifier si la demande correspond à ce type
          const demandeMatchesType =
            demande.documentTypeDisplay === matchingDocumentType.label ||
            demande.documentType === matchingDocumentType.value ||
            demande.documentType === matchingDocumentType.label ||
            demande.documentTypeDisplay === matchingDocumentType.value;

          if (demandeMatchesType) {
            return true;
          }
        }

        return false;
      }

      // Pour les autres filtres, correspondance exacte
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

  const handlePayDemande = async (demandeId) => {
    try {
      const res = await fetch(
        `${API_CONFIG.BASE_URL}${API_CONFIG.PAYMENT.CREATE_SESSION}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ demandeId }),
        }
      );
      if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        showNotification("error", t("common.error"), err?.error || t("dashboard.notifications.cannotCreatePayment"));
        return;
      }
      const data = await res.json();
      if (data.url) {
        window.location.href = data.url;
      } else {
        showNotification("error", t("common.error"), t("dashboard.notifications.cannotRedirectPayment"));
      }
    } catch (e) {
      showNotification("error", t("common.error"), t("dashboard.notifications.connectionError"));
    }
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
        showNotification("success", t("common.success"), t("dashboard.notifications.documentGenerated"));

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
            t("common.success"),
            t("dashboard.notifications.documentDownloaded")
          );
        } else {
          showNotification(
            "error",
            t("common.error"),
            t("dashboard.notifications.cannotDownload")
          );
        }
      } else {
        const errorData = await response.json();
        showNotification(
          "error",
          t("common.error"),
          errorData.error || t("dashboard.notifications.cannotGenerate")
        );
      }
    } catch (err) {
      console.error("Erreur:", err);
      showNotification("error", t("common.error"), t("dashboard.notifications.generateError"));
    }
  };

  const getStatusBadge = (status) => {
    const key = `dashboard.status.${status}`;
    const label = t(key);
    const className = {
      PENDING_PAYMENT: "badge bg-amber-100 text-amber-800",
      PENDING: "badge badge-pending",
      APPROVED: "badge badge-approved",
      REJECTED: "badge badge-rejected",
      COMPLETED: "badge bg-blue-100 text-blue-800",
    }[status] || "badge badge-pending";
    return <span className={className}>{label !== key ? label : status}</span>;
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "PENDING_PAYMENT":
        return "bg-amber-100 text-amber-800";
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

  // Options de filtres dynamiques (labels traduits)
  const filterOptions = {
    status: {
      label: t("dashboard.filters.status"),
      values: [
        { value: "PENDING_PAYMENT", label: t("dashboard.status.PENDING_PAYMENT") },
        { value: "PENDING", label: t("dashboard.status.PENDING") },
        { value: "APPROVED", label: t("dashboard.status.APPROVED") },
        { value: "REJECTED", label: t("dashboard.status.REJECTED") },
        { value: "COMPLETED", label: t("dashboard.status.COMPLETED") },
      ],
    },
    documentType: {
      label: t("dashboard.filters.documentType"),
      values: documentTypesLoading
        ? [{ value: "", label: t("dashboard.filters.loadingTypes") }]
        : documentTypes.length > 0
        ? [
            { value: "", label: t("dashboard.filters.allTypes") },
            ...documentTypes.map((type) => ({
              value: type.value,
              label: type.translationKey ? t(type.translationKey) : type.label,
            })),
          ]
        : [
            { value: "", label: t("dashboard.filters.allTypes") },
            { value: "PASSEPORT", label: t("dashboard.filters.passport") },
            { value: "ACTE_NAISSANCE", label: t("dashboard.filters.birthCertificate") },
            { value: "CERTIFICAT_MARIAGE", label: t("dashboard.filters.marriageCertificate") },
            { value: "CARTE_IDENTITE", label: t("dashboard.filters.idCard") },
            { value: "AUTRE", label: t("dashboard.filters.other") },
          ],
    },
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">{t("common.loading")}</p>
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
            {t("dashboard.title")}
          </h1>
          <p className="mt-2 text-gray-600">
            {t("dashboard.subtitle")}
          </p>
        </div>

        {/* Cartes de statistiques */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatsCard
            title={t("dashboard.stats.totalRequests")}
            value={stats.totalRequests}
            icon={DocumentTextIcon}
            change="+2"
            changeType="positive"
            description={t("dashboard.stats.thisMonth")}
          />
          <StatsCard
            title={t("dashboard.stats.pending")}
            value={stats.pendingRequests}
            icon={ClockIcon}
            change="+1"
            changeType="neutral"
            description={t("dashboard.stats.thisMonth")}
          />
          <StatsCard
            title={t("dashboard.stats.approved")}
            value={stats.approvedRequests}
            icon={CheckCircleIcon}
            change="+1"
            changeType="positive"
            description={t("dashboard.stats.thisMonth")}
          />
          <StatsCard
            title={t("dashboard.stats.rejected")}
            value={stats.rejectedRequests}
            icon={XCircleIcon}
            change="0"
            changeType="neutral"
            description={t("dashboard.stats.thisMonth")}
          />
        </div>

        {/* Section Demandes */}
        <div className="card">
          <div className="card-header">
            <div className="flex items-center justify-between">
              <h2 className="card-title">{t("dashboard.requests.myRequests")}</h2>
              <div className="flex space-x-2">
                <button
                  className="btn-secondary"
                  onClick={handleRefreshData}
                  title={t("dashboard.requests.refreshData")}
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
                  {t("dashboard.requests.newRequest")}
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
            placeholder={t("dashboard.requests.searchPlaceholder")}
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
                    {t("dashboard.requests.documentType")}
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
                    {t("dashboard.requests.createdAt")}
                    {sortConfig.key === "createdAt" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th className="table-header-cell">{t("dashboard.requests.actions")}</th>
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
                          {t("dashboard.requests.requestId", { id: demande.id })}
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
                      {new Date(demande.createdAt).toLocaleDateString(dateLocale, {
                        year: "numeric",
                        month: "long",
                        day: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </td>
                    <td className="table-cell">
                      <div className="flex space-x-2">
                        {demande.status === "PENDING_PAYMENT" && (
                          <button
                            onClick={() => handlePayDemande(demande.id)}
                            className="px-2 py-1 text-sm bg-amber-500 text-white rounded hover:bg-amber-600"
                            title={t("dashboard.requests.payRequest")}
                          >
                            {t("dashboard.requests.pay")}
                          </button>
                        )}
                        {demande.status === "APPROVED" && (
                          <button
                            onClick={() => handleGenerateDocument(demande.id)}
                            className="btn-success btn-sm"
                            title={t("dashboard.requests.downloadDoc")}
                          >
                            <DocumentArrowDownIcon className="h-4 w-4" />
                          </button>
                        )}
                        <button
                          className="btn-secondary btn-sm"
                          title={t("dashboard.requests.viewDetails")}
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
              <p className="mt-2 text-gray-500">{t("dashboard.requests.noRequests")}</p>
              <button
                className="btn-primary mt-4"
                onClick={() => setShowNewDemandeForm(true)}
              >
                <PlusIcon className="h-4 w-4 mr-2" />
                {t("dashboard.requests.createFirstRequest")}
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
                  {t("dashboard.modal.detailsTitle", { id: selectedDemande.id })}
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
                    {t("dashboard.modal.generalInfo")}
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="font-medium">{t("dashboard.modal.documentTypeLabel")}</span>{" "}
                      {selectedDemande.documentTypeDisplay}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.statusLabel")}</span>
                      <span
                        className={`ml-2 inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(
                          selectedDemande.status
                        )}`}
                      >
                        {selectedDemande.statusDisplay}
                      </span>
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.createdAtLabel")}</span>{" "}
                      {new Date(selectedDemande.createdAt).toLocaleDateString(
                        dateLocale,
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
                      <span className="font-medium">{t("dashboard.modal.updatedAtLabel")}</span>{" "}
                      {new Date(selectedDemande.updatedAt).toLocaleDateString(
                        dateLocale,
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
                    {t("dashboard.modal.personalInfo")}
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="font-medium">{t("dashboard.modal.lastNameLabel")}</span>{" "}
                      {selectedDemande.lastName}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.firstNameLabel")}</span>{" "}
                      {selectedDemande.firstName}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.birthDateLabel")}</span>{" "}
                      {selectedDemande.birthDate}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.birthPlaceLabel")}</span>{" "}
                      {selectedDemande.birthPlace}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.birthCountryLabel")}</span>{" "}
                      {selectedDemande.birthCountry}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.civilityLabel")}</span>{" "}
                      {selectedDemande.civilite}
                    </div>
                  </div>
                </div>

                {/* Adresse */}
                <div className="bg-gray-50 p-4 rounded-lg">
                  <h4 className="font-medium text-gray-900 mb-2">{t("dashboard.modal.address")}</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="font-medium">{t("dashboard.modal.streetLabel")}</span>{" "}
                      {selectedDemande.streetNumber}{" "}
                      {selectedDemande.streetName}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.boxLabel")}</span>{" "}
                      {selectedDemande.boxNumber || "N/A"}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.postalCodeLabel")}</span>{" "}
                      {selectedDemande.postalCode}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.cityLabel")}</span>{" "}
                      {selectedDemande.city}
                    </div>
                    <div>
                      <span className="font-medium">{t("dashboard.modal.countryLabel")}</span>{" "}
                      {selectedDemande.country}
                    </div>
                  </div>
                </div>

                {/* Filiation */}
                {(selectedDemande.fatherFirstName ||
                  selectedDemande.motherFirstName) && (
                  <div className="bg-gray-50 p-4 rounded-lg">
                    <h4 className="font-medium text-gray-900 mb-2">
                      {t("dashboard.modal.filiation")}
                    </h4>
                    <div className="space-y-4">
                      {selectedDemande.fatherFirstName && (
                        <div>
                          <h5 className="font-medium text-sm text-gray-700 mb-1">
                            {t("dashboard.modal.father")}
                          </h5>
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
                            <div>
                              <span className="font-medium">{t("dashboard.modal.lastNameLabel")}</span>{" "}
                              {selectedDemande.fatherLastName}
                            </div>
                            <div>
                              <span className="font-medium">{t("dashboard.modal.firstNameLabel")}</span>{" "}
                              {selectedDemande.fatherFirstName}
                            </div>
                            <div>
                              <span className="font-medium">{t("dashboard.modal.birthDateLabel")}</span>{" "}
                              {selectedDemande.fatherBirthDate}
                            </div>
                            <div>
                              <span className="font-medium">{t("dashboard.modal.birthPlaceLabel")}</span>{" "}
                              {selectedDemande.fatherBirthPlace}
                            </div>
                          </div>
                        </div>
                      )}
                      {selectedDemande.motherFirstName && (
                        <div>
                          <h5 className="font-medium text-sm text-gray-700 mb-1">
                            {t("dashboard.modal.mother")}
                          </h5>
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm">
                            <div>
                              <span className="font-medium">{t("dashboard.modal.lastNameLabel")}</span>{" "}
                              {selectedDemande.motherLastName}
                            </div>
                            <div>
                              <span className="font-medium">{t("dashboard.modal.firstNameLabel")}</span>{" "}
                              {selectedDemande.motherFirstName}
                            </div>
                            <div>
                              <span className="font-medium">{t("dashboard.modal.birthDateLabel")}</span>{" "}
                              {selectedDemande.motherBirthDate}
                            </div>
                            <div>
                              <span className="font-medium">{t("dashboard.modal.birthPlaceLabel")}</span>{" "}
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
                        {t("dashboard.modal.attachedDocs")}
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
                  {t("common.close")}
                </button>
                {selectedDemande.status === "APPROVED" && (
                  <button
                    className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 font-medium"
                    onClick={() => handleGenerateDocument(selectedDemande.id)}
                  >
                    {t("dashboard.modal.generateDocument")}
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
