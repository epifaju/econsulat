import React, { useState, useEffect } from "react";
import {
  EyeIcon,
  PencilIcon,
  TrashIcon,
  DocumentArrowDownIcon,
  DocumentTextIcon,
  MagnifyingGlassIcon,
  FunnelIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  CheckCircleIcon,
  XCircleIcon,
  ClockIcon,
  EnvelopeIcon,
} from "@heroicons/react/24/outline";
import {
  downloadBlob,
  isValidPdf,
  ensureFileExtension,
  formatFileSize,
  handlePdfDownload,
} from "../utils/fileDownload";
import AdminDemandeEditModal from "./AdminDemandeEditModal";
import ConfirmationModal from "./ConfirmationModal";

const AdminDemandesList = ({ token, onNotification }) => {
  const [demandes, setDemandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [sortBy, setSortBy] = useState("createdAt");
  const [sortDir, setSortDir] = useState("desc");
  const [generatingPdf, setGeneratingPdf] = useState(false);
  const [selectedDemande, setSelectedDemande] = useState(null);
  const [showDetails, setShowDetails] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingDemande, setEditingDemande] = useState(null);
  const [deletingDemande, setDeletingDemande] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [demandeToDelete, setDemandeToDelete] = useState(null);
  const [documentTypes, setDocumentTypes] = useState([]);
  const [selectedDocumentType, setSelectedDocumentType] = useState({});
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [lastNotifications, setLastNotifications] = useState({});
  const [loadingNotifications, setLoadingNotifications] = useState(false);

  useEffect(() => {
    fetchDemandes();
  }, [currentPage, searchTerm, statusFilter, sortBy, sortDir]);

  // Charger les types de documents après les demandes pour pouvoir faire le mapping
  useEffect(() => {
    if (demandes.length > 0) {
      fetchDocumentTypes();
      // Récupérer les notifications pour toutes les demandes
      fetchNotificationsForAllDemandes();
    }
  }, [demandes]);

  const fetchDemandes = async () => {
    try {
      setLoading(true);
      let url = `http://127.0.0.1:8080/api/admin/demandes?page=${currentPage}&size=5&sortBy=${sortBy}&sortDir=${sortDir}`;

      if (searchTerm) {
        url = `http://127.0.0.1:8080/api/admin/demandes/search?q=${encodeURIComponent(
          searchTerm
        )}&page=${currentPage}&size=5`;
      } else if (statusFilter) {
        url = `http://127.0.0.1:8080/api/admin/demandes/status/${statusFilter}?page=${currentPage}&size=5`;
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDemandes(data.content);
        setTotalPages(data.totalPages);
      } else {
        onNotification("error", "Erreur", "Impossible de charger les demandes");
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème de connexion au serveur");
    } finally {
      setLoading(false);
    }
  };

  const fetchDocumentTypes = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/admin/document-types",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        setDocumentTypes(data);

        // Initialiser le type sélectionné pour chaque demande
        // en mappant le type de document de la demande avec le type correspondant en base
        if (demandes.length > 0 && data.length > 0) {
          const initialSelection = {};
          demandes.forEach((demande) => {
            // Chercher le type de document correspondant en base
            const matchingType = data.find(
              (type) =>
                type.libelle.toLowerCase() ===
                  demande.documentTypeDisplay?.toLowerCase() ||
                type.libelle
                  .toLowerCase()
                  .includes(demande.documentTypeDisplay?.toLowerCase()) ||
                demande.documentTypeDisplay
                  ?.toLowerCase()
                  .includes(type.libelle.toLowerCase())
            );

            if (matchingType) {
              initialSelection[demande.id] = matchingType.id;
            } else {
              // Fallback : utiliser le premier type disponible
              initialSelection[demande.id] = data[0]?.id || null;
            }
          });

          setSelectedDocumentType(initialSelection);
        }
      }
    } catch (err) {
      console.error("Erreur lors du chargement des types de documents:", err);
    }
  };

  const updateDemandeStatus = async (demandeId, newStatus) => {
    try {
      setUpdatingStatus(true);
      const response = await fetch(
        `http://127.0.0.1:8080/api/demandes/${demandeId}/status`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ status: newStatus }),
        }
      );

      if (response.ok) {
        const updatedDemande = await response.json();

        // Mettre à jour la liste des demandes
        setDemandes((prevDemandes) =>
          prevDemandes.map((d) => (d.id === demandeId ? updatedDemande : d))
        );

        // Récupérer la dernière notification pour cette demande
        await fetchLastNotification(demandeId);

        onNotification(
          "success",
          "Succès",
          `Statut de la demande mis à jour vers ${newStatus}`
        );
      } else {
        const errorData = await response.json();
        onNotification(
          "error",
          "Erreur",
          errorData.message || "Erreur lors de la mise à jour du statut"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème de connexion au serveur");
    } finally {
      setUpdatingStatus(false);
    }
  };

  const fetchLastNotification = async (demandeId) => {
    try {
      const response = await fetch(
        `http://127.0.0.1:8080/api/notifications/demande/${demandeId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const notifications = await response.json();
        if (notifications.length > 0) {
          setLastNotifications((prev) => ({
            ...prev,
            [demandeId]: notifications[0],
          }));
        } else {
          // Marquer explicitement qu'aucune notification n'existe pour cette demande
          setLastNotifications((prev) => ({
            ...prev,
            [demandeId]: null,
          }));
        }
      } else {
        console.warn(
          `Erreur ${response.status} lors de la récupération des notifications pour la demande ${demandeId}`
        );
        // Marquer qu'il n'y a pas de notification en cas d'erreur
        setLastNotifications((prev) => ({
          ...prev,
          [demandeId]: null,
        }));
      }
    } catch (err) {
      console.error(
        "Erreur lors de la récupération de la dernière notification:",
        err
      );
      // Marquer qu'il n'y a pas de notification en cas d'erreur
      setLastNotifications((prev) => ({
        ...prev,
        [demandeId]: null,
      }));
    }
  };

  const fetchNotificationsForAllDemandes = async () => {
    try {
      setLoadingNotifications(true);
      const notificationPromises = demandes.map((demande) =>
        fetchLastNotification(demande.id)
      );

      // Attendre que toutes les notifications soient récupérées
      await Promise.all(notificationPromises);
    } catch (err) {
      console.error("Erreur lors de la récupération des notifications:", err);
    } finally {
      setLoadingNotifications(false);
    }
  };

  const handleDocumentTypeChange = (demandeId, documentTypeId) => {
    setSelectedDocumentType((prev) => ({
      ...prev,
      [demandeId]: parseInt(documentTypeId),
    }));
  };

  const handleStatusChange = async (demandeId, newStatus) => {
    try {
      console.log(
        `Mise à jour du statut: demande ${demandeId} -> ${newStatus}`
      );

      // Utiliser la nouvelle API qui déclenche automatiquement l'envoi de notifications
      const response = await fetch(
        `http://127.0.0.1:8080/api/demandes/${demandeId}/status`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ status: newStatus }),
        }
      );

      console.log(`Réponse status: ${response.status} ${response.statusText}`);

      if (response.ok) {
        const updatedDemande = await response.json();

        // Mettre à jour la liste des demandes
        setDemandes((prevDemandes) =>
          prevDemandes.map((d) => (d.id === demandeId ? updatedDemande : d))
        );

        // Récupérer la dernière notification pour cette demande
        await fetchLastNotification(demandeId);

        onNotification(
          "success",
          "Succès",
          `Statut mis à jour vers ${newStatus} et notification envoyée`
        );
      } else {
        const errorData = await response.json().catch(() => ({}));
        const errorMessage =
          errorData?.message || "Impossible de mettre à jour le statut";
        console.error("Erreur mise à jour statut:", errorData);
        onNotification("error", "Erreur", errorMessage);
      }
    } catch (err) {
      console.error("Erreur lors de la mise à jour du statut:", err);
      onNotification(
        "error",
        "Erreur",
        "Problème de connexion lors de la mise à jour"
      );
    }
  };

  const handleGenerateDocument = async (demandeId) => {
    const documentTypeId = selectedDocumentType[demandeId];
    if (!documentTypeId) {
      onNotification(
        "error",
        "Erreur",
        "Veuillez sélectionner un type de document"
      );
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/admin/documents/generate?demandeId=${demandeId}&documentTypeId=${documentTypeId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const generatedDocument = await response.json();
        onNotification("success", "Succès", "Document généré avec succès");

        // Télécharger le document
        const downloadResponse = await fetch(
          `http://localhost:8080/api/admin/documents/download/${generatedDocument.id}`,
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
          a.download = generatedDocument.fileName;
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
        } else {
          onNotification(
            "error",
            "Erreur",
            "Impossible de télécharger le document généré"
          );
        }
      } else {
        const errorData = await response.json().catch(() => null);
        const errorMessage =
          errorData?.message || "Impossible de générer le document";
        onNotification("error", "Erreur", errorMessage);
      }
    } catch (err) {
      console.error("Erreur lors de la génération:", err);
      onNotification(
        "error",
        "Erreur",
        "Problème de connexion lors de la génération"
      );
    }
  };

  const handleGeneratePdfDocument = async (demandeId) => {
    const documentTypeId = selectedDocumentType[demandeId];
    if (!documentTypeId) {
      onNotification(
        "error",
        "Erreur",
        "Veuillez sélectionner un type de document"
      );
      return;
    }

    setGeneratingPdf(true);
    try {
      console.log(
        `Génération PDF pour demande ${demandeId}, type ${documentTypeId}`
      );

      const response = await fetch(
        `http://127.0.0.1:8080/api/admin/pdf-documents/generate?demandeId=${demandeId}&documentTypeId=${documentTypeId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log(
        `Réponse génération PDF: ${response.status} ${response.statusText}`
      );

      if (response.ok) {
        const generatedDocument = await response.json();
        console.log("Document généré:", generatedDocument);
        onNotification("success", "Succès", "Document PDF généré avec succès");

        // Télécharger le document PDF
        console.log(`Téléchargement du document ${generatedDocument.id}`);
        const downloadResponse = await fetch(
          `http://127.0.0.1:8080/api/admin/pdf-documents/download/${generatedDocument.id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        console.log(
          `Réponse téléchargement: ${downloadResponse.status} ${downloadResponse.statusText}`
        );

        if (downloadResponse.ok) {
          // Vérifier le type de contenu
          const contentType = downloadResponse.headers.get("content-type");
          const contentLength = downloadResponse.headers.get("content-length");

          console.log("Headers de réponse:", {
            contentType,
            contentLength,
            status: downloadResponse.status,
            statusText: downloadResponse.statusText,
          });

          // Vérifier que c'est bien un PDF
          if (contentType && !contentType.includes("application/pdf")) {
            console.warn("Type de contenu inattendu:", contentType);
            onNotification(
              "warning",
              "Attention",
              `Type de fichier inattendu: ${contentType}. Tentative de téléchargement...`
            );
          }

          const blob = await downloadResponse.blob();
          console.log("Blob reçu:", {
            size: blob.size,
            type: blob.type,
            lastModified: blob.lastModified,
          });

          if (blob.size > 0) {
            // Vérifier que le blob est bien un PDF
            if (blob.type && !blob.type.includes("application/pdf")) {
              console.warn("Type de blob inattendu:", blob.type);
            }

            // Créer l'URL du blob
            const url = window.URL.createObjectURL(blob);

            // Créer l'élément de téléchargement
            const a = document.createElement("a");
            a.href = url;

            // S'assurer que le fichier a l'extension .pdf
            let fileName = generatedDocument.fileName || "document.pdf";
            if (!fileName.toLowerCase().endsWith(".pdf")) {
              fileName = fileName.replace(/\.[^/.]+$/, "") + ".pdf";
            }

            a.download = fileName;
            a.style.display = "none";

            // Ajouter au DOM et déclencher le téléchargement
            document.body.appendChild(a);

            try {
              a.click();
              console.log("Téléchargement déclenché pour:", fileName);
              onNotification(
                "success",
                "Succès",
                `Document PDF téléchargé: ${fileName}`
              );
            } catch (downloadError) {
              console.error(
                "Erreur lors du déclenchement du téléchargement:",
                downloadError
              );
              onNotification(
                "error",
                "Erreur",
                "Impossible de déclencher le téléchargement automatique. Vérifiez les paramètres de votre navigateur."
              );
            } finally {
              // Nettoyer
              document.body.removeChild(a);
              window.URL.revokeObjectURL(url);
            }
          } else {
            console.error("Blob vide reçu");
            onNotification(
              "error",
              "Erreur",
              "Le document PDF généré est vide (0 bytes)"
            );
          }
        } else {
          // Gestion des erreurs HTTP
          let errorMessage = `Erreur ${downloadResponse.status}: ${downloadResponse.statusText}`;

          try {
            const errorText = await downloadResponse.text();
            console.error(
              "Erreur téléchargement - Réponse complète:",
              errorText
            );

            // Essayer de parser comme JSON
            try {
              const errorJson = JSON.parse(errorText);
              errorMessage =
                errorJson.message || errorJson.error || errorMessage;
            } catch {
              // Si ce n'est pas du JSON, utiliser le texte brut
              if (errorText.trim()) {
                errorMessage = errorText;
              }
            }
          } catch (textError) {
            console.error("Impossible de lire le texte d'erreur:", textError);
          }

          onNotification("error", "Erreur de Téléchargement", errorMessage);
        }
      } else {
        // Gestion des erreurs de génération
        let errorMessage = `Erreur ${response.status}: ${response.statusText}`;

        try {
          const errorData = await response.json();
          console.error("Erreur génération:", errorData);
          errorMessage = errorData?.message || errorData?.error || errorMessage;
        } catch (jsonError) {
          console.error("Impossible de parser l'erreur JSON:", jsonError);
        }

        onNotification("error", "Erreur de Génération", errorMessage);
      }
    } catch (err) {
      console.error("Erreur lors de la génération PDF:", err);

      let errorMessage = err.message;
      if (err.name === "TypeError" && err.message.includes("fetch")) {
        errorMessage =
          "Problème de connexion au serveur. Vérifiez que le backend est démarré.";
      } else if (err.name === "AbortError") {
        errorMessage = "La requête a été annulée.";
      }

      onNotification("error", "Erreur de Connexion", errorMessage);
    } finally {
      setGeneratingPdf(false);
    }
  };

  const handleEditDemande = (demande) => {
    setEditingDemande(demande);
    setShowEditModal(true);
  };

  const handleSaveDemande = (updatedDemande) => {
    // Mettre à jour la liste des demandes avec la demande modifiée
    setDemandes((prevDemandes) =>
      prevDemandes.map((demande) =>
        demande.id === updatedDemande.id ? updatedDemande : demande
      )
    );
  };

  const handleDeleteDemande = (demande) => {
    setDemandeToDelete(demande);
    setShowDeleteModal(true);
  };

  const confirmDeleteDemande = async () => {
    if (!demandeToDelete) return;

    try {
      setDeletingDemande(demandeToDelete.id);
      console.log(`Suppression de la demande ${demandeToDelete.id}`);

      const response = await fetch(
        `http://localhost:8080/api/admin/demandes/${demandeToDelete.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log(
        `Réponse suppression: ${response.status} ${response.statusText}`
      );

      if (response.ok) {
        onNotification("success", "Succès", "Demande supprimée avec succès");
        fetchDemandes();
      } else {
        const errorData = await response.json().catch(() => ({}));
        const errorMessage =
          errorData?.message || "Impossible de supprimer la demande";
        console.error("Erreur suppression:", errorData);
        onNotification("error", "Erreur", errorMessage);
      }
    } catch (err) {
      console.error("Erreur lors de la suppression:", err);
      onNotification(
        "error",
        "Erreur",
        "Problème de connexion lors de la suppression"
      );
    } finally {
      setDeletingDemande(null);
      setShowDeleteModal(false);
      setDemandeToDelete(null);
    }
  };

  const cancelDeleteDemande = () => {
    setShowDeleteModal(false);
    setDemandeToDelete(null);
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      PENDING: { color: "bg-yellow-100 text-yellow-800", label: "En attente" },
      APPROVED: { color: "bg-green-100 text-green-800", label: "Approuvé" },
      REJECTED: { color: "bg-red-100 text-red-800", label: "Rejeté" },
      COMPLETED: { color: "bg-blue-100 text-blue-800", label: "Terminé" },
    };

    const config = statusConfig[status] || statusConfig.PENDING;
    return (
      <span
        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.color}`}
      >
        {config.label}
      </span>
    );
  };

  const handleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortBy(field);
      setSortDir("asc");
    }
  };

  const SortIcon = ({ field }) => {
    if (sortBy !== field) return null;
    return sortDir === "asc" ? (
      <ArrowUpIcon className="h-4 w-4 ml-1" />
    ) : (
      <ArrowDownIcon className="h-4 w-4 ml-1" />
    );
  };

  const handleResendNotification = async (notificationId, demandeId) => {
    try {
      const response = await fetch(
        `http://127.0.0.1:8080/api/notifications/${notificationId}/resend`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        onNotification(
          "success",
          "Succès",
          "Notification renvoyée avec succès"
        );
        // Recharger la notification pour cette demande
        await fetchLastNotification(demandeId);
      } else {
        const errorData = await response.json();
        onNotification(
          "error",
          "Erreur",
          errorData.error || "Erreur lors du renvoi de la notification"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème de connexion au serveur");
    }
  };

  const getNotificationIndicator = (demandeId) => {
    const notification = lastNotifications[demandeId];

    // Si les notifications sont en cours de chargement
    if (loadingNotifications) {
      return (
        <div className="flex items-center space-x-2">
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
          <span className="text-xs text-gray-500">Chargement...</span>
        </div>
      );
    }

    // Si aucune notification n'existe pour cette demande
    if (notification === null) {
      return (
        <div className="flex items-center space-x-2">
          <EnvelopeIcon className="h-4 w-4 text-gray-400" />
          <span className="text-xs text-gray-500">Aucune notification</span>
        </div>
      );
    }

    // Si la notification n'est pas encore chargée
    if (!notification) {
      return (
        <div className="flex items-center space-x-2">
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-gray-400"></div>
          <span className="text-xs text-gray-500">Chargement...</span>
        </div>
      );
    }

    const getStatusIcon = (statut) => {
      switch (statut) {
        case "ENVOYE":
          return <CheckCircleIcon className="h-4 w-4 text-green-500" />;
        case "ECHEC":
          return <XCircleIcon className="h-4 w-4 text-red-500" />;
        case "EN_COURS":
          return <ClockIcon className="h-4 w-4 text-yellow-500" />;
        default:
          return <EnvelopeIcon className="h-4 w-4 text-gray-400" />;
      }
    };

    const getStatusColor = (statut) => {
      switch (statut) {
        case "ENVOYE":
          return "text-green-700 bg-green-100";
        case "ECHEC":
          return "text-red-700 bg-red-100";
        case "EN_COURS":
          return "text-yellow-700 bg-yellow-100";
        default:
          return "text-gray-700 bg-gray-100";
      }
    };

    return (
      <div className="space-y-2">
        <div className="flex items-center space-x-2">
          {getStatusIcon(notification.statut)}
          <span className="text-xs font-medium">
            {notification.statut === "ENVOYE"
              ? "Notification envoyée"
              : notification.statut === "ECHEC"
              ? "Échec d'envoi"
              : notification.statut === "EN_COURS"
              ? "Envoi en cours"
              : "Notification"}
          </span>
        </div>
        <div className="text-xs text-gray-600">
          {new Date(notification.dateEnvoi).toLocaleDateString("fr-FR")} à{" "}
          {new Date(notification.dateEnvoi).toLocaleTimeString("fr-FR", {
            hour: "2-digit",
            minute: "2-digit",
          })}
        </div>
        <div
          className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(
            notification.statut
          )}`}
        >
          {notification.statut === "ENVOYE"
            ? "✓ Envoyé"
            : notification.statut === "ECHEC"
            ? "✗ Échec"
            : notification.statut === "EN_COURS"
            ? "⏳ En cours"
            : notification.statut}
        </div>

        {/* Bouton de renvoi en cas d'échec */}
        {notification.statut === "ECHEC" && (
          <button
            onClick={() => handleResendNotification(notification.id, demandeId)}
            className="mt-1 inline-flex items-center px-2 py-1 text-xs font-medium text-red-700 bg-red-100 rounded-full hover:bg-red-200 transition-colors"
            title="Renvoyer la notification"
          >
            <EnvelopeIcon className="h-3 w-3 mr-1" />
            Renvoyer
          </button>
        )}
      </div>
    );
  };

  if (loading) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-900">
          Gestion des Demandes
        </h2>
        <p className="text-gray-600">
          Consultez et gérez toutes les demandes de documents
        </p>
      </div>

      {/* Filtres et recherche */}
      <div className="mb-6 flex flex-col sm:flex-row gap-4">
        <div className="flex-1">
          <div className="relative">
            <MagnifyingGlassIcon className="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Rechercher par nom..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>
        <div className="flex gap-2">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">Tous les statuts</option>
            <option value="PENDING">En attente</option>
            <option value="APPROVED">Approuvé</option>
            <option value="REJECTED">Rejeté</option>
            <option value="COMPLETED">Terminé</option>
          </select>
        </div>
      </div>

      {/* Tableau des demandes */}
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort("firstName")}
              >
                <div className="flex items-center">
                  Demandeur
                  <SortIcon field="firstName" />
                </div>
              </th>
              <th
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort("documentType")}
              >
                <div className="flex items-center">
                  Type de document
                  <SortIcon field="documentType" />
                </div>
              </th>
              <th
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort("status")}
              >
                <div className="flex items-center">
                  Statut
                  <SortIcon field="status" />
                </div>
              </th>
              <th
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort("createdAt")}
              >
                <div className="flex items-center">
                  Date de création
                  <SortIcon field="createdAt" />
                </div>
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Type à générer
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <div className="flex items-center space-x-2">
                  <EnvelopeIcon className="h-4 w-4" />
                  <span>Notification envoyée</span>
                </div>
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {demandes.map((demande) => (
              <tr key={demande.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <div>
                    <div className="text-sm font-medium text-gray-900">
                      {demande.firstName} {demande.lastName}
                    </div>
                    <div className="text-sm text-gray-500">
                      {demande.user?.email}
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="text-sm text-gray-900">
                    {demande.documentTypeDisplay}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {getStatusBadge(demande.status)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {new Date(demande.createdAt).toLocaleDateString("fr-FR")}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div className="space-y-1">
                    <select
                      value={selectedDocumentType[demande.id] || ""}
                      onChange={(e) =>
                        handleDocumentTypeChange(demande.id, e.target.value)
                      }
                      className="text-xs border border-gray-300 rounded px-2 py-1 min-w-[120px]"
                      title="Sélectionner le type de document à générer"
                    >
                      <option value="">Sélectionner...</option>
                      {documentTypes.map((type) => (
                        <option key={type.id} value={type.id}>
                          {type.libelle}
                        </option>
                      ))}
                    </select>
                    {selectedDocumentType[demande.id] && (
                      <div className="text-xs text-gray-500">
                        <span className="text-green-600">✓</span>{" "}
                        Pré-sélectionné
                      </div>
                    )}
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {getNotificationIndicator(demande.id)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div className="flex space-x-2">
                    <button
                      onClick={() => {
                        setSelectedDemande(demande);
                        setShowDetails(true);
                      }}
                      className="text-blue-600 hover:text-blue-900"
                      title="Voir détails"
                    >
                      <EyeIcon className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleEditDemande(demande)}
                      className="text-orange-600 hover:text-orange-900"
                      title="Modifier la demande"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>

                    <button
                      onClick={() => handleGenerateDocument(demande.id)}
                      className="text-green-600 hover:text-green-900"
                      title="Générer document Word"
                      disabled={!selectedDocumentType[demande.id]}
                    >
                      <DocumentArrowDownIcon className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleGeneratePdfDocument(demande.id)}
                      disabled={
                        generatingPdf || !selectedDocumentType[demande.id]
                      }
                      className={`${
                        generatingPdf || !selectedDocumentType[demande.id]
                          ? "text-gray-400 cursor-not-allowed"
                          : "text-blue-600 hover:text-blue-900"
                      }`}
                      title={
                        generatingPdf
                          ? "Génération en cours..."
                          : !selectedDocumentType[demande.id]
                          ? "Sélectionnez d'abord un type de document"
                          : "Générer document PDF"
                      }
                    >
                      {generatingPdf ? (
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                      ) : (
                        <DocumentTextIcon className="h-4 w-4" />
                      )}
                    </button>
                    <select
                      value={demande.status}
                      onChange={(e) =>
                        handleStatusChange(demande.id, e.target.value)
                      }
                      className="text-xs border border-gray-300 rounded px-2 py-1"
                    >
                      <option value="PENDING">En attente</option>
                      <option value="APPROVED">Approuvé</option>
                      <option value="REJECTED">Rejeté</option>
                      <option value="COMPLETED">Terminé</option>
                    </select>
                    <button
                      onClick={() => handleDeleteDemande(demande)}
                      disabled={deletingDemande === demande.id}
                      className={`${
                        deletingDemande === demande.id
                          ? "text-gray-400 cursor-not-allowed"
                          : "text-red-600 hover:text-red-900"
                      }`}
                      title={
                        deletingDemande === demande.id
                          ? "Suppression en cours..."
                          : "Supprimer la demande"
                      }
                    >
                      {deletingDemande === demande.id ? (
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-red-600"></div>
                      ) : (
                        <TrashIcon className="h-4 w-4" />
                      )}
                    </button>

                    {/* Indicateur de notification dans les actions */}
                    {lastNotifications[demande.id] &&
                      lastNotifications[demande.id].statut === "ENVOYE" && (
                        <div className="ml-2 inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          <CheckCircleIcon className="h-3 w-3 mr-1" />
                          Notifié
                        </div>
                      )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-6 flex items-center justify-between">
          <div className="text-sm text-gray-700">
            Page {currentPage + 1} sur {totalPages}
          </div>
          <div className="flex space-x-2">
            <button
              onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
              disabled={currentPage === 0}
              className="px-3 py-2 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
            >
              Précédent
            </button>
            <button
              onClick={() =>
                setCurrentPage(Math.min(totalPages - 1, currentPage + 1))
              }
              disabled={currentPage === totalPages - 1}
              className="px-3 py-2 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
            >
              Suivant
            </button>
          </div>
        </div>
      )}

      {/* Modal de détails */}
      {showDetails && selectedDemande && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Détails de la demande</h3>
              <button
                onClick={() => setShowDetails(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                ✕
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <h4 className="font-medium text-gray-900">
                  Informations du demandeur
                </h4>
                <p className="text-sm text-gray-600">
                  {selectedDemande.firstName} {selectedDemande.lastName}
                </p>
                <p className="text-sm text-gray-600">
                  {selectedDemande.user?.email}
                </p>
              </div>

              <div>
                <h4 className="font-medium text-gray-900">Type de document</h4>
                <p className="text-sm text-gray-600">
                  {selectedDemande.documentTypeDisplay}
                </p>
              </div>

              <div>
                <h4 className="font-medium text-gray-900">Statut</h4>
                <div className="mt-1">
                  {getStatusBadge(selectedDemande.status)}
                </div>
              </div>

              <div>
                <h4 className="font-medium text-gray-900">Documents joints</h4>
                {selectedDemande.documentFiles &&
                selectedDemande.documentFiles.length > 0 ? (
                  <ul className="text-sm text-gray-600">
                    {selectedDemande.documentFiles.map((file, index) => (
                      <li key={index}>{file}</li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-gray-500">Aucun document joint</p>
                )}
              </div>

              <div>
                <h4 className="font-medium text-gray-900">Date de création</h4>
                <p className="text-sm text-gray-600">
                  {new Date(selectedDemande.createdAt).toLocaleString("fr-FR")}
                </p>
              </div>
            </div>

            <div className="mt-6 flex justify-end space-x-3">
              <button
                onClick={() => setShowDetails(false)}
                className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50"
              >
                Fermer
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal d'édition */}
      <AdminDemandeEditModal
        demande={editingDemande}
        isOpen={showEditModal}
        onClose={() => {
          setShowEditModal(false);
          setEditingDemande(null);
        }}
        onSave={handleSaveDemande}
        token={token}
        onNotification={onNotification}
      />

      {/* Modal de confirmation de suppression */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        onClose={cancelDeleteDemande}
        onConfirm={confirmDeleteDemande}
        title="Supprimer la demande"
        message={
          demandeToDelete
            ? `Êtes-vous sûr de vouloir supprimer définitivement la demande de ${demandeToDelete.firstName} ${demandeToDelete.lastName} ? Cette action est irréversible et supprimera également tous les documents générés associés.`
            : "Êtes-vous sûr de vouloir supprimer cette demande ?"
        }
        confirmText="Supprimer"
        cancelText="Annuler"
        type="danger"
        loading={deletingDemande !== null}
      />
    </div>
  );
};

export default AdminDemandesList;
