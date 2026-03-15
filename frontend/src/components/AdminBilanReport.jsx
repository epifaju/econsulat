import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {
  ArrowPathIcon,
  DocumentArrowDownIcon,
  TableCellsIcon,
} from "@heroicons/react/24/outline";
import API_CONFIG, { buildApiUrl, buildApiUrlWithParams } from "../config/api";

const AdminBilanReport = ({ token }) => {
  const { t, i18n } = useTranslation();
  const [groupBy, setGroupBy] = useState("MONTH");
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [downloading, setDownloading] = useState(null); // "pdf" | "excel" | null

  const fetchReport = async () => {
    if (!token) return;
    try {
      setLoading(true);
      setError(null);
      const params = { groupBy };
      if (dateFrom) params.dateFrom = dateFrom;
      if (dateTo) params.dateTo = dateTo;
      const url = buildApiUrlWithParams(API_CONFIG.ADMIN.BILAN, params);
      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Accept-Language": i18n.language || "fr",
        },
      });
      if (response.ok) {
        const data = await response.json();
        setReport(data);
      } else {
        setError(t("admin.bilan.loadError"));
      }
    } catch (err) {
      setError(t("admin.bilan.loadError"));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReport();
  }, []);

  const handleExport = async (format) => {
    if (!token) return;
    const params = { groupBy };
    if (dateFrom) params.dateFrom = dateFrom;
    if (dateTo) params.dateTo = dateTo;
    const endpoint =
      format === "pdf"
        ? API_CONFIG.ADMIN.BILAN_EXPORT_PDF
        : API_CONFIG.ADMIN.BILAN_EXPORT_EXCEL;
    const url = buildApiUrlWithParams(endpoint, params);
    try {
      setDownloading(format);
      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Accept-Language": i18n.language || "fr",
        },
      });
      if (!response.ok) throw new Error("Export failed");
      const blob = await response.blob();
      const disposition = response.headers.get("Content-Disposition");
      let filename = format === "pdf" ? "bilan-comptable.pdf" : "bilan-comptable.xlsx";
      if (disposition) {
        const match = disposition.match(/filename="?([^";\n]+)"?/);
        if (match) filename = match[1].trim();
      }
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = filename;
      link.click();
      window.URL.revokeObjectURL(link.href);
    } catch (err) {
      setError(t("admin.bilan.loadError"));
    } finally {
      setDownloading(null);
    }
  };

  return (
    <div className="p-6">
      <div className="mb-6">
        <h2 className="text-xl font-semibold text-gray-900">{t("admin.bilan.title")}</h2>
        <p className="mt-1 text-sm text-gray-600">{t("admin.bilan.subtitle")}</p>
      </div>

      {/* Filtres et actions */}
      <div className="flex flex-wrap items-end gap-4 mb-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {t("admin.bilan.groupBy")}
          </label>
          <select
            value={groupBy}
            onChange={(e) => setGroupBy(e.target.value)}
            className="rounded-md border border-gray-300 shadow-sm px-3 py-2 text-sm focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="DAY">{t("admin.bilan.groupByDay")}</option>
            <option value="MONTH">{t("admin.bilan.groupByMonth")}</option>
            <option value="YEAR">{t("admin.bilan.groupByYear")}</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {t("admin.bilan.dateFrom")}
          </label>
          <input
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.target.value)}
            className="rounded-md border border-gray-300 shadow-sm px-3 py-2 text-sm focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {t("admin.bilan.dateTo")}
          </label>
          <input
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.target.value)}
            className="rounded-md border border-gray-300 shadow-sm px-3 py-2 text-sm focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <button
          type="button"
          onClick={fetchReport}
          disabled={loading}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
        >
          <ArrowPathIcon
            className={`-ml-1 mr-2 h-5 w-5 ${loading ? "animate-spin" : ""}`}
          />
          {t("admin.bilan.refresh")}
        </button>
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => handleExport("pdf")}
            disabled={loading || downloading !== null}
            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            <DocumentArrowDownIcon className="-ml-1 mr-2 h-5 w-5" />
            {downloading === "pdf" ? "..." : t("admin.bilan.downloadPdf")}
          </button>
          <button
            type="button"
            onClick={() => handleExport("excel")}
            disabled={loading || downloading !== null}
            className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            <TableCellsIcon className="-ml-1 mr-2 h-5 w-5" />
            {downloading === "excel" ? "..." : t("admin.bilan.downloadExcel")}
          </button>
        </div>
      </div>

      {error && (
        <div className="mb-4 rounded-md bg-red-50 p-4 text-sm text-red-700">{error}</div>
      )}

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <ArrowPathIcon className="h-10 w-10 text-blue-500 animate-spin" />
        </div>
      ) : report ? (
        <>
          <div className="overflow-x-auto border border-gray-200 rounded-lg">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th
                    scope="col"
                    className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                  >
                    {t("admin.bilan.colPeriod")}
                  </th>
                  <th
                    scope="col"
                    className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                  >
                    {t("admin.bilan.colDocumentType")}
                  </th>
                  <th
                    scope="col"
                    className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"
                  >
                    {t("admin.bilan.colCount")}
                  </th>
                  <th
                    scope="col"
                    className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider"
                  >
                    {t("admin.bilan.colAmount")}
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {report.rows && report.rows.length > 0 ? (
                  report.rows.map((row, idx) => (
                    <tr key={idx}>
                      <td className="px-4 py-3 text-sm text-gray-900 whitespace-nowrap">
                        {row.periodLabel}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-900">
                        {row.documentTypeLibelle || "—"}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-900 text-right">
                        {row.count}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-900 text-right font-mono">
                        {row.amountEuros ?? "0.00"}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan={4}
                      className="px-4 py-8 text-center text-sm text-gray-500"
                    >
                      {t("admin.bilan.noData")}
                    </td>
                  </tr>
                )}
                {report.rows && report.rows.length > 0 && (
                  <tr className="bg-gray-50 font-semibold">
                    <td className="px-4 py-3 text-sm text-gray-900" colSpan={2}>
                      {t("admin.bilan.total")}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-900 text-right">—</td>
                    <td className="px-4 py-3 text-sm text-gray-900 text-right font-mono">
                      {report.totalAmountEuros ?? "0.00"}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          {report.generatedAt && (
            <p className="mt-3 text-xs text-gray-500">
              {t("admin.bilan.generatedOn")}{" "}
              {new Date(report.generatedAt).toLocaleString(i18n.language || "fr")}
            </p>
          )}
        </>
      ) : null}
    </div>
  );
};

export default AdminBilanReport;
