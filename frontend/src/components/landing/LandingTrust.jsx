import React from "react";
import { useTranslation } from "react-i18next";
import { ShieldCheckIcon } from "@heroicons/react/24/outline";
import ScrollReveal from "./ScrollReveal";

const LandingTrust = () => {
  const { t } = useTranslation();

  return (
    <section className="py-16 sm:py-24 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <ScrollReveal>
          <div className="rounded-2xl bg-primary-50 border-2 border-primary-200 shadow-lg p-8 sm:p-12 text-center">
            <div className="inline-flex h-14 w-14 rounded-full bg-primary-100 items-center justify-center ring-4 ring-primary-100/80">
              <ShieldCheckIcon className="h-7 w-7 text-primary-600" />
            </div>
            <h2 className="mt-6 text-3xl font-bold text-gray-900">{t("landing.trust.title")}</h2>
            <p className="mt-4 text-gray-700 max-w-2xl mx-auto">{t("landing.trust.body")}</p>
          </div>
        </ScrollReveal>
      </div>
    </section>
  );
};

export default LandingTrust;
