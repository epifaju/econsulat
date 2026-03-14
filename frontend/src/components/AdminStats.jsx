import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {
  DocumentTextIcon,
  UsersIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  DocumentArrowDownIcon,
} from "@heroicons/react/24/outline";
import API_CONFIG, { buildApiUrl } from "../config/api";

const AdminStats = ({ token }) => {
  const { t } = useTranslation();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const response = await fetch(buildApiUrl(API_CONFIG.ADMIN.STATS), {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setStats(data);
      } else {
        setError(t("admin.stats.loadError"));
      }
    } catch (err) {
      setError(t("dashboard.notifications.connectionError"));
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    {
      title: t("admin.stats.totalDemandes"),
      value: stats?.totalDemandes || 0,
      icon: DocumentTextIcon,
      color: "bg-blue-500",
      description: t("admin.stats.totalDemandesDesc"),
    },
    {
      title: t("admin.stats.pending"),
      value: stats?.pendingDemandes || 0,
      icon: ClockIcon,
      color: "bg-yellow-500",
      description: t("admin.stats.pendingDesc"),
    },
    {
      title: t("admin.stats.approved"),
      value: stats?.approvedDemandes || 0,
      icon: CheckCircleIcon,
      color: "bg-green-500",
      description: t("admin.stats.approvedDesc"),
    },
    {
      title: t("admin.stats.rejected"),
      value: stats?.rejectedDemandes || 0,
      icon: XCircleIcon,
      color: "bg-red-500",
      description: t("admin.stats.rejectedDesc"),
    },
    {
      title: t("admin.stats.completed"),
      value: stats?.completedDemandes || 0,
      icon: CheckCircleIcon,
      color: "bg-purple-500",
      description: t("admin.stats.completedDesc"),
    },
    {
      title: t("admin.stats.totalUsers"),
      value: stats?.totalUsers || 0,
      icon: UsersIcon,
      color: "bg-indigo-500",
      description: t("admin.stats.totalUsersDesc"),
    },
    {
      title: t("admin.stats.generatedDocs"),
      value: stats?.totalGeneratedDocuments || 0,
      icon: DocumentArrowDownIcon,
      color: "bg-emerald-500",
      description: t("admin.stats.generatedDocsDesc"),
    },
  ];

  if (loading) {
    return (
      <div className="p-6">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6">
        <div className="text-center">
          <XCircleIcon className="h-12 w-12 text-red-500 mx-auto" />
          <p className="mt-2 text-gray-600">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-900">
          {t("admin.stats.title")}
        </h2>
        <p className="text-gray-600">{t("admin.stats.subtitle")}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <div
              key={index}
              className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex items-center">
                <div className={`p-3 rounded-lg ${stat.color}`}>
                  <Icon className="h-6 w-6 text-white" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">
                    {stat.title}
                  </p>
                  <p className="text-2xl font-bold text-gray-900">
                    {stat.value.toLocaleString()}
                  </p>
                </div>
              </div>
              <p className="mt-2 text-xs text-gray-500">{stat.description}</p>
            </div>
          );
        })}
      </div>

      {/* Graphiques et visualisations supplémentaires */}
      <div className="mt-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            {t("admin.stats.chartTitle")}
          </h3>
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-sm text-gray-600">{t("admin.stats.pendingLabel")}</span>
              <div className="flex items-center">
                <div className="w-32 bg-gray-200 rounded-full h-2 mr-3">
                  <div
                    className="bg-yellow-500 h-2 rounded-full"
                    style={{
                      width: `${
                        stats?.totalDemandes > 0
                          ? (stats.pendingDemandes / stats.totalDemandes) * 100
                          : 0
                      }%`,
                    }}
                  ></div>
                </div>
                <span className="text-sm font-medium text-gray-900">
                  {stats?.totalDemandes > 0
                    ? Math.round(
                        (stats.pendingDemandes / stats.totalDemandes) * 100
                      )
                    : 0}
                  %
                </span>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm text-gray-600">{t("admin.stats.approvedLabel")}</span>
              <div className="flex items-center">
                <div className="w-32 bg-gray-200 rounded-full h-2 mr-3">
                  <div
                    className="bg-green-500 h-2 rounded-full"
                    style={{
                      width: `${
                        stats?.totalDemandes > 0
                          ? (stats.approvedDemandes / stats.totalDemandes) * 100
                          : 0
                      }%`,
                    }}
                  ></div>
                </div>
                <span className="text-sm font-medium text-gray-900">
                  {stats?.totalDemandes > 0
                    ? Math.round(
                        (stats.approvedDemandes / stats.totalDemandes) * 100
                      )
                    : 0}
                  %
                </span>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm text-gray-600">{t("admin.stats.rejectedLabel")}</span>
              <div className="flex items-center">
                <div className="w-32 bg-gray-200 rounded-full h-2 mr-3">
                  <div
                    className="bg-red-500 h-2 rounded-full"
                    style={{
                      width: `${
                        stats?.totalDemandes > 0
                          ? (stats.rejectedDemandes / stats.totalDemandes) * 100
                          : 0
                      }%`,
                    }}
                  ></div>
                </div>
                <span className="text-sm font-medium text-gray-900">
                  {stats?.totalDemandes > 0
                    ? Math.round(
                        (stats.rejectedDemandes / stats.totalDemandes) * 100
                      )
                    : 0}
                  %
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            {t("admin.stats.activityTitle")}
          </h3>
          <div className="space-y-4">
            <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
              <div className="flex items-center">
                <DocumentTextIcon className="h-5 w-5 text-blue-500 mr-3" />
                <span className="text-sm text-gray-700">
                  {t("admin.stats.newRequests")}
                </span>
              </div>
              <span className="text-sm font-medium text-gray-900">
                {stats?.pendingDemandes || 0}
              </span>
            </div>
            <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
              <div className="flex items-center">
                <CheckCircleIcon className="h-5 w-5 text-green-500 mr-3" />
                <span className="text-sm text-gray-700">Documents générés</span>
              </div>
              <span className="text-sm font-medium text-gray-900">
                {stats?.totalGeneratedDocuments || 0}
              </span>
            </div>
            <div className="flex items-center justify-between p-3 bg-purple-50 rounded-lg">
              <div className="flex items-center">
                <UsersIcon className="h-5 w-5 text-purple-500 mr-3" />
                <span className="text-sm text-gray-700">
                  {t("admin.stats.activeUsers")}
                </span>
              </div>
              <span className="text-sm font-medium text-gray-900">
                {stats?.totalUsers || 0}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminStats;
