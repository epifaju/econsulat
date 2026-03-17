import React, { useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { ArrowLeftIcon, MagnifyingGlassIcon } from "@heroicons/react/24/outline";
import axios from "axios";
import API_CONFIG from "../../config/api";
import faqDataStatic from "../../data/faq.json";

const FaqView = () => {
  const { t } = useTranslation();
  const [search, setSearch] = useState("");
  const [selectedId, setSelectedId] = useState(null);
  const [faqData, setFaqData] = useState(faqDataStatic);

  useEffect(() => {
    axios
      .get(`${API_CONFIG.BASE_URL}${API_CONFIG.FAQ}`)
      .then((res) => {
        if (res.data?.categories?.length) {
          setFaqData({ categories: res.data.categories });
        }
      })
      .catch(() => {});
  }, []);

  const categoriesWithEntries = useMemo(() => {
    const categories = faqData.categories || [];
    if (categories.length && categories[0].entries) {
      return categories
        .sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
        .map((c) => ({ ...c, entries: (c.entries || []).sort((a, b) => (a.order ?? 0) - (b.order ?? 0)) }));
    }
    const byCategory = {};
    categories.sort((a, b) => (a.order ?? 0) - (b.order ?? 0)).forEach((cat) => {
      byCategory[cat.id] = { ...cat, entries: [] };
    });
    (faqData.entries || []).sort((a, b) => (a.order ?? 0) - (b.order ?? 0)).forEach((entry) => {
      if (byCategory[entry.categoryId]) {
        byCategory[entry.categoryId].entries.push(entry);
      }
    });
    return Object.values(byCategory);
  }, [faqData]);

  const filteredCategories = useMemo(() => {
    if (!search.trim()) return categoriesWithEntries;
    const q = search.trim().toLowerCase();
    return categoriesWithEntries
      .map((cat) => ({
        ...cat,
        entries: cat.entries.filter((entry) => {
          const question = t(entry.questionKey).toLowerCase();
          const answer = t(entry.answerKey).toLowerCase();
          const keywords = (entry.keywords || "").toLowerCase();
          return question.includes(q) || answer.includes(q) || keywords.includes(q);
        }),
      }))
      .filter((cat) => cat.entries.length > 0);
  }, [categoriesWithEntries, search, t]);

  const allEntries = useMemo(
    () => faqData.entries || faqData.categories?.flatMap((c) => c.entries || []) || [],
    [faqData]
  );
  const selectedEntry = useMemo(
    () => (selectedId ? allEntries.find((e) => String(e.id) === String(selectedId)) : null),
    [selectedId, allEntries]
  );

  if (selectedEntry) {
    return (
      <div className="max-w-3xl mx-auto">
        <button
          type="button"
          onClick={() => setSelectedId(null)}
          className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-medium mb-6"
        >
          <ArrowLeftIcon className="w-5 h-5" />
          {t("aide.faq.backToList")}
        </button>
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4">
            {t(selectedEntry.questionKey)}
          </h2>
          <div className="prose prose-gray max-w-none text-gray-700 whitespace-pre-wrap">
            {t(selectedEntry.answerKey)}
          </div>
          <div className="mt-6 pt-4 border-t">
            <Link
              to="/new-demande"
              className="inline-flex items-center px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
            >
              {t("aide.faq.createDemande")}
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto">
      <h2 className="text-xl font-bold text-gray-900 mb-4">{t("aide.faq.title")}</h2>
      <div className="relative mb-6">
        <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <input
          type="search"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder={t("aide.faq.searchPlaceholder")}
          className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
        />
      </div>
      <div className="space-y-8">
        {filteredCategories.length === 0 ? (
          <p className="text-gray-500">
            {search.trim()
              ? t("aide.faq.noResults")
              : t("common.loading")}
          </p>
        ) : (
          filteredCategories.map((cat) => (
            <section key={cat.id}>
              <h3 className="text-lg font-semibold text-gray-800 mb-3">
                {t(`aide.faq.category.${cat.id}`)}
              </h3>
              <ul className="space-y-2">
                {cat.entries.map((entry) => (
                  <li key={entry.id}>
                    <button
                      type="button"
                      onClick={() => setSelectedId(entry.id)}
                      className="block w-full text-left px-4 py-3 bg-white border border-gray-200 rounded-lg hover:border-indigo-300 hover:bg-indigo-50/50 transition"
                    >
                      <span className="font-medium text-gray-900">
                        {t(entry.questionKey)}
                      </span>
                    </button>
                  </li>
                ))}
              </ul>
            </section>
          ))
        )}
      </div>
    </div>
  );
};

export default FaqView;
