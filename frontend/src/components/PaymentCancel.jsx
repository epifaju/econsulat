import React from "react";
import { Link } from "react-router-dom";
import { XCircleIcon } from "@heroicons/react/24/solid";

const PaymentCancel = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8 text-center">
        <XCircleIcon className="w-16 h-16 text-amber-500 mx-auto mb-4" />
        <h1 className="text-xl font-semibold text-gray-900 mb-2">
          Paiement annulé
        </h1>
        <p className="text-gray-600 mb-6">
          Vous avez annulé le paiement. Votre demande est enregistrée et reste
          en attente de paiement. Vous pouvez la retrouver sur votre tableau de
          bord et réessayer de payer.
        </p>
        <div className="space-x-3">
          <Link
            to="/dashboard"
            className="inline-block px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Tableau de bord
          </Link>
        </div>
      </div>
    </div>
  );
};

export default PaymentCancel;
