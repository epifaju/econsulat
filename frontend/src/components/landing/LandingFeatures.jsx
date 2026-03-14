import React from "react";
import { useTranslation } from "react-i18next";
import ScrollReveal from "./ScrollReveal";

const LandingFeatures = ({ featuresData }) => {
  const { t } = useTranslation();

  return (
    <section id="fonctionnalites" className="pt-8 sm:pt-12 lg:pt-16 pb-16 sm:pb-24 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <ScrollReveal className="text-center mb-14">
          <h2 className="text-3xl font-bold text-gray-900">{t("landing.features.title")}</h2>
          <p className="mt-4 text-lg text-gray-700 max-w-2xl mx-auto">{t("landing.features.subtitle")}</p>
        </ScrollReveal>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {featuresData.map(({ Icon, titleKey, descKey }, index) => (
            <ScrollReveal key={titleKey} delay={index * 80}>
              <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
                <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                  <Icon className="h-6 w-6 text-primary-600" />
                </div>
                <h3 className="mt-4 font-semibold text-gray-900">{t(titleKey)}</h3>
                <p className="mt-2 text-sm text-gray-700">{t(descKey)}</p>
              </div>
            </ScrollReveal>
          ))}
        </div>
      </div>
    </section>
  );
};

export default LandingFeatures;
