import React from "react";
import {
  UsersIcon,
  DocumentTextIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  ExclamationTriangleIcon,
  ArrowUpIcon,
  ArrowDownIcon,
} from "@heroicons/react/24/outline";

const DashboardStats = ({ stats, period = "month", onPeriodChange }) => {
  const {
    totalUsers,
    totalDemandes,
    pendingDemandes,
    approvedDemandes,
    rejectedDemandes,
    completedDemandes,
    userGrowth,
    demandeGrowth,
    topDocumentTypes,
    recentActivity,
  } = stats || {};

  const getStatusColor = (status) => {
    switch (status) {
      case "PENDING":
        return "text-yellow-600 bg-yellow-100";
      case "APPROVED":
        return "text-green-600 bg-green-100";
      case "REJECTED":
        return "text-red-600 bg-red-100";
      case "COMPLETED":
        return "text-blue-600 bg-blue-100";
      default:
        return "text-gray-600 bg-gray-100";
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case "PENDING":
        return <ClockIcon className="h-5 w-5" />;
      case "APPROVED":
        return <CheckCircleIcon className="h-5 w-5" />;
      case "REJECTED":
        return <XCircleIcon className="h-5 w-5" />;
      case "COMPLETED":
        return <DocumentTextIcon className="h-5 w-5" />;
      default:
        return <DocumentTextIcon className="h-5 w-5" />;
    }
  };

  const formatNumber = (num) => {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + "M";
    if (num >= 1000) return (num / 1000).toFixed(1) + "K";
    return num.toString();
  };

  const formatPercentage = (value) => {
    if (value > 0) return `+${value}%`;
    if (value < 0) return `${value}%`;
    return "0%";
  };

  return (
    <div className="space-y-6">
      {/* Sélecteur de période */}
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-gray-900">
          Statistiques du tableau de bord
        </h2>
        <div className="flex space-x-2">
          {["week", "month", "quarter", "year"].map((p) => (
            <button
              key={p}
              onClick={() => onPeriodChange?.(p)}
              className={`px-3 py-1 text-sm rounded-md transition-colors ${
                period === p
                  ? "bg-blue-100 text-blue-700 border border-blue-300"
                  : "bg-white text-gray-600 border border-gray-300 hover:bg-gray-50"
              }`}
            >
              {p === "week" && "Semaine"}
              {p === "month" && "Mois"}
              {p === "quarter" && "Trimestre"}
              {p === "year" && "Année"}
            </button>
          ))}
        </div>
      </div>

      {/* Cartes de statistiques principales */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total utilisateurs */}
        <div className="bg-white rounded-lg shadow p-6 border border-gray-200">
          <div className="flex items-center">
            <div className="p-2 bg-blue-100 rounded-lg">
              <UsersIcon className="h-6 w-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">
                Total utilisateurs
              </p>
              <p className="text-2xl font-semibold text-gray-900">
                {formatNumber(totalUsers || 0)}
              </p>
            </div>
          </div>
          {userGrowth !== undefined && (
            <div className="mt-4 flex items-center text-sm">
              {userGrowth >= 0 ? (
                <ArrowUpIcon className="h-4 w-4 text-green-500" />
              ) : (
                <ArrowDownIcon className="h-4 w-4 text-red-500" />
              )}
              <span
                className={`ml-1 ${
                  userGrowth >= 0 ? "text-green-600" : "text-red-600"
                }`}
              >
                {formatPercentage(userGrowth)} ce {period}
              </span>
            </div>
          )}
        </div>

        {/* Total demandes */}
        <div className="bg-white rounded-lg shadow p-6 border border-gray-200">
          <div className="flex items-center">
            <div className="p-2 bg-green-100 rounded-lg">
              <DocumentTextIcon className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">
                Total demandes
              </p>
              <p className="text-2xl font-semibold text-gray-900">
                {formatNumber(totalDemandes || 0)}
              </p>
            </div>
          </div>
          {demandeGrowth !== undefined && (
            <div className="mt-4 flex items-center text-sm">
              {demandeGrowth >= 0 ? (
                <ArrowUpIcon className="h-4 w-4 text-green-500" />
              ) : (
                <ArrowDownIcon className="h-4 w-4 text-red-500" />
              )}
              <span
                className={`ml-1 ${
                  demandeGrowth >= 0 ? "text-green-600" : "text-red-600"
                }`}
              >
                {formatPercentage(demandeGrowth)} ce {period}
              </span>
            </div>
          )}
        </div>

        {/* Demandes en attente */}
        <div className="bg-white rounded-lg shadow p-6 border border-gray-200">
          <div className="flex items-center">
            <div className="p-2 bg-yellow-100 rounded-lg">
              <ClockIcon className="h-6 w-6 text-yellow-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">En attente</p>
              <p className="text-2xl font-semibold text-gray-900">
                {formatNumber(pendingDemandes || 0)}
              </p>
            </div>
          </div>
          <div className="mt-4">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">Taux de traitement</span>
              <span className="font-medium text-gray-900">
                {totalDemandes > 0
                  ? Math.round(
                      ((totalDemandes - pendingDemandes) / totalDemandes) * 100
                    )
                  : 0}
                %
              </span>
            </div>
            <div className="mt-2 w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-yellow-500 h-2 rounded-full transition-all duration-300"
                style={{
                  width: `${
                    totalDemandes > 0
                      ? (pendingDemandes / totalDemandes) * 100
                      : 0
                  }%`,
                }}
              ></div>
            </div>
          </div>
        </div>

        {/* Demandes approuvées */}
        <div className="bg-white rounded-lg shadow p-6 border border-gray-200">
          <div className="flex items-center">
            <div className="p-2 bg-green-100 rounded-lg">
              <CheckCircleIcon className="h-6 w-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Approuvées</p>
              <p className="text-2xl font-semibold text-gray-900">
                {formatNumber(approvedDemandes || 0)}
              </p>
            </div>
          </div>
          <div className="mt-4">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">Taux d'approbation</span>
              <span className="font-medium text-gray-900">
                {totalDemandes > 0
                  ? Math.round((approvedDemandes / totalDemandes) * 100)
                  : 0}
                %
              </span>
            </div>
            <div className="mt-2 w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-green-500 h-2 rounded-full transition-all duration-300"
                style={{
                  width: `${
                    totalDemandes > 0
                      ? (approvedDemandes / totalDemandes) * 100
                      : 0
                  }%`,
                }}
              ></div>
            </div>
          </div>
        </div>
      </div>

      {/* Graphique des types de documents */}
      {topDocumentTypes && topDocumentTypes.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 border border-gray-200">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Types de documents les plus demandés
          </h3>
          <div className="space-y-3">
            {topDocumentTypes.map((type, index) => (
              <div
                key={type.name}
                className="flex items-center justify-between"
              >
                <div className="flex items-center space-x-3">
                  <span className="text-sm font-medium text-gray-600 w-8">
                    #{index + 1}
                  </span>
                  <span className="text-sm text-gray-900">{type.name}</span>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-32 bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                      style={{
                        width: `${
                          (type.count /
                            Math.max(...topDocumentTypes.map((t) => t.count))) *
                          100
                        }%`,
                      }}
                    ></div>
                  </div>
                  <span className="text-sm font-medium text-gray-900 w-12 text-right">
                    {type.count}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Activité récente */}
      {recentActivity && recentActivity.length > 0 && (
        <div className="bg-white rounded-lg shadow p-6 border border-gray-200">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Activité récente
          </h3>
          <div className="space-y-3">
            {recentActivity.map((activity, index) => (
              <div
                key={index}
                className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg"
              >
                <div
                  className={`p-2 rounded-lg ${getStatusColor(
                    activity.status
                  )}`}
                >
                  {getStatusIcon(activity.status)}
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-900">
                    {activity.description}
                  </p>
                  <p className="text-xs text-gray-500">
                    {new Date(activity.timestamp).toLocaleString()}
                  </p>
                </div>
                <span
                  className={`text-xs px-2 py-1 rounded-full ${getStatusColor(
                    activity.status
                  )}`}
                >
                  {activity.status}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default DashboardStats;
