import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";

export const useDemandes = () => {
  const { token } = useAuth();
  const [demandes, setDemandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const fetchDemandes = useCallback(async () => {
    if (!token) return;
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get("/api/demandes/my");
      setDemandes(response.data);
    } catch (err) {
      if (err.response?.status === 403) {
        setError("Erreur d'authentification. Veuillez vous reconnecter.");
        window.location.href = "/login";
        return;
      }
      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Erreur lors du chargement des demandes";
      setError(err.response ? message : "Erreur de connexion au serveur");
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
