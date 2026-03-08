import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {
  FaEnvelope,
  FaEye,
  FaClock,
  FaCheckCircle,
  FaExclamationTriangle,
} from "react-icons/fa";

const UserNotifications = () => {
  const { t, i18n } = useTranslation();
  const dateLocale = i18n.language?.startsWith("pt") ? "pt-PT" : "fr-FR";
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedNotification, setSelectedNotification] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchNotifications();
  }, [currentPage]);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      const response = await fetch("/api/notifications/my", {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error(t("dashboard.notificationsTable.fetchError"));
      }

      const data = await response.json();
      setNotifications(data);

      // Calculer le nombre total de pages
      setTotalPages(Math.ceil(data.length / itemsPerPage));
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (statut) => {
    switch (statut) {
      case "ENVOYE":
        return <FaCheckCircle className="text-green-500" title={t("dashboard.notificationsTable.statusSent")} />;
      case "ECHEC":
        return <FaExclamationTriangle className="text-red-500" title={t("dashboard.notificationsTable.statusFailure")} />;
      case "EN_COURS":
        return <FaClock className="text-yellow-500" title={t("dashboard.notificationsTable.statusInProgress")} />;
      default:
        return <FaEnvelope className="text-gray-500" title={t("dashboard.notificationsTable.statusUnknown")} />;
    }
  };

  const getStatusColor = (statut) => {
    switch (statut) {
      case "ENVOYE":
        return "bg-green-100 text-green-800";
      case "ECHEC":
        return "bg-red-100 text-red-800";
      case "EN_COURS":
        return "bg-yellow-100 text-yellow-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString(dateLocale, {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const openNotificationModal = (notification) => {
    setSelectedNotification(notification);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedNotification(null);
  };

  const paginatedNotifications = notifications.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  if (loading) {
    return (
      <div className="flex justify-center items-center p-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-md p-4">
        <div className="flex">
          <FaExclamationTriangle className="text-red-400 mr-2 mt-1" />
          <div className="text-red-700">
            <p className="font-medium">{t("common.error")}</p>
            <p className="text-sm">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md">
      <div className="px-6 py-4 border-b border-gray-200">
        <h2 className="text-xl font-semibold text-gray-900 flex items-center">
          <FaEnvelope className="mr-2 text-blue-600" />
          {t("dashboard.notificationsTable.title")}
        </h2>
        <p className="text-sm text-gray-600 mt-1">
          {t("dashboard.notificationsTable.subtitle")}
        </p>
      </div>

      {notifications.length === 0 ? (
        <div className="px-6 py-8 text-center">
          <FaEnvelope className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">
            {t("dashboard.notificationsTable.noNotifications")}
          </h3>
          <p className="mt-1 text-sm text-gray-500">
            {t("dashboard.notificationsTable.noNotificationsHint")}
          </p>
        </div>
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t("dashboard.notificationsTable.status")}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t("dashboard.notificationsTable.subject")}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t("dashboard.notificationsTable.sentDate")}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t("dashboard.notificationsTable.actions")}
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {paginatedNotifications.map((notification) => (
                  <tr key={notification.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        {getStatusIcon(notification.statut)}
                        <span
                          className={`ml-2 inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(
                            notification.statut
                          )}`}
                        >
                          {notification.statut}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900 max-w-xs truncate">
                        {notification.objet}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {formatDate(notification.dateEnvoi)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => openNotificationModal(notification)}
                        className="text-blue-600 hover:text-blue-900 flex items-center"
                      >
                        <FaEye className="mr-1" />
                        {t("dashboard.notificationsTable.viewContent")}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="px-6 py-3 border-t border-gray-200">
              <div className="flex items-center justify-between">
                <div className="text-sm text-gray-700">
                  {t("dashboard.notificationsTable.pageOf", { current: currentPage, total: totalPages })}
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                    disabled={currentPage === 1}
                    className="px-3 py-1 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  >
                    {t("dashboard.notificationsTable.previous")}
                  </button>
                  <button
                    onClick={() =>
                      setCurrentPage(Math.min(totalPages, currentPage + 1))
                    }
                    disabled={currentPage === totalPages}
                    className="px-3 py-1 text-sm border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                  >
                    {t("dashboard.notificationsTable.next")}
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {/* Modal pour afficher le contenu de la notification */}
      {showModal && selectedNotification && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-medium text-gray-900">
                  Détails de la notification
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
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    {t("dashboard.notificationsTable.subject")}
                  </label>
                  <p className="mt-1 text-sm text-gray-900">
                    {selectedNotification.objet}
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    {t("dashboard.notificationsTable.sentDate")}
                  </label>
                  <p className="mt-1 text-sm text-gray-900">
                    {formatDate(selectedNotification.dateEnvoi)}
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    {t("dashboard.notificationsTable.status")}
                  </label>
                  <div className="mt-1 flex items-center">
                    {getStatusIcon(selectedNotification.statut)}
                    <span
                      className={`ml-2 inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(
                        selectedNotification.statut
                      )}`}
                    >
                      {selectedNotification.statut}
                    </span>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    {t("dashboard.notificationsTable.content")}
                  </label>
                  <div className="mt-1 p-3 bg-gray-50 rounded-md">
                    <pre className="text-sm text-gray-900 whitespace-pre-wrap font-sans">
                      {selectedNotification.contenu}
                    </pre>
                  </div>
                </div>
              </div>

              <div className="mt-6 flex justify-end">
                <button
                  onClick={closeModal}
                  className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
                >
                  {t("common.close")}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserNotifications;
