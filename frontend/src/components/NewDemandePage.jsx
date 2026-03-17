import React from "react";
import { useNavigate } from "react-router-dom";
import NewDemandeForm from "./NewDemandeForm";

/**
 * Page dédiée "Nouvelle demande" (route /new-demande).
 * Réutilise NewDemandeForm avec redirection après fermeture ou succès vers "Mon dossier".
 */
const NewDemandePage = () => {
  const navigate = useNavigate();

  const handleClose = () => {
    navigate("/history");
  };

  const handleSuccess = () => {
    navigate("/history");
  };

  return (
    <NewDemandeForm onClose={handleClose} onSuccess={handleSuccess} />
  );
};

export default NewDemandePage;
