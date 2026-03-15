import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useAuth } from "../contexts/AuthContext";
import {
  DocumentTextIcon,
  UsersIcon,
  CogIcon,
  ChartBarIcon,
  CurrencyEuroIcon,
  EyeIcon,
  PencilIcon,
  TrashIcon,
  PlusIcon,
  ArrowDownTrayIcon,
  CheckCircleIcon,
  XCircleIcon,
  ClockIcon,
} from "@heroicons/react/24/outline";
import Notification from "./Notification";
import AdminDemandesList from "./AdminDemandesList";
import AdminUsersList from "./AdminUsersList";
import AdminDocumentTypes from "./AdminDocumentTypes";
import AdminStats from "./AdminStats";
import AdminBilanReport from "./AdminBilanReport";

const AdminDashboard = () => {
  const { t } = useTranslation();
  const { user, token } = useAuth();
  const [activeTab, setActiveTab] = useState("stats");
  const [notification, setNotification] = useState(null);

  const showNotification = (type, title, message) => {
    setNotification({ type, title, message });
    setTimeout(() => setNotification(null), 5000);
  };

  const tabs = [
    { id: "stats", name: t("admin.tabStats"), icon: ChartBarIcon },
    { id: "demandes", name: t("admin.tabDemandes"), icon: DocumentTextIcon },
    { id: "users", name: t("admin.tabUsers"), icon: UsersIcon },
    { id: "document-types", name: t("admin.tabDocumentTypes"), icon: CogIcon },
    { id: "bilan", name: t("admin.tabBilan"), icon: CurrencyEuroIcon },
  ];

  const renderTabContent = () => {
    switch (activeTab) {
      case "stats":
        return <AdminStats token={token} />;
      case "demandes":
        return (
          <AdminDemandesList token={token} onNotification={showNotification} />
        );
      case "users":
        return (
          <AdminUsersList token={token} onNotification={showNotification} />
        );
      case "document-types":
        return (
          <AdminDocumentTypes token={token} onNotification={showNotification} />
        );
      case "bilan":
        return <AdminBilanReport token={token} />;
      default:
        return <AdminStats token={token} />;
    }
  };

  if (!user || user.role !== "ADMIN") {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <XCircleIcon className="h-12 w-12 text-red-500 mx-auto" />
          <h2 className="mt-4 text-xl font-semibold text-gray-900">
            {t("admin.accessDeniedTitle")}
          </h2>
          <p className="mt-2 text-gray-600">
            {t("admin.accessDeniedMessage")}
          </p>
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
            {t("admin.dashboardTitle")}
          </h1>
          <p className="mt-2 text-gray-600">
            {t("admin.dashboardSubtitle")}
          </p>
        </div>

        {/* Onglets */}
        <div className="mb-8">
          <nav className="flex space-x-8" aria-label="Tabs">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center space-x-2 px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                    activeTab === tab.id
                      ? "bg-blue-100 text-blue-700 border-b-2 border-blue-700"
                      : "text-gray-500 hover:text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  <Icon className="h-5 w-5" />
                  <span>{tab.name}</span>
                </button>
              );
            })}
          </nav>
        </div>

        {/* Contenu des onglets */}
        <div className="bg-white rounded-lg shadow">{renderTabContent()}</div>
      </div>
    </div>
  );
};

export default AdminDashboard;
