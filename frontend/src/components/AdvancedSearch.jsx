import React, { useState, useEffect, useMemo } from "react";
import {
  MagnifyingGlassIcon,
  FunnelIcon,
  XMarkIcon,
} from "@heroicons/react/24/outline";

const AdvancedSearch = ({
  data,
  searchFields,
  filterFields,
  onSearch,
  placeholder = "Rechercher...",
  className = "",
}) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [filters, setFilters] = useState({});
  const [showFilters, setShowFilters] = useState(false);
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });

  // Recherche et filtrage en temps réel
  const filteredData = useMemo(() => {
    let result = [...data];

    // Appliquer la recherche
    if (searchTerm.trim()) {
      result = result.filter((item) => {
        return searchFields.some((field) => {
          const value = getNestedValue(item, field);
          return (
            value &&
            value.toString().toLowerCase().includes(searchTerm.toLowerCase())
          );
        });
      });
    }

    // Appliquer les filtres
    Object.keys(filters).forEach((filterKey) => {
      if (filters[filterKey] && filters[filterKey] !== "") {
        result = result.filter((item) => {
          const value = getNestedValue(item, filterKey);
          return value === filters[filterKey];
        });
      }
    });

    // Appliquer le tri
    if (sortConfig.key) {
      result.sort((a, b) => {
        const aValue = getNestedValue(a, sortConfig.key);
        const bValue = getNestedValue(b, sortConfig.key);

        if (aValue < bValue) return sortConfig.direction === "asc" ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === "asc" ? 1 : -1;
        return 0;
      });
    }

    return result;
  }, [data, searchTerm, filters, sortConfig, searchFields]);

  // Obtenir la valeur d'un champ imbriqué (ex: "user.name")
  const getNestedValue = (obj, path) => {
    return path
      .split(".")
      .reduce((current, key) => current && current[key], obj);
  };

  // Gérer le changement de terme de recherche
  const handleSearchChange = (value) => {
    setSearchTerm(value);
    onSearch(filteredData);
  };

  // Gérer le changement de filtre
  const handleFilterChange = (filterKey, value) => {
    const newFilters = { ...filters, [filterKey]: value };
    setFilters(newFilters);
    onSearch(filteredData);
  };

  // Gérer le tri
  const handleSort = (key) => {
    setSortConfig((prev) => ({
      key,
      direction: prev.key === key && prev.direction === "asc" ? "desc" : "asc",
    }));
  };

  // Réinitialiser tous les filtres
  const resetFilters = () => {
    setFilters({});
    setSearchTerm("");
    setSortConfig({ key: null, direction: "asc" });
    onSearch(data);
  };

  // Obtenir le nombre de filtres actifs
  const activeFiltersCount = Object.keys(filters).filter(
    (key) => filters[key] && filters[key] !== ""
  ).length;

  // Obtenir les options uniques pour un champ de filtre
  const getFilterOptions = (fieldKey) => {
    const values = data
      .map((item) => getNestedValue(item, fieldKey))
      .filter(Boolean);
    return [...new Set(values)].sort();
  };

  useEffect(() => {
    onSearch(filteredData);
  }, [filteredData, onSearch]);

  return (
    <div className={`space-y-4 ${className}`}>
      {/* Barre de recherche principale */}
      <div className="relative">
        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
          <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" />
        </div>
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => handleSearchChange(e.target.value)}
          placeholder={placeholder}
          className="block w-full pl-10 pr-12 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
        />
        <div className="absolute inset-y-0 right-0 flex items-center">
          <button
            onClick={() => setShowFilters(!showFilters)}
            className={`p-2 rounded-md transition-colors ${
              showFilters
                ? "bg-blue-100 text-blue-600"
                : "text-gray-400 hover:text-gray-600"
            }`}
            title="Filtres avancés"
          >
            <FunnelIcon className="h-5 w-5" />
            {activeFiltersCount > 0 && (
              <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                {activeFiltersCount}
              </span>
            )}
          </button>
        </div>
      </div>

      {/* Filtres avancés */}
      {showFilters && (
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-medium text-gray-700">
              Filtres avancés
            </h3>
            <button
              onClick={resetFilters}
              className="text-sm text-gray-500 hover:text-gray-700 flex items-center space-x-1"
            >
              <XMarkIcon className="h-4 w-4" />
              <span>Réinitialiser</span>
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filterFields.map((field) => (
              <div key={field.key}>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {field.label}
                </label>
                <select
                  value={filters[field.key] || ""}
                  onChange={(e) =>
                    handleFilterChange(field.key, e.target.value)
                  }
                  className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">Tous</option>
                  {getFilterOptions(field.key).map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </div>
            ))}
          </div>

          {/* Options de tri */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Trier par
            </label>
            <div className="flex flex-wrap gap-2">
              {searchFields.map((field) => (
                <button
                  key={field}
                  onClick={() => handleSort(field)}
                  className={`px-3 py-1 text-sm rounded-md border transition-colors ${
                    sortConfig.key === field
                      ? "bg-blue-100 border-blue-300 text-blue-700"
                      : "bg-white border-gray-300 text-gray-700 hover:bg-gray-50"
                  }`}
                >
                  {field}
                  {sortConfig.key === field && (
                    <span className="ml-1">
                      {sortConfig.direction === "asc" ? "↑" : "↓"}
                    </span>
                  )}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Résumé des résultats */}
      <div className="text-sm text-gray-500">
        {filteredData.length} résultat{filteredData.length !== 1 ? "s" : ""}{" "}
        trouvé{filteredData.length !== 1 ? "s" : ""}
        {searchTerm && ` pour "${searchTerm}"`}
        {activeFiltersCount > 0 &&
          ` avec ${activeFiltersCount} filtre${
            activeFiltersCount !== 1 ? "s" : ""
          } actif${activeFiltersCount !== 1 ? "s" : ""}`}
      </div>
    </div>
  );
};

export default AdvancedSearch;
