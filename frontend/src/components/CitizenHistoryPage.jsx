import React, { useState, useEffect, useCallback } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../contexts/AuthContext";
import API_CONFIG from "../config/api";
import {
  DocumentTextIcon,
  ArrowPathIcon,
  EyeIcon,
  DocumentArrowDownIcon,
  PlusIcon,
  ArrowLeftIcon,
} from "@heroicons/react/24/outline";

const CitizenHistoryPage = () => {
  const { t, i18n } = useTranslation();
  const { token } = useAuth();
  const dateLocale = i18n.language?.startsWith("pt") ? "pt-PT" : "fr-FR";
  const [history, setHistory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedDemande, setSelectedDemande] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [filterStatus, setFilterStatus] = useState("");
  const [filterType, setFilterType] = useState("");

  const fetchHistory = useCallback(async () => {
    if (!token) return;
    try {
      setLoading(true);
      setError(null);
      const url = `${API_CONFIG.BASE_URL}${API_CONFIG.ME.HISTORY}`;
      const response = await axios.get(url);
      setHistory(response.data);
    } catch (err) {
      if (err.response?.status === 403) {
        setError(t("history.error"));
        return;
      }
      setError(err.response?.data?.message || err.response?.data?.error || t("history.error"));
    } finally {
      setLoading(false);
    }
  }, [token, t]);

  useEffect(() => {
    if (token) fetchHistory();
  }, [token, fetchHistory]);

  const handleDownload = async (demandeId) => {
    try {
      const url = `${API_CONFIG.BASE_URL}${API_CONFIG.DEMANDES.DOWNLOAD_DOCUMENT(demandeId)}`;
      const response = await axios.get(url, { responseType: "blob" });
      const disposition = response.headers["content-disposition"];
      const fileName = disposition
        ? disposition.split("filename=")[1]?.replace(/"/g, "").trim() || `document-${demandeId}.pdf`
        : `document-${demandeId}.pdf`;
      const blob = new Blob([response.data], { type: "application/pdf" });
      const link = window.document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
      window.URL.revokeObjectURL(link.href);
    } catch (err) {
      console.error("Download error", err);
    }
  };

  const filteredDemandes = history?.demandes?.filter((d) => {
    if (filterStatus && d.status !== filterStatus) return false;
    if (filterType && d.documentTypeLibelle !== filterType) return false;
    return true;
  }) ?? [];

  const statusLabel = (status) => {
    const key = `dashboard.status.${status}`;
    const label = t(key);
    return label !== key ? label : status;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "—";
    const d = new Date(dateStr);
    return d.toLocaleDateString(dateLocale, {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (loading && !history) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="mb-4">
          <Link
            to="/dashboard"
            className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium"
          >
            <ArrowLeftIcon className="w-5 h-5" />
            {t("history.backToDashboard")}
          </Link>
        </div>
        <p className="text-gray-600">{t("history.loading")}</p>
      </div>
    );
  }

  if (error && !history) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="mb-4">
          <Link
            to="/dashboard"
            className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium"
          >
            <ArrowLeftIcon className="w-5 h-5" />
            {t("history.backToDashboard")}
          </Link>
        </div>
        <p className="text-red-600">{error}</p>
        <button
          type="button"
          onClick={fetchHistory}
          className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          {t("history.refresh")}
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="mb-4">
        <Link
          to="/dashboard"
          className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium"
        >
          <ArrowLeftIcon className="w-5 h-5" />
          {t("history.backToDashboard")}
        </Link>
      </div>
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{t("history.title")}</h1>
          <p className="text-gray-600 mt-1">{t("history.subtitle")}</p>
        </div>
        <div className="flex items-center gap-2">
          <Link
            to="/new-demande"
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
          >
            <PlusIcon className="w-5 h-5" />
            {t("history.createFirst")}
          </Link>
          <button
            type="button"
            onClick={fetchHistory}
            className="inline-flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            <ArrowPathIcon className="w-5 h-5" />
            {t("history.refresh")}
          </button>
        </div>
      </div>

      {/* Résumé */}
      <div className="bg-white rounded-lg shadow p-4 mb-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div>
          <p className="text-sm text-gray-500">{t("history.summary.user")}</p>
          <p className="font-medium">
            {history?.userFirstName} {history?.userLastName}
          </p>
          <p className="text-sm text-gray-600">{history?.userEmail}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500">{t("history.summary.totalDemandes")}</p>
          <p className="font-medium text-lg">{history?.totalDemandes ?? 0}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500">{t("history.summary.totalPaid")}</p>
          <p className="font-medium text-lg">{history?.totalPaidEuros ?? "0.00"} €</p>
        </div>
      </div>

      {/* Filtres */}
      <div className="flex flex-wrap gap-4 mb-4">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2 text-sm"
        >
          <option value="">{t("history.filters.allStatuses")}</option>
          <option value="PENDING_PAYMENT">{statusLabel("PENDING_PAYMENT")}</option>
          <option value="PENDING">{statusLabel("PENDING")}</option>
          <option value="APPROVED">{statusLabel("APPROVED")}</option>
          <option value="REJECTED">{statusLabel("REJECTED")}</option>
          <option value="COMPLETED">{statusLabel("COMPLETED")}</option>
        </select>
        <select
          value={filterType}
          onChange={(e) => setFilterType(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2 text-sm"
        >
          <option value="">{t("history.filters.allTypes")}</option>
          {[...new Set(history?.demandes?.map((d) => d.documentTypeLibelle).filter(Boolean) ?? [])].map((lib) => (
            <option key={lib} value={lib}>
              {lib}
            </option>
          ))}
        </select>
      </div>

      {/* Liste des demandes */}
      {filteredDemandes.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-8 text-center">
          <DocumentTextIcon className="w-12 h-12 mx-auto text-gray-400 mb-4" />
          <p className="text-gray-600 mb-4">{t("history.empty")}</p>
          <Link
            to="/new-demande"
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
          >
            <PlusIcon className="w-5 h-5" />
            {t("history.createFirst")}
          </Link>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t("history.table.id")}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t("history.table.documentType")}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t("history.table.createdAt")}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t("history.table.status")}</th>
                  <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t("history.table.amountPaid")}</th>
                  <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">{t("history.table.actions")}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredDemandes.map((d) => (
                  <tr key={d.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm text-gray-900">{d.id}</td>
                    <td className="px-4 py-3 text-sm text-gray-900">{d.documentTypeLibelle ?? "—"}</td>
                    <td className="px-4 py-3 text-sm text-gray-600">{formatDate(d.createdAt)}</td>
                    <td className="px-4 py-3">
                      <span className="inline-flex px-2 py-1 text-xs font-medium rounded bg-gray-100 text-gray-800">
                        {statusLabel(d.status)}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900">{d.totalPaidEuros ?? "0.00"} €</td>
                    <td className="px-4 py-3 text-right">
                      <button
                        type="button"
                        onClick={() => {
                          setSelectedDemande(d);
                          setShowDetailModal(true);
                        }}
                        className="text-indigo-600 hover:text-indigo-800 mr-3"
                        title={t("history.table.details")}
                      >
                        <EyeIcon className="w-5 h-5 inline" />
                      </button>
                      {d.documents?.length > 0 && (
                        <button
                          type="button"
                          onClick={() => handleDownload(d.id)}
                          className="text-indigo-600 hover:text-indigo-800"
                          title={t("history.table.downloadDocument")}
                        >
                          <DocumentArrowDownIcon className="w-5 h-5 inline" />
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal Détails */}
      {showDetailModal && selectedDemande && (
        <div className="fixed inset-0 z-50 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <div className="fixed inset-0 bg-black/50" onClick={() => setShowDetailModal(false)} aria-hidden="true" />
            <div className="relative bg-white rounded-lg shadow-xl max-w-2xl w-full p-6">
              <h2 className="text-lg font-semibold mb-4">{t("history.detail.title")} #{selectedDemande.id}</h2>
              <div className="space-y-4">
                <div>
                  <p className="text-sm text-gray-500">{t("history.detail.summary")}</p>
                  <p><span className="font-medium">{t("history.table.documentType")}:</span> {selectedDemande.documentTypeLibelle ?? "—"}</p>
                  <p><span className="font-medium">{t("history.table.status")}:</span> {statusLabel(selectedDemande.status)}</p>
                  <p><span className="font-medium">{t("history.table.createdAt")}:</span> {formatDate(selectedDemande.createdAt)}</p>
                  <p><span className="font-medium">{t("history.table.amountPaid")}:</span> {selectedDemande.totalPaidEuros ?? "0.00"} €</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-2">{t("history.detail.payments")}</p>
                  {selectedDemande.payments?.length ? (
                    <ul className="list-disc list-inside text-sm">
                      {selectedDemande.payments.map((p) => (
                        <li key={p.id}>
                          {p.amountEuros} € — {formatDate(p.paidAt)} ({p.status})
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm text-gray-500">{t("history.detail.noPayments")}</p>
                  )}
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-700 mb-2">{t("history.detail.documents")}</p>
                  {selectedDemande.documents?.length ? (
                    <ul className="list-disc list-inside text-sm">
                      {selectedDemande.documents.map((doc) => (
                        <li key={doc.id}>
                          {doc.fileName}
                          <button
                            type="button"
                            onClick={() => handleDownload(selectedDemande.id)}
                            className="ml-2 text-indigo-600 hover:underline"
                          >
                            {t("history.table.downloadDocument")}
                          </button>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm text-gray-500">{t("history.detail.noDocuments")}</p>
                  )}
                </div>
              </div>
              <div className="mt-6 flex justify-end gap-2">
                {selectedDemande.documents?.length > 0 && (
                  <button
                    type="button"
                    onClick={() => handleDownload(selectedDemande.id)}
                    className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700"
                  >
                    {t("history.table.downloadDocument")}
                  </button>
                )}
                <button
                  type="button"
                  onClick={() => setShowDetailModal(false)}
                  className="px-4 py-2 border border-gray-300 rounded hover:bg-gray-50"
                >
                  {t("common.close")}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CitizenHistoryPage;
