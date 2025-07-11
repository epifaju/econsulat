/**
 * Utilitaire pour gérer les téléchargements de fichiers
 */

/**
 * Télécharge un blob avec un nom de fichier spécifique
 * @param {Blob} blob - Le blob à télécharger
 * @param {string} fileName - Le nom du fichier
 * @param {string} mimeType - Le type MIME (optionnel)
 * @returns {Promise<boolean>} - True si le téléchargement a réussi
 */
export const downloadBlob = async (blob, fileName, mimeType = null) => {
  try {
    // Vérifier que le blob est valide
    if (!blob || blob.size === 0) {
      throw new Error("Blob invalide ou vide");
    }

    // Créer l'URL du blob
    const url = window.URL.createObjectURL(blob);

    // Créer l'élément de téléchargement
    const link = document.createElement("a");
    link.href = url;
    link.download = fileName;
    link.style.display = "none";

    // Ajouter au DOM
    document.body.appendChild(link);

    // Déclencher le téléchargement
    link.click();

    // Nettoyer
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    console.log(`Téléchargement réussi: ${fileName} (${blob.size} bytes)`);
    return true;
  } catch (error) {
    console.error("Erreur lors du téléchargement:", error);
    throw error;
  }
};

/**
 * Télécharge un fichier depuis une URL
 * @param {string} url - L'URL du fichier
 * @param {string} fileName - Le nom du fichier
 * @param {Object} options - Options de la requête
 * @returns {Promise<boolean>} - True si le téléchargement a réussi
 */
export const downloadFromUrl = async (url, fileName, options = {}) => {
  try {
    const response = await fetch(url, {
      method: "GET",
      ...options,
    });

    if (!response.ok) {
      throw new Error(`Erreur HTTP: ${response.status} ${response.statusText}`);
    }

    const blob = await response.blob();

    // Vérifier le type de contenu
    const contentType = response.headers.get("content-type");
    console.log("Type de contenu reçu:", contentType);

    return await downloadBlob(blob, fileName, contentType);
  } catch (error) {
    console.error("Erreur lors du téléchargement depuis URL:", error);
    throw error;
  }
};

/**
 * Vérifie si un blob est un PDF valide
 * @param {Blob} blob - Le blob à vérifier
 * @returns {boolean} - True si c'est un PDF valide
 */
export const isValidPdf = (blob) => {
  if (!blob) return false;

  // Vérifier le type MIME
  if (blob.type && blob.type !== "application/pdf") {
    console.warn("Type MIME inattendu:", blob.type);
    return false;
  }

  // Vérifier la taille (un PDF ne peut pas être vide)
  if (blob.size === 0) {
    console.warn("Blob vide");
    return false;
  }

  // Vérifier la taille minimale (un PDF simple fait au moins quelques centaines de bytes)
  if (blob.size < 100) {
    console.warn(
      "Blob trop petit pour être un PDF valide:",
      blob.size,
      "bytes"
    );
    return false;
  }

  return true;
};

/**
 * Ouvre un PDF dans un nouvel onglet
 * @param {Blob} blob - Le blob PDF
 * @param {string} fileName - Le nom du fichier
 */
export const openPdfInNewTab = (blob, fileName) => {
  try {
    const url = window.URL.createObjectURL(blob);
    const newWindow = window.open(url, "_blank");

    if (!newWindow) {
      console.warn("Popup bloqué, tentative de téléchargement...");
      downloadBlob(blob, fileName);
    } else {
      // Nettoyer l'URL quand la fenêtre se ferme
      newWindow.onbeforeunload = () => {
        window.URL.revokeObjectURL(url);
      };
    }
  } catch (error) {
    console.error("Erreur lors de l'ouverture du PDF:", error);
    // Fallback vers le téléchargement
    downloadBlob(blob, fileName);
  }
};

/**
 * Gère le téléchargement d'un PDF avec options
 * @param {Blob} blob - Le blob PDF
 * @param {string} fileName - Le nom du fichier
 * @param {Object} options - Options (openInNewTab, forceDownload)
 */
export const handlePdfDownload = async (blob, fileName, options = {}) => {
  const { openInNewTab = false, forceDownload = false } = options;

  try {
    // Vérifier que c'est un PDF valide
    if (!isValidPdf(blob)) {
      throw new Error("Le fichier reçu n'est pas un PDF valide");
    }

    if (openInNewTab && !forceDownload) {
      openPdfInNewTab(blob, fileName);
    } else {
      await downloadBlob(blob, fileName);
    }

    return true;
  } catch (error) {
    console.error("Erreur lors du traitement du PDF:", error);
    throw error;
  }
};

/**
 * Formate la taille d'un fichier en bytes lisible
 * @param {number} bytes - Taille en bytes
 * @returns {string} - Taille formatée
 */
export const formatFileSize = (bytes) => {
  if (bytes === 0) return "0 Bytes";

  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

/**
 * Extrait l'extension d'un nom de fichier
 * @param {string} fileName - Le nom du fichier
 * @returns {string} - L'extension (avec le point)
 */
export const getFileExtension = (fileName) => {
  const lastDotIndex = fileName.lastIndexOf(".");
  return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
};

/**
 * S'assure qu'un nom de fichier a la bonne extension
 * @param {string} fileName - Le nom du fichier
 * @param {string} expectedExtension - L'extension attendue (ex: '.pdf')
 * @returns {string} - Le nom de fichier avec la bonne extension
 */
export const ensureFileExtension = (fileName, expectedExtension) => {
  const currentExtension = getFileExtension(fileName);
  const expectedExt = expectedExtension.startsWith(".")
    ? expectedExtension
    : `.${expectedExtension}`;

  if (currentExtension.toLowerCase() !== expectedExt.toLowerCase()) {
    // Remplacer l'extension existante ou en ajouter une nouvelle
    if (currentExtension) {
      return fileName.substring(0, fileName.lastIndexOf(".")) + expectedExt;
    } else {
      return fileName + expectedExt;
    }
  }

  return fileName;
};
