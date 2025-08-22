// Gestionnaire centralisé des erreurs d'API
export class ApiError extends Error {
  constructor(message, status, code, details = null) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.details = details;
    this.timestamp = new Date();
  }
}

// Codes d'erreur prédéfinis
export const ErrorCodes = {
  // Erreurs d'authentification
  UNAUTHORIZED: "UNAUTHORIZED",
  FORBIDDEN: "FORBIDDEN",
  TOKEN_EXPIRED: "TOKEN_EXPIRED",
  INVALID_CREDENTIALS: "INVALID_CREDENTIALS",

  // Erreurs de validation
  VALIDATION_ERROR: "VALIDATION_ERROR",
  MISSING_REQUIRED_FIELD: "MISSING_REQUIRED_FIELD",
  INVALID_FORMAT: "INVALID_FORMAT",

  // Erreurs de ressources
  RESOURCE_NOT_FOUND: "RESOURCE_NOT_FOUND",
  RESOURCE_ALREADY_EXISTS: "RESOURCE_ALREADY_EXISTS",
  RESOURCE_IN_USE: "RESOURCE_IN_USE",

  // Erreurs de serveur
  INTERNAL_SERVER_ERROR: "INTERNAL_SERVER_ERROR",
  SERVICE_UNAVAILABLE: "SERVICE_UNAVAILABLE",
  DATABASE_ERROR: "DATABASE_ERROR",

  // Erreurs de réseau
  NETWORK_ERROR: "NETWORK_ERROR",
  TIMEOUT_ERROR: "TIMEOUT_ERROR",
  CONNECTION_ERROR: "CONNECTION_ERROR",

  // Erreurs métier
  BUSINESS_RULE_VIOLATION: "BUSINESS_RULE_VIOLATION",
  QUOTA_EXCEEDED: "QUOTA_EXCEEDED",
  OPERATION_NOT_ALLOWED: "OPERATION_NOT_ALLOWED",
};

// Messages d'erreur par défaut
export const ErrorMessages = {
  [ErrorCodes.UNAUTHORIZED]:
    "Vous devez être connecté pour accéder à cette ressource",
  [ErrorCodes.FORBIDDEN]: "Vous n'avez pas les permissions nécessaires",
  [ErrorCodes.TOKEN_EXPIRED]:
    "Votre session a expiré, veuillez vous reconnecter",
  [ErrorCodes.INVALID_CREDENTIALS]: "Identifiants incorrects",
  [ErrorCodes.VALIDATION_ERROR]: "Les données saisies ne sont pas valides",
  [ErrorCodes.MISSING_REQUIRED_FIELD]: "Un champ obligatoire est manquant",
  [ErrorCodes.INVALID_FORMAT]: "Le format des données n'est pas correct",
  [ErrorCodes.RESOURCE_NOT_FOUND]: "La ressource demandée n'existe pas",
  [ErrorCodes.RESOURCE_ALREADY_EXISTS]: "Cette ressource existe déjà",
  [ErrorCodes.RESOURCE_IN_USE]: "Cette ressource est actuellement utilisée",
  [ErrorCodes.INTERNAL_SERVER_ERROR]: "Une erreur interne s'est produite",
  [ErrorCodes.SERVICE_UNAVAILABLE]:
    "Le service est temporairement indisponible",
  [ErrorCodes.DATABASE_ERROR]: "Erreur de base de données",
  [ErrorCodes.NETWORK_ERROR]: "Erreur de connexion réseau",
  [ErrorCodes.TIMEOUT_ERROR]: "La requête a pris trop de temps",
  [ErrorCodes.CONNECTION_ERROR]: "Impossible de se connecter au serveur",
  [ErrorCodes.BUSINESS_RULE_VIOLATION]: "Cette opération n'est pas autorisée",
  [ErrorCodes.QUOTA_EXCEEDED]: "Vous avez dépassé votre quota",
  [ErrorCodes.OPERATION_NOT_ALLOWED]: "Cette opération n'est pas autorisée",
};

// Gestionnaire principal des erreurs d'API
export const handleApiError = (error, context = {}) => {
  let apiError;

  // Si c'est déjà une ApiError, la retourner
  if (error instanceof ApiError) {
    apiError = error;
  } else if (error.response) {
    // Erreur de réponse HTTP
    const { status, data } = error.response;
    const message =
      data?.message ||
      data?.error ||
      ErrorMessages[getErrorCode(status)] ||
      "Erreur inconnue";
    const code = data?.code || getErrorCode(status);

    apiError = new ApiError(message, status, code, data);
  } else if (error.request) {
    // Erreur de requête (pas de réponse)
    apiError = new ApiError(
      "Impossible de contacter le serveur",
      0,
      ErrorCodes.NETWORK_ERROR,
      { originalError: error.message }
    );
  } else {
    // Erreur JavaScript
    apiError = new ApiError(
      error.message || "Une erreur inattendue s'est produite",
      0,
      ErrorCodes.INTERNAL_SERVER_ERROR,
      { originalError: error }
    );
  }

  // Ajouter le contexte
  apiError.context = context;

  // Logger l'erreur
  logError(apiError);

  return apiError;
};

// Déterminer le code d'erreur basé sur le statut HTTP
const getErrorCode = (status) => {
  switch (status) {
    case 400:
      return ErrorCodes.VALIDATION_ERROR;
    case 401:
      return ErrorCodes.UNAUTHORIZED;
    case 403:
      return ErrorCodes.FORBIDDEN;
    case 404:
      return ErrorCodes.RESOURCE_NOT_FOUND;
    case 409:
      return ErrorCodes.RESOURCE_ALREADY_EXISTS;
    case 422:
      return ErrorCodes.VALIDATION_ERROR;
    case 429:
      return ErrorCodes.QUOTA_EXCEEDED;
    case 500:
      return ErrorCodes.INTERNAL_SERVER_ERROR;
    case 503:
      return ErrorCodes.SERVICE_UNAVAILABLE;
    default:
      return ErrorCodes.INTERNAL_SERVER_ERROR;
  }
};

// Logger les erreurs
const logError = (error) => {
  const logData = {
    timestamp: error.timestamp,
    message: error.message,
    status: error.status,
    code: error.code,
    context: error.context,
    stack: error.stack,
    url: window.location.href,
    userAgent: navigator.userAgent,
  };

  // En mode développement, afficher dans la console
  if (process.env.NODE_ENV === "development") {
    console.error("API Error:", logData);
  }

  // En production, envoyer à un service de logging
  if (process.env.NODE_ENV === "production") {
    // Ici vous pourriez envoyer l'erreur à un service comme Sentry, LogRocket, etc.
    // sendToLoggingService(logData);
  }
};

// Gestionnaire d'erreurs pour les composants React
export const useApiErrorHandler = () => {
  const handleError = (error, context = {}) => {
    const apiError = handleApiError(error, context);

    // Retourner un objet avec les informations d'erreur formatées
    return {
      message: apiError.message,
      status: apiError.status,
      code: apiError.code,
      isNetworkError: apiError.code === ErrorCodes.NETWORK_ERROR,
      isAuthError: [
        ErrorCodes.UNAUTHORIZED,
        ErrorCodes.FORBIDDEN,
        ErrorCodes.TOKEN_EXPIRED,
      ].includes(apiError.code),
      isValidationError: apiError.code === ErrorCodes.VALIDATION_ERROR,
      isServerError: apiError.status >= 500,
      shouldRetry: [
        ErrorCodes.NETWORK_ERROR,
        ErrorCodes.TIMEOUT_ERROR,
        ErrorCodes.SERVICE_UNAVAILABLE,
      ].includes(apiError.code),
      retryAfter: apiError.status === 429 ? 60 : null, // Retry after 1 minute for rate limiting
    };
  };

  return { handleError };
};

// Fonction utilitaire pour retry automatique
export const withRetry = async (apiCall, maxRetries = 3, delay = 1000) => {
  let lastError;

  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await apiCall();
    } catch (error) {
      lastError = error;
      const apiError = handleApiError(error);

      if (!apiError.shouldRetry || attempt === maxRetries) {
        throw apiError;
      }

      // Attendre avant de réessayer
      await new Promise((resolve) => setTimeout(resolve, delay * attempt));
    }
  }

  throw lastError;
};

// Fonction utilitaire pour valider les réponses d'API
export const validateApiResponse = (response, schema) => {
  try {
    // Ici vous pourriez utiliser une bibliothèque comme Zod ou Yup pour la validation
    // Pour l'instant, on retourne juste la réponse
    return response;
  } catch (error) {
    throw new ApiError(
      "La réponse du serveur n'est pas valide",
      200,
      ErrorCodes.VALIDATION_ERROR,
      { validationError: error.message }
    );
  }
};

export default {
  ApiError,
  ErrorCodes,
  ErrorMessages,
  handleApiError,
  useApiErrorHandler,
  withRetry,
  validateApiResponse,
};
