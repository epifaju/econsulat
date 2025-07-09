import React from "react";

const Step2Address = ({ formData, updateFormData, pays }) => {
  const handleChange = (field, value) => {
    updateFormData({ [field]: value });
  };

  return (
    <div className="space-y-6">
      <div className="text-center">
        <h3 className="text-xl font-semibold text-gray-800 mb-2">Adresse</h3>
        <p className="text-gray-600">
          Veuillez indiquer votre adresse actuelle
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Nom de rue */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Nom de rue <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.streetName}
            onChange={(e) => handleChange("streetName", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Ex: Avenue de la République"
            required
          />
        </div>

        {/* Numéro */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Numéro <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.streetNumber}
            onChange={(e) => handleChange("streetNumber", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Ex: 123"
            required
          />
        </div>

        {/* Boîte */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Boîte (facultatif)
          </label>
          <input
            type="text"
            value={formData.boxNumber}
            onChange={(e) => handleChange("boxNumber", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Ex: Bte 5"
          />
        </div>

        {/* Code postal */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Code postal <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.postalCode}
            onChange={(e) => handleChange("postalCode", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Ex: 75001"
            required
          />
        </div>

        {/* Ville */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Ville <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={formData.city}
            onChange={(e) => handleChange("city", e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Ex: Paris"
            required
          />
        </div>

        {/* Pays */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Pays <span className="text-red-500">*</span>
          </label>
          <select
            value={formData.countryId}
            onChange={(e) => handleChange("countryId", e.target.value)}
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
              Adresse de correspondance
            </h3>
            <div className="mt-2 text-sm text-blue-700">
              <p>
                Cette adresse sera utilisée pour vous envoyer des documents
                officiels. Assurez-vous qu'elle soit complète et à jour.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Step2Address;
