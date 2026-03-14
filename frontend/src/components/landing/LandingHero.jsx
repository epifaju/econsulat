import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ArrowRightIcon } from "@heroicons/react/24/outline";

const LandingHero = ({ scrollToSection, focusRing }) => {
  const { t } = useTranslation();

  return (
    <section id="main-content" className="relative bg-gradient-to-b from-primary-50 to-white pt-12 sm:pt-20 lg:pt-28 pb-8 sm:pb-12 lg:pb-16" tabIndex={-1}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-2 gap-6 lg:gap-12 items-center">
          <div className="text-center lg:text-left order-1">
            <h1 className="text-4xl sm:text-5xl font-bold text-gray-900 tracking-tight">
              {t("landing.hero.title")}
            </h1>
            <p className="mt-4 sm:mt-6 text-lg text-gray-700">{t("landing.hero.subtitle")}</p>
            <div className="mt-6 sm:mt-10 flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
              <Link
                to="/login"
                className={`inline-flex items-center justify-center gap-2 px-6 py-3 rounded-md text-base font-medium text-white bg-primary-600 hover:bg-primary-700 ${focusRing}`}
              >
                {t("landing.hero.ctaLogin")}
                <ArrowRightIcon className="h-5 w-5" />
              </Link>
              <button
                type="button"
                onClick={() => scrollToSection("comment-ca-marche")}
                className={`inline-flex items-center justify-center px-6 py-3 rounded-md text-base font-medium text-primary-600 bg-white border border-primary-200 hover:bg-primary-50 ${focusRing}`}
              >
                {t("landing.hero.ctaHowItWorks")}
              </button>
            </div>
            <p className="mt-4 sm:mt-6 text-sm text-gray-500" aria-hidden="true">
              {t("landing.hero.trustLine")}
            </p>
          </div>
          <div className="order-2 flex justify-center lg:justify-end">
            <img
              src="/images/hero-illustration.svg"
              alt={t("landing.hero.imageAlt")}
              className="w-full max-w-md lg:max-w-xl h-auto object-contain max-h-[180px] sm:max-h-none"
              width="400"
              height="320"
              fetchPriority="high"
            />
          </div>
        </div>
      </div>
    </section>
  );
};

export default LandingHero;
