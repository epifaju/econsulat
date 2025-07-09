import React from "react";

const Step3Filiation = ({ formData, updateFormData, pays }) => {
  const handleChange = (field, value) => {
    updateFormData({ [field]: value });
  };

  return (
    <div className="space-y-8">
      <div className="text-center">
        <h3 className="text-xl font-semibold text-gray-800 mb-2">Filiation</h3>
        <p className="text-gray-600">
          Veuillez remplir les informations concernant vos parents
        </p>
      </div>

      {/* Informations du père */}
      <div className="bg-gray-50 p-6 rounded-lg">
        <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
          <svg
            className="w-5 h-5 mr-2 text-blue-600"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              fillRule="evenodd"
              d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
              clipRule="evenodd"
            />
          </svg>
          Informations du père
        </h4>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Prénom du père <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.fatherFirstName}
              onChange={(e) => handleChange("fatherFirstName", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Prénom du père"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Nom du père <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.fatherLastName}
              onChange={(e) => handleChange("fatherLastName", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Nom du père"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Date de naissance du père <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={formData.fatherBirthDate}
              onChange={(e) => handleChange("fatherBirthDate", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Lieu de naissance du père <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.fatherBirthPlace}
              onChange={(e) => handleChange("fatherBirthPlace", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Ville de naissance"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Pays de naissance du père <span className="text-red-500">*</span>
            </label>
            <select
              value={formData.fatherBirthCountryId}
              onChange={(e) =>
                handleChange("fatherBirthCountryId", e.target.value)
              }
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            >
              <option value="">Sélectionnez un pays</option>
              {pays.map((pays) => (
                <option key={pays.id} value={pays.id}>
                  {pays.libelle}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Informations de la mère */}
      <div className="bg-gray-50 p-6 rounded-lg">
        <h4 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
          <svg
            className="w-5 h-5 mr-2 text-pink-600"
            fill="currentColor"
            viewBox="0 0 20 20"
          >
            <path
              fillRule="evenodd"
              d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
              clipRule="evenodd"
            />
          </svg>
          Informations de la mère
        </h4>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Prénom de la mère <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.motherFirstName}
              onChange={(e) => handleChange("motherFirstName", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Prénom de la mère"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Nom de la mère <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.motherLastName}
              onChange={(e) => handleChange("motherLastName", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Nom de la mère"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Date de naissance de la mère{" "}
              <span className="text-red-500">*</span>
            </label>
            <input
              type="date"
              value={formData.motherBirthDate}
              onChange={(e) => handleChange("motherBirthDate", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Lieu de naissance de la mère{" "}
              <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={formData.motherBirthPlace}
              onChange={(e) => handleChange("motherBirthPlace", e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Ville de naissance"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Pays de naissance de la mère{" "}
              <span className="text-red-500">*</span>
            </label>
            <select
              value={formData.motherBirthCountryId}
              onChange={(e) =>
                handleChange("motherBirthCountryId", e.target.value)
              }
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            >
              <option value="">Sélectionnez un pays</option>
              {pays.map((pays) => (
                <option key={pays.id} value={pays.id}>
                  {pays.libelle}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      <div className="bg-blue-50 p-4 rounded-lg">
        <div className="flex items-start">
          <div className="flex-shrink-0">
            <svg
              className="h-5 w-5 text-blue-400"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fillRule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                clipRule="evenodd"
              />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-blue-800">
              Informations de filiation
            </h3>
            <div className="mt-2 text-sm text-blue-700">
              <p>
                Ces informations sont nécessaires pour l'établissement de
                documents officiels. Assurez-vous qu'elles correspondent aux
                actes de naissance de vos parents.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Step3Filiation;
