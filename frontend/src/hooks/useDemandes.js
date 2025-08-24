import { useState, useEffect, useCallback } from "react";
import { useAuth } from "../contexts/AuthContext";

export const useDemandes = () => {
  const { token } = useAuth();
  const [demandes, setDemandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const fetchDemandes = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      console.log("🔍 Debug - Token utilisé pour fetchDemandes:", token);

      const response = await fetch("http://127.0.0.1:8080/api/demandes/my", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      console.log(
        "🔍 Debug - Réponse fetchDemandes:",
        response.status,
        response.statusText
      );

      if (response.ok) {
        const data = await response.json();
        setDemandes(data);
      } else if (response.status === 403) {
        setError("Erreur d'authentification. Veuillez vous reconnecter.");
        // Rediriger vers la page de connexion
        window.location.href = "/login";
      } else {
        // Essayer de récupérer le message d'erreur, sinon utiliser un message par défaut
        let errorMessage = "Erreur lors du chargement des demandes";
        try {
          const errorData = await response.json();
          if (errorData && errorData.message) {
            errorMessage = errorData.message;
          }
        } catch (parseError) {
          console.warn(
            "⚠️ Impossible de parser la réponse d'erreur:",
            parseError
          );
          // Utiliser le message par défaut
        }
        setError(errorMessage);
      }
    } catch (err) {
      console.error("❌ Erreur de connexion dans fetchDemandes:", err);
      setError("Erreur de connexion au serveur");
    } finally {
      setLoading(false);
    }
  }, [token]);

  const refreshDemandes = useCallback(() => {
    setRefreshTrigger((prev) => prev + 1);
  }, []);

  const addDemande = useCallback((newDemande) => {
    setDemandes((prev) => [newDemande, ...prev]);
  }, []);

  const updateDemande = useCallback((demandeId, updates) => {
    setDemandes((prev) =>
      prev.map((demande) =>
        demande.id === demandeId ? { ...demande, ...updates } : demande
      )
    );
  }, []);

  const removeDemande = useCallback((demandeId) => {
    setDemandes((prev) => prev.filter((demande) => demande.id !== demandeId));
  }, []);

  useEffect(() => {
    if (token) {
      fetchDemandes();
    }
  }, [fetchDemandes, refreshTrigger]);

  return {
    demandes,
    loading,
    error,
    refreshDemandes,
    addDemande,
    updateDemande,
    removeDemande,
    fetchDemandes,
  };
};
