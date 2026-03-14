import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ArrowRightIcon } from "@heroicons/react/24/outline";
import ScrollReveal from "./ScrollReveal";

const LandingCta = ({ focusRing }) => {
  const { t } = useTranslation();

  return (
    <section id="cta" className="py-16 sm:py-20 bg-gray-50">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <ScrollReveal>
          <h2 className="text-2xl font-bold text-gray-900">{t("landing.cta.title")}</h2>
          <p className="mt-4 text-gray-700">{t("landing.cta.subtitle")}</p>
          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/login"
              state={{ showRegister: true }}
              className={`inline-flex items-center justify-center gap-2 px-6 py-3 rounded-md text-base font-medium text-white bg-primary-600 hover:bg-primary-700 ${focusRing}`}
            >
              {t("landing.cta.createAccount")}
              <ArrowRightIcon className="h-5 w-5" />
            </Link>
            <Link
              to="/login"
              className={`inline-flex items-center justify-center px-6 py-3 rounded-md text-base font-medium text-primary-600 bg-white border border-gray-300 hover:bg-gray-50 ${focusRing}`}
            >
              {t("landing.cta.login")}
            </Link>
          </div>
        </ScrollReveal>
      </div>
    </section>
  );
};

export default LandingCta;
