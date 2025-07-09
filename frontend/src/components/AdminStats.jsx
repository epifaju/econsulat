import React, { useState, useEffect } from "react";
import {
  DocumentTextIcon,
  UsersIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon,
  DocumentArrowDownIcon,
} from "@heroicons/react/24/outline";

const AdminStats = ({ token }) => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/admin/stats", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setStats(data);
      } else {
        setError("Erreur lors du chargement des statistiques");
      }
    } catch (err) {
      setError("Problème de connexion au serveur");
    } finally {
      setLoading(false);
    }
  };

  const statCards = [
    {
      title: "Total Demandes",
      value: stats?.totalDemandes || 0,
      icon: DocumentTextIcon,
      color: "bg-blue-500",
      description: "Toutes les demandes",
    },
    {
      title: "En Attente",
      value: stats?.pendingDemandes || 0,
      icon: ClockIcon,
      color: "bg-yellow-500",
      description: "Demandes en attente",
    },
    {
      title: "Approuvées",
      value: stats?.approvedDemandes || 0,
      icon: CheckCircleIcon,
      color: "bg-green-500",
      description: "Demandes approuvées",
    },
    {
      title: "Rejetées",
      value: stats?.rejectedDemandes || 0,
      icon: XCircleIcon,
      color: "bg-red-500",
      description: "Demandes rejetées",
    },
    {
      title: "Terminées",
      value: stats?.completedDemandes || 0,
      icon: CheckCircleIcon,
      color: "bg-purple-500",
      description: "Demandes terminées",
    },
    {
      title: "Total Utilisateurs",
      value: stats?.totalUsers || 0,
      icon: UsersIcon,
      color: "bg-indigo-500",
      description: "Utilisateurs inscrits",
    },
    {
      title: "Documents Générés",
      value: stats?.totalGeneratedDocuments || 0,
      icon: DocumentArrowDownIcon,
      color: "bg-emerald-500",
      description: "Documents créés",
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
          Statistiques Générales
        </h2>
        <p className="text-gray-600">Vue d'ensemble du système eConsulat</p>
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
            Répartition des Demandes
          </h3>
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-sm text-gray-600">En attente</span>
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
              <span className="text-sm text-gray-600">Approuvées</span>
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
              <span className="text-sm text-gray-600">Rejetées</span>
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
            Activité Récente
          </h3>
          <div className="space-y-4">
            <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
              <div className="flex items-center">
                <DocumentTextIcon className="h-5 w-5 text-blue-500 mr-3" />
                <span className="text-sm text-gray-700">
                  Nouvelles demandes
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
                  Utilisateurs actifs
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
