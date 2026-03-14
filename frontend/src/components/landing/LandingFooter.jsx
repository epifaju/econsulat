import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const LandingFooter = ({ scrollToSection, focusRing }) => {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const isLanding = pathname === "/";

  return (
    <footer className="bg-white border-t border-gray-200 py-8 pb-[max(2rem,env(safe-area-inset-bottom))]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row justify-between items-center gap-6">
          <div className="flex items-center gap-2">
            <img src="/images/logo.svg" alt="" className="h-8 w-8 rounded-lg flex-shrink-0" width="32" height="32" />
            <span className="text-sm font-medium text-gray-700">{t("common.appName")}</span>
          </div>
          <nav className="flex flex-col sm:flex-row flex-wrap justify-center sm:justify-end gap-2 sm:gap-6 items-center" aria-label="Liens du pied de page">
            {isLanding ? (
              <button
                type="button"
                onClick={() => scrollToSection("contact")}
                className={`min-h-[44px] inline-flex items-center px-4 text-sm font-medium text-gray-700 hover:text-primary-600 rounded ${focusRing}`}
              >
                {t("landing.footer.contact")}
              </button>
            ) : (
              <Link to="/#contact" className={`min-h-[44px] inline-flex items-center px-4 text-sm font-medium text-gray-700 hover:text-primary-600 rounded ${focusRing}`}>
                {t("landing.footer.contact")}
              </Link>
            )}
            <Link to="/mentions" className={`min-h-[44px] inline-flex items-center px-4 text-sm text-gray-700 hover:text-primary-600 rounded ${focusRing}`}>
              {t("landing.footer.legal")}
            </Link>
            <Link to="/confidentialite" className={`min-h-[44px] inline-flex items-center px-4 text-sm text-gray-700 hover:text-primary-600 rounded ${focusRing}`}>
              {t("landing.footer.privacy")}
            </Link>
            <Link to="/login" className={`min-h-[44px] inline-flex items-center px-4 text-sm text-gray-700 hover:text-primary-600 rounded ${focusRing}`}>
              {t("landing.footer.login")}
            </Link>
          </nav>
        </div>
        <p className="mt-6 text-center sm:text-left text-xs text-gray-500">
          {t("common.allRightsReserved", { year: new Date().getFullYear() })}
        </p>
      </div>
    </footer>
  );
};

export default LandingFooter;
