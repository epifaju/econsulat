import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { ChevronDownIcon } from "@heroicons/react/24/outline";
import ScrollReveal from "./ScrollReveal";

const LandingFAQ = ({ faqData, focusRing }) => {
  const { t } = useTranslation();
  const [openId, setOpenId] = useState(null);

  return (
    <section id="faq" className="py-16 sm:py-24 bg-gray-50">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <ScrollReveal className="text-center mb-12">
          <h2 className="text-3xl font-bold text-gray-900">{t("landing.faq.title")}</h2>
          <p className="mt-4 text-lg text-gray-700">{t("landing.faq.subtitle")}</p>
        </ScrollReveal>
        <div className="space-y-2">
          {faqData.map(({ id, questionKey, answerKey }, index) => {
            const isOpen = openId === id;
            return (
              <ScrollReveal key={id} delay={index * 50}>
                <div className="rounded-xl border border-gray-200 bg-white overflow-hidden">
                  <button
                    type="button"
                    onClick={() => setOpenId(isOpen ? null : id)}
                    className={`w-full flex items-center justify-between gap-4 px-5 py-4 text-left text-sm font-medium text-gray-900 hover:bg-gray-50 focus:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary-500 ${focusRing}`}
                    aria-expanded={isOpen}
                    aria-controls={`faq-answer-${id}`}
                    id={`faq-question-${id}`}
                  >
                    <span>{t(questionKey)}</span>
                    <ChevronDownIcon
                      className={`h-5 w-5 flex-shrink-0 text-gray-500 transition-transform duration-200 ${isOpen ? "rotate-180" : ""}`}
                      aria-hidden
                    />
                  </button>
                  <div
                    id={`faq-answer-${id}`}
                    role="region"
                    aria-labelledby={`faq-question-${id}`}
                    className={`grid transition-[grid-template-rows] duration-200 ease-out ${isOpen ? "grid-rows-[1fr]" : "grid-rows-[0fr]"}`}
                  >
                    <div className="overflow-hidden">
                      <p className="px-5 pb-4 pt-0 text-gray-700 border-t border-gray-100">{t(answerKey)}</p>
                    </div>
                  </div>
                </div>
              </ScrollReveal>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default LandingFAQ;
