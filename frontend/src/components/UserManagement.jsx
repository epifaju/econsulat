import React, { useState, useEffect } from "react";
import { useAuth } from "../contexts/AuthContext";
import {
  UsersIcon,
  UserPlusIcon,
  EyeIcon,
  PencilIcon,
  TrashIcon,
  ShieldCheckIcon,
  UserIcon,
} from "@heroicons/react/24/outline";
import SearchAndFilters from "./SearchAndFilters";
import Pagination from "./Pagination";
import Notification from "./Notification";

const UserManagement = () => {
  const { token } = useAuth();
  const [users, setUsers] = useState([]);
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
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/users", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else {
        setError("Erreur lors du chargement des utilisateurs");
        showNotification(
          "error",
          "Erreur",
          "Impossible de charger les utilisateurs"
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
    totalUsers: users.length,
    adminUsers: users.filter((u) => u.role === "ADMIN").length,
    agentUsers: users.filter((u) => u.role === "AGENT").length,
    regularUsers: users.filter((u) => u.role === "USER").length,
    verifiedUsers: users.filter((u) => u.emailVerified).length,
    unverifiedUsers: users.filter((u) => !u.emailVerified).length,
  };

  // Filtrage et recherche
  const filteredUsers = users.filter((user) => {
    const matchesSearch =
      searchTerm === "" ||
      user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesFilters = Object.keys(filters).every((key) => {
      if (!filters[key]) return true;
      return user[key] === filters[key];
    });

    return matchesSearch && matchesFilters;
  });

  // Tri
  const sortedUsers = [...filteredUsers].sort((a, b) => {
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
  const currentUsers = sortedUsers.slice(indexOfFirstItem, indexOfLastItem);

  const handleSort = (key) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === "asc" ? "desc" : "asc",
    }));
  };

  const getRoleBadge = (role) => {
    switch (role) {
      case "ADMIN":
        return <span className="badge badge-admin">Administrateur</span>;
      case "AGENT":
        return <span className="badge badge-user">Agent</span>;
      case "USER":
        return <span className="badge badge-user">Utilisateur</span>;
      default:
        return <span className="badge badge-user">{role}</span>;
    }
  };

  const getVerificationBadge = (verified) => {
    return verified ? (
      <span className="badge badge-approved">Vérifié</span>
    ) : (
      <span className="badge badge-pending">Non vérifié</span>
    );
  };

  const filterOptions = {
    role: {
      label: "Rôle",
      values: [
        { value: "ADMIN", label: "Administrateur" },
        { value: "AGENT", label: "Agent" },
        { value: "USER", label: "Utilisateur" },
      ],
    },
    emailVerified: {
      label: "Vérification email",
      values: [
        { value: "true", label: "Vérifié" },
        { value: "false", label: "Non vérifié" },
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
          <UsersIcon className="h-12 w-12 text-red-500 mx-auto" />
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
            Gestion des utilisateurs
          </h1>
          <p className="mt-2 text-gray-600">
            Gérez les comptes utilisateurs, les rôles et les permissions de
            l'application.
          </p>
        </div>

        {/* Cartes de statistiques */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          <div className="stats-card">
            <div className="stats-card-header">
              <div className="flex items-center">
                <div className="stats-card-icon">
                  <UsersIcon className="h-6 w-6 text-gray-400" />
                </div>
                <div className="stats-card-content">
                  <p className="stats-card-title">Total Utilisateurs</p>
                  <p className="stats-card-value">{stats.totalUsers}</p>
                </div>
              </div>
            </div>
          </div>

          <div className="stats-card">
            <div className="stats-card-header">
              <div className="flex items-center">
                <div className="stats-card-icon">
                  <ShieldCheckIcon className="h-6 w-6 text-gray-400" />
                </div>
                <div className="stats-card-content">
                  <p className="stats-card-title">Administrateurs</p>
                  <p className="stats-card-value">{stats.adminUsers}</p>
                </div>
              </div>
            </div>
          </div>

          <div className="stats-card">
            <div className="stats-card-header">
              <div className="flex items-center">
                <div className="stats-card-icon">
                  <UserIcon className="h-6 w-6 text-gray-400" />
                </div>
                <div className="stats-card-content">
                  <p className="stats-card-title">Agents</p>
                  <p className="stats-card-value">{stats.agentUsers}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Section Utilisateurs */}
        <div className="card">
          <div className="card-header">
            <div className="flex items-center justify-between">
              <h2 className="card-title">Liste des utilisateurs</h2>
              <button className="btn-primary">
                <UserPlusIcon className="h-4 w-4 mr-2" />
                Ajouter un utilisateur
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
            placeholder="Rechercher par nom, prénom ou email..."
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
                    onClick={() => handleSort("email")}
                  >
                    Email
                    {sortConfig.key === "email" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("role")}
                  >
                    Rôle
                    {sortConfig.key === "role" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("emailVerified")}
                  >
                    Vérification
                    {sortConfig.key === "emailVerified" && (
                      <span className="ml-1">
                        {sortConfig.direction === "asc" ? "↑" : "↓"}
                      </span>
                    )}
                  </th>
                  <th
                    className="table-header-cell"
                    onClick={() => handleSort("createdAt")}
                  >
                    Date d'inscription
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
                {currentUsers.map((user) => (
                  <tr key={user.id} className="table-row">
                    <td className="table-cell">
                      <div>
                        <div className="font-medium text-gray-900">
                          {user.firstName} {user.lastName}
                        </div>
                        <div className="text-gray-500">ID: {user.id}</div>
                      </div>
                    </td>
                    <td className="table-cell">
                      <div className="text-sm text-gray-900">{user.email}</div>
                    </td>
                    <td className="table-cell">{getRoleBadge(user.role)}</td>
                    <td className="table-cell">
                      {getVerificationBadge(user.emailVerified)}
                    </td>
                    <td className="table-cell">
                      {new Date(user.createdAt).toLocaleDateString("fr-FR")}
                    </td>
                    <td className="table-cell">
                      <div className="flex space-x-2">
                        <button
                          className="btn-secondary btn-sm"
                          title="Voir détails"
                        >
                          <EyeIcon className="h-4 w-4" />
                        </button>
                        <button
                          className="btn-secondary btn-sm"
                          title="Modifier"
                        >
                          <PencilIcon className="h-4 w-4" />
                        </button>
                        <button className="btn-danger btn-sm" title="Supprimer">
                          <TrashIcon className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          {filteredUsers.length > 0 && (
            <Pagination
              currentPage={currentPage}
              totalPages={Math.ceil(filteredUsers.length / itemsPerPage)}
              onPageChange={setCurrentPage}
              itemsPerPage={itemsPerPage}
              totalItems={filteredUsers.length}
            />
          )}

          {filteredUsers.length === 0 && (
            <div className="text-center py-8">
              <UsersIcon className="h-12 w-12 text-gray-400 mx-auto" />
              <p className="mt-2 text-gray-500">Aucun utilisateur trouvé</p>
              <button className="btn-primary mt-4">
                <UserPlusIcon className="h-4 w-4 mr-2" />
                Ajouter le premier utilisateur
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default UserManagement;
