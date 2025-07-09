import React, { useState, useEffect } from "react";
import {
  PlusIcon,
  PencilIcon,
  TrashIcon,
  MagnifyingGlassIcon,
  CheckCircleIcon,
  XCircleIcon,
} from "@heroicons/react/24/outline";

const AdminDocumentTypes = ({ token, onNotification }) => {
  const [documentTypes, setDocumentTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedDocumentType, setSelectedDocumentType] = useState(null);
  const [formData, setFormData] = useState({
    libelle: "",
    description: "",
    templatePath: "",
    isActive: true,
  });

  useEffect(() => {
    fetchDocumentTypes();
  }, []);

  const fetchDocumentTypes = async () => {
    try {
      setLoading(true);
      let url = "http://localhost:8080/api/admin/document-types";

      if (searchTerm) {
        url += `?q=${encodeURIComponent(searchTerm)}`;
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDocumentTypes(data);
      } else {
        onNotification(
          "error",
          "Erreur",
          "Impossible de charger les types de documents"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème de connexion au serveur");
    } finally {
      setLoading(false);
    }
  };

  const handleCreateDocumentType = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        "http://localhost:8080/api/admin/document-types",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(formData),
        }
      );

      if (response.ok) {
        onNotification(
          "success",
          "Succès",
          "Type de document créé avec succès"
        );
        setShowCreateModal(false);
        setFormData({
          libelle: "",
          description: "",
          templatePath: "",
          isActive: true,
        });
        fetchDocumentTypes();
      } else {
        onNotification(
          "error",
          "Erreur",
          "Impossible de créer le type de document"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème lors de la création");
    }
  };

  const handleUpdateDocumentType = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(
        `http://localhost:8080/api/admin/document-types/${selectedDocumentType.id}`,
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
        onNotification(
          "success",
          "Succès",
          "Type de document mis à jour avec succès"
        );
        setShowEditModal(false);
        setSelectedDocumentType(null);
        fetchDocumentTypes();
      } else {
        onNotification(
          "error",
          "Erreur",
          "Impossible de mettre à jour le type de document"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème lors de la mise à jour");
    }
  };

  const handleDeleteDocumentType = async (documentTypeId) => {
    if (
      !window.confirm(
        "Êtes-vous sûr de vouloir supprimer ce type de document ?"
      )
    ) {
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/admin/document-types/${documentTypeId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        onNotification(
          "success",
          "Succès",
          "Type de document supprimé avec succès"
        );
        fetchDocumentTypes();
      } else {
        onNotification(
          "error",
          "Erreur",
          "Impossible de supprimer le type de document"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème lors de la suppression");
    }
  };

  const handleActivateDocumentType = async (documentTypeId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/admin/document-types/${documentTypeId}/activate`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        onNotification(
          "success",
          "Succès",
          "Type de document activé avec succès"
        );
        fetchDocumentTypes();
      } else {
        onNotification(
          "error",
          "Erreur",
          "Impossible d'activer le type de document"
        );
      }
    } catch (err) {
      onNotification("error", "Erreur", "Problème lors de l'activation");
    }
  };

  const openEditModal = (documentType) => {
    setSelectedDocumentType(documentType);
    setFormData({
      libelle: documentType.libelle,
      description: documentType.description || "",
      templatePath: documentType.templatePath || "",
      isActive: documentType.isActive,
    });
    setShowEditModal(true);
  };

  const filteredDocumentTypes = documentTypes.filter(
    (docType) =>
      docType.libelle.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (docType.description &&
        docType.description.toLowerCase().includes(searchTerm.toLowerCase()))
  );

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
      <div className="mb-6 flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">
            Types de Documents
          </h2>
          <p className="text-gray-600">
            Gérez les types de documents disponibles
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 flex items-center space-x-2"
        >
          <PlusIcon className="h-4 w-4" />
          <span>Nouveau type</span>
        </button>
      </div>

      {/* Recherche */}
      <div className="mb-6">
        <div className="relative">
          <MagnifyingGlassIcon className="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Rechercher par nom ou description..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
      </div>

      {/* Liste des types de documents */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredDocumentTypes.map((docType) => (
          <div
            key={docType.id}
            className={`bg-white rounded-lg border p-6 hover:shadow-md transition-shadow ${
              !docType.isActive ? "opacity-60" : ""
            }`}
          >
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-semibold text-gray-900">
                  {docType.libelle}
                </h3>
                <div className="flex items-center mt-1">
                  {docType.isActive ? (
                    <CheckCircleIcon className="h-4 w-4 text-green-500 mr-1" />
                  ) : (
                    <XCircleIcon className="h-4 w-4 text-red-500 mr-1" />
                  )}
                  <span
                    className={`text-xs font-medium ${
                      docType.isActive ? "text-green-600" : "text-red-600"
                    }`}
                  >
                    {docType.isActive ? "Actif" : "Inactif"}
                  </span>
                </div>
              </div>
              <div className="flex space-x-2">
                <button
                  onClick={() => openEditModal(docType)}
                  className="text-blue-600 hover:text-blue-900"
                  title="Modifier"
                >
                  <PencilIcon className="h-4 w-4" />
                </button>
                {!docType.isActive ? (
                  <button
                    onClick={() => handleActivateDocumentType(docType.id)}
                    className="text-green-600 hover:text-green-900"
                    title="Activer"
                  >
                    <CheckCircleIcon className="h-4 w-4" />
                  </button>
                ) : (
                  <button
                    onClick={() => handleDeleteDocumentType(docType.id)}
                    className="text-red-600 hover:text-red-900"
                    title="Supprimer"
                  >
                    <TrashIcon className="h-4 w-4" />
                  </button>
                )}
              </div>
            </div>

            {docType.description && (
              <p className="text-sm text-gray-600 mb-3">
                {docType.description}
              </p>
            )}

            {docType.templatePath && (
              <div className="text-xs text-gray-500">
                Template: {docType.templatePath}
              </div>
            )}

            <div className="text-xs text-gray-400 mt-3">
              Créé le {new Date(docType.createdAt).toLocaleDateString("fr-FR")}
            </div>
          </div>
        ))}
      </div>

      {filteredDocumentTypes.length === 0 && (
        <div className="text-center py-12">
          <div className="text-gray-400 mb-4">
            <svg
              className="mx-auto h-12 w-12"
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
          </div>
          <p className="text-gray-500">Aucun type de document trouvé</p>
        </div>
      )}

      {/* Modal de création */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">
              Créer un nouveau type de document
            </h3>
            <form onSubmit={handleCreateDocumentType} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Libellé
                </label>
                <input
                  type="text"
                  required
                  value={formData.libelle}
                  onChange={(e) =>
                    setFormData({ ...formData, libelle: e.target.value })
                  }
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Ex: Certificat de résidence"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) =>
                    setFormData({ ...formData, description: e.target.value })
                  }
                  rows={3}
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Description du type de document..."
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Chemin du template
                </label>
                <input
                  type="text"
                  value={formData.templatePath}
                  onChange={(e) =>
                    setFormData({ ...formData, templatePath: e.target.value })
                  }
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Ex: templates/certificat_residence.docx"
                />
              </div>
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="isActive"
                  checked={formData.isActive}
                  onChange={(e) =>
                    setFormData({ ...formData, isActive: e.target.checked })
                  }
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label
                  htmlFor="isActive"
                  className="ml-2 block text-sm text-gray-900"
                >
                  Actif
                </label>
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700"
                >
                  Créer
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal de modification */}
      {showEditModal && selectedDocumentType && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h3 className="text-lg font-semibold mb-4">
              Modifier le type de document
            </h3>
            <form onSubmit={handleUpdateDocumentType} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Libellé
                </label>
                <input
                  type="text"
                  required
                  value={formData.libelle}
                  onChange={(e) =>
                    setFormData({ ...formData, libelle: e.target.value })
                  }
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) =>
                    setFormData({ ...formData, description: e.target.value })
                  }
                  rows={3}
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Chemin du template
                </label>
                <input
                  type="text"
                  value={formData.templatePath}
                  onChange={(e) =>
                    setFormData({ ...formData, templatePath: e.target.value })
                  }
                  className="mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="editIsActive"
                  checked={formData.isActive}
                  onChange={(e) =>
                    setFormData({ ...formData, isActive: e.target.checked })
                  }
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label
                  htmlFor="editIsActive"
                  className="ml-2 block text-sm text-gray-900"
                >
                  Actif
                </label>
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50"
                >
                  Annuler
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700"
                >
                  Mettre à jour
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDocumentTypes;
