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

      const response = await fetch("http://localhost:8080/api/demandes/my", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setDemandes(data);
      } else {
        setError("Erreur lors du chargement des demandes");
      }
    } catch (err) {
      setError("Erreur de connexion");
      console.error(err);
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
