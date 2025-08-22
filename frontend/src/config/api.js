// Configuration des URLs de l'API
const API_CONFIG = {
  // URL de base du backend
  BASE_URL: "http://127.0.0.1:8080",

  // Endpoints d'authentification
  AUTH: {
    LOGIN: "/api/auth/login",
    REGISTER: "/api/auth/register",
    VERIFY_EMAIL: "/api/auth/verify-email",
  },

  // Endpoints d'administration
  ADMIN: {
    DEMANDES: "/api/admin/demandes",
    DEMANDES_SEARCH: "/api/admin/demandes/search",
    DEMANDES_STATUS: "/api/admin/demandes/status",
    DEMANDE_STATUS_UPDATE: (id) => `/api/admin/demandes/${id}/status`,
    DEMANDE_UPDATE: (id) => `/api/admin/demandes/${id}`,
    DEMANDE_DELETE: (id) => `/api/admin/demandes/${id}`,

    // Documents
    PDF_GENERATE: "/api/admin/pdf-documents/generate",
    PDF_DOWNLOAD: (id) => `/api/admin/pdf-documents/download/${id}`,
    WORD_GENERATE: "/api/admin/documents/generate",
    WORD_DOWNLOAD: (id) => `/api/admin/documents/download/${id}`,

    // Types de documents
    DOCUMENT_TYPES: "/api/admin/document-types",
    DOCUMENT_TYPE_CREATE: "/api/admin/document-types",
    DOCUMENT_TYPE_UPDATE: (id) => `/api/admin/document-types/${id}`,
    DOCUMENT_TYPE_DELETE: (id) => `/api/admin/document-types/${id}`,

    // Utilisateurs
    USERS: "/api/admin/users",
    USER_CREATE: "/api/admin/users",
    USER_UPDATE: (id) => `/api/admin/users/${id}`,
    USER_DELETE: (id) => `/api/admin/users/${id}`,

    // Statistiques
    STATS: "/api/admin/stats",
  },

  // Endpoints citoyens
  CITIZEN: {
    DEMANDES: "/api/citizens/my-requests",
    DEMANDE_CREATE: "/api/citizens/demandes",
    DEMANDE_UPDATE: (id) => `/api/citizens/demandes/${id}`,
    DEMANDE_DELETE: (id) => `/api/citizens/demandes/${id}`,
  },

  // Endpoints de documents
  DOCUMENTS: {
    UPLOAD: "/api/documents/upload",
    DOWNLOAD: (id) => `/api/documents/download/${id}`,
  },
};

// Fonction utilitaire pour construire une URL complète
export const buildApiUrl = (endpoint) => {
  return `${API_CONFIG.BASE_URL}${endpoint}`;
};

// Fonction utilitaire pour construire une URL avec paramètres
export const buildApiUrlWithParams = (endpoint, params) => {
  const url = new URL(`${API_CONFIG.BASE_URL}${endpoint}`);

  if (params) {
    Object.keys(params).forEach((key) => {
      if (params[key] !== null && params[key] !== undefined) {
        url.searchParams.append(key, params[key]);
      }
    });
  }

  return url.toString();
};

export default API_CONFIG;
