import React from "react";
import { useTranslation } from "react-i18next";
import ScrollReveal from "./ScrollReveal";

const LandingHowItWorks = ({ howItWorksData }) => {
  const { t } = useTranslation();

  return (
    <section id="comment-ca-marche" className="py-16 sm:py-24 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <ScrollReveal className="text-center mb-14">
          <h2 className="text-3xl font-bold text-gray-900">{t("landing.howItWorks.title")}</h2>
          <p className="mt-4 text-lg text-gray-700 max-w-2xl mx-auto">{t("landing.howItWorks.subtitle")}</p>
        </ScrollReveal>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {howItWorksData.map(({ step, titleKey, descKey }, index) => (
            <ScrollReveal key={step} delay={index * 80}>
              <div className="flex flex-col items-center text-center">
                <div className="h-12 w-12 rounded-full bg-primary-600 text-white flex items-center justify-center font-bold text-lg">
                  {step}
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

export default LandingHowItWorks;
