import React, { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { CheckCircleIcon } from "@heroicons/react/24/solid";
import API_CONFIG from "../config/api";

const PaymentSuccess = () => {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const sessionId = searchParams.get("session_id");
  const [confirmed, setConfirmed] = useState(false);

  // Confirmer le paiement côté backend (au cas où le webhook n'a pas été reçu)
  useEffect(() => {
    if (!sessionId) return;
    const url = `${API_CONFIG.BASE_URL}/api/payment/confirm-session?session_id=${encodeURIComponent(sessionId)}`;
    fetch(url)
      .then((res) => res.ok && res.json())
      .then((data) => data && data.confirmed && setConfirmed(true))
      .catch(() => {});
  }, [sessionId]);

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8 text-center">
        <CheckCircleIcon className="w-16 h-16 text-green-500 mx-auto mb-4" />
        <h1 className="text-xl font-semibold text-gray-900 mb-2">
          {t("dashboard.notifications.paymentSuccessTitle", "Paiement réussi")}
        </h1>
        <p className="text-gray-600 mb-2">
          {t("dashboard.notifications.paymentSuccessMessage", "Votre demande a bien été enregistrée et payée. Elle sera traitée par notre équipe.")}
        </p>
        <p className="text-sm text-gray-500 mb-6">
          {t("dashboard.notifications.emailConfirmationSent")}
        </p>
        {sessionId && (
          <p className="text-xs text-gray-400 mb-4">
            Référence : {sessionId.slice(0, 20)}…
          </p>
        )}
        <Link
          to="/dashboard"
          className="inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
        >
          {t("dashboard.requests.backToDashboard")}
        </Link>
      </div>
    </div>
  );
};

export default PaymentSuccess;
