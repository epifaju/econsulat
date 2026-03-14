import React from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {
  DocumentTextIcon,
  CreditCardIcon,
  ChartBarIcon,
  ArrowDownTrayIcon,
  ShieldCheckIcon,
  UserPlusIcon,
  ArrowRightIcon,
  MapPinIcon,
  PhoneIcon,
  EnvelopeIcon,
  ClockIcon,
} from "@heroicons/react/24/outline";
import LanguageSwitcher from "./LanguageSwitcher";

const LandingPage = () => {
  const { t } = useTranslation();
  const scrollToSection = (id) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <div className="min-h-screen bg-white">
      {/* Navbar publique */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center gap-8">
              <Link to="/" className="flex items-center gap-2">
                <div className="h-9 w-9 bg-primary-600 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-sm">EC</span>
                </div>
                <span className="text-xl font-semibold text-gray-900">
                  {t("common.appName")}
                </span>
              </Link>
              <nav className="hidden md:flex gap-6">
                <button
                  onClick={() => scrollToSection("fonctionnalites")}
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  {t("landing.nav.features")}
                </button>
                <button
                  onClick={() => scrollToSection("comment-ca-marche")}
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  {t("landing.nav.howItWorks")}
                </button>
                <button
                  onClick={() => scrollToSection("contact")}
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  {t("landing.nav.contact")}
                </button>
              </nav>
            </div>
            <div className="flex items-center gap-3">
              <LanguageSwitcher />
              <Link
                to="/login"
                className="text-sm font-medium text-gray-700 hover:text-primary-600"
              >
                {t("common.login")}
              </Link>
              <Link
                to="/login"
                state={{ showRegister: true }}
                className="inline-flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium text-white bg-primary-600 hover:bg-primary-700"
              >
                <UserPlusIcon className="h-4 w-4" />
                {t("common.createAccount")}
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Hero */}
      <section className="relative bg-gradient-to-b from-primary-50 to-white py-20 sm:py-28">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto">
            <h1 className="text-4xl sm:text-5xl font-bold text-gray-900 tracking-tight">
              {t("landing.hero.title")}
            </h1>
            <p className="mt-6 text-lg text-gray-600">
              {t("landing.hero.subtitle")}
            </p>
            <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                to="/login"
                className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-md text-base font-medium text-white bg-primary-600 hover:bg-primary-700"
              >
                {t("landing.hero.ctaLogin")}
                <ArrowRightIcon className="h-5 w-5" />
              </Link>
              <button
                type="button"
                onClick={() => scrollToSection("comment-ca-marche")}
                className="inline-flex items-center justify-center px-6 py-3 rounded-md text-base font-medium text-primary-600 bg-white border border-primary-200 hover:bg-primary-50"
              >
                {t("landing.hero.ctaHowItWorks")}
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Fonctionnalités */}
      <section id="fonctionnalites" className="py-16 sm:py-24 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-bold text-gray-900">
              {t("landing.features.title")}
            </h2>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              {t("landing.features.subtitle")}
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <DocumentTextIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.features.createRequests")}
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                {t("landing.features.createRequestsDesc")}
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <CreditCardIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.features.onlinePayment")}
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                {t("landing.features.onlinePaymentDesc")}
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <ChartBarIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.features.realtimeTracking")}
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                {t("landing.features.realtimeTrackingDesc")}
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <ArrowDownTrayIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.features.downloadDocs")}
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                {t("landing.features.downloadDocsDesc")}
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Comment ça marche */}
      <section id="comment-ca-marche" className="py-16 sm:py-24 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-bold text-gray-900">
              {t("landing.howItWorks.title")}
            </h2>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              {t("landing.howItWorks.subtitle")}
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {[
              {
                step: 1,
                titleKey: "landing.howItWorks.step1Title",
                descKey: "landing.howItWorks.step1Desc",
              },
              {
                step: 2,
                titleKey: "landing.howItWorks.step2Title",
                descKey: "landing.howItWorks.step2Desc",
              },
              {
                step: 3,
                titleKey: "landing.howItWorks.step3Title",
                descKey: "landing.howItWorks.step3Desc",
              },
              {
                step: 4,
                titleKey: "landing.howItWorks.step4Title",
                descKey: "landing.howItWorks.step4Desc",
              },
            ].map((item) => (
              <div
                key={item.step}
                className="flex flex-col items-center text-center"
              >
                <div className="h-12 w-12 rounded-full bg-primary-600 text-white flex items-center justify-center font-bold text-lg">
                  {item.step}
                </div>
                <h3 className="mt-4 font-semibold text-gray-900">
                  {t(item.titleKey)}
                </h3>
                <p className="mt-2 text-sm text-gray-600">{t(item.descKey)}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Confiance */}
      <section className="py-16 sm:py-24 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="rounded-2xl bg-primary-50 border border-primary-100 p-8 sm:p-12 text-center">
            <div className="inline-flex h-14 w-14 rounded-full bg-primary-100 items-center justify-center">
              <ShieldCheckIcon className="h-7 w-7 text-primary-600" />
            </div>
            <h2 className="mt-6 text-2xl font-bold text-gray-900">
              {t("landing.trust.title")}
            </h2>
            <p className="mt-4 text-gray-600 max-w-2xl mx-auto">
              {t("landing.trust.body")}
            </p>
          </div>
        </div>
      </section>

      {/* CTA final */}
      <section className="py-16 sm:py-20 bg-gray-50">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-2xl font-bold text-gray-900">
            {t("landing.cta.title")}
          </h2>
          <p className="mt-4 text-gray-600">
            {t("landing.cta.subtitle")}
          </p>
          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/login"
              className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-md text-base font-medium text-white bg-primary-600 hover:bg-primary-700"
            >
              {t("landing.cta.login")}
              <ArrowRightIcon className="h-5 w-5" />
            </Link>
            <Link
              to="/login"
              state={{ showRegister: true }}
              className="inline-flex items-center justify-center px-6 py-3 rounded-md text-base font-medium text-primary-600 bg-white border border-gray-300 hover:bg-gray-50"
            >
              {t("landing.cta.createAccount")}
            </Link>
          </div>
        </div>
      </section>

      {/* Contact */}
      <section id="contact" className="py-16 sm:py-24 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-bold text-gray-900">
              {t("landing.contact.title")}
            </h2>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              {t("landing.contact.subtitle")}
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <MapPinIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.contact.name")}
              </h3>
              <p className="mt-2 text-sm text-gray-600 whitespace-pre-line">
                {t("landing.contact.address")}
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <PhoneIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.contact.phoneLabel")}
              </h3>
              <a
                href="tel:+33155531122"
                className="mt-2 text-sm text-gray-600 hover:text-primary-600 block"
              >
                {t("landing.contact.phone")}
              </a>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <EnvelopeIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.contact.emailLabel")}
              </h3>
              <a
                href="mailto:contact@econsulat.com"
                className="mt-2 text-sm text-gray-600 hover:text-primary-600 block break-all"
              >
                {t("landing.contact.email")}
              </a>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <ClockIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                {t("landing.contact.hoursLabel")}
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                {t("landing.contact.hours")}
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col sm:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-2">
              <div className="h-8 w-8 bg-primary-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-xs">EC</span>
              </div>
              <span className="text-sm font-medium text-gray-700">
                {t("common.appName")}
              </span>
            </div>
            <div className="flex gap-6">
              <Link
                to="/login"
                className="text-sm text-gray-600 hover:text-primary-600"
              >
                {t("landing.footer.login")}
              </Link>
            </div>
          </div>
          <p className="mt-6 text-center sm:text-left text-xs text-gray-500">
            {t("common.allRightsReserved", { year: new Date().getFullYear() })}
          </p>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
