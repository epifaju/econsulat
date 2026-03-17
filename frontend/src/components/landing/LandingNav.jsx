import React, { useRef, useEffect } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { Bars3Icon, XMarkIcon, UserPlusIcon } from "@heroicons/react/24/outline";
import LanguageSwitcher from "../LanguageSwitcher";

const LandingNav = ({ navLinks, activeSectionId, scrollToSection, handleNavClick, isMenuOpen, setIsMenuOpen, focusRing }) => {
  const { t } = useTranslation();
  const hamburgerRef = useRef(null);
  const prevMenuOpenRef = useRef(isMenuOpen);

  useEffect(() => {
    if (prevMenuOpenRef.current && !isMenuOpen) {
      hamburgerRef.current?.focus();
    }
    prevMenuOpenRef.current = isMenuOpen;
  }, [isMenuOpen]);

  return (
    <>
      <header className="bg-white border-b border-gray-200 sticky top-0 z-50 pt-[env(safe-area-inset-top)]">
        <div className="max-w-7xl mx-auto px-3 sm:px-6 lg:px-8">
          <div className="flex justify-between h-14 min-h-[44px] sm:h-16 items-center gap-2 sm:gap-4">
            <div className="flex items-center gap-3 sm:gap-8 min-w-0 flex-1">
              <Link to="/" className={`flex items-center gap-2 rounded min-h-[44px] min-w-[44px] sm:min-w-0 flex-shrink-0 ${focusRing}`}>
                <img src="/images/logo.svg" alt="" className="h-9 w-9 rounded-lg flex-shrink-0" width="36" height="36" />
                <span className="text-lg sm:text-xl font-semibold text-gray-900 truncate">{t("common.appName")}</span>
              </Link>
              <nav className="hidden md:flex gap-6 items-center" aria-label="Navigation principale">
                {navLinks.map(({ id, labelKey }) => (
                  <button
                    key={id}
                    onClick={() => scrollToSection(id)}
                    className="min-h-[44px] min-w-[44px] flex items-center justify-center py-2 px-1 text-sm font-medium text-gray-600 hover:text-primary-600 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2 rounded"
                  >
                    {t(labelKey)}
                  </button>
                ))}
              </nav>
              <button
                ref={hamburgerRef}
                type="button"
                className="md:hidden min-h-[44px] min-w-[44px] flex items-center justify-center p-2 rounded-md text-gray-600 hover:text-primary-600 hover:bg-gray-100 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2"
                onClick={() => setIsMenuOpen((prev) => !prev)}
                aria-expanded={isMenuOpen}
                aria-controls="mobile-menu"
                aria-label={isMenuOpen ? t("landing.menu.close") : t("landing.menu.open")}
              >
                {isMenuOpen ? <XMarkIcon className="h-6 w-6" /> : <Bars3Icon className="h-6 w-6" />}
              </button>
            </div>
            <div className="flex items-center gap-1 sm:gap-3 flex-shrink-0">
              <LanguageSwitcher />
              <Link
                to="/aide"
                className={`text-sm font-medium text-gray-700 hover:text-primary-600 hidden sm:inline-flex sm:min-h-[44px] sm:items-center rounded ${focusRing}`}
              >
                {t("nav.aide")}
              </Link>
              <Link
                to="/login"
                className={`text-sm font-medium text-gray-700 hover:text-primary-600 hidden sm:inline-flex sm:min-h-[44px] sm:items-center rounded ${focusRing}`}
              >
                {t("common.login")}
              </Link>
              <Link
                to="/login"
                state={{ showRegister: true }}
                className={`inline-flex items-center justify-center gap-2 min-h-[44px] min-w-[44px] sm:min-w-0 px-3 sm:px-4 py-2 rounded-md text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 ${focusRing}`}
              >
                <UserPlusIcon className="h-4 w-4 flex-shrink-0" aria-hidden />
                <span className="hidden sm:inline">{t("common.createAccount")}</span>
              </Link>
            </div>
          </div>
        </div>
        <div
          id="mobile-menu"
          className={`md:hidden absolute top-full left-0 right-0 bg-white border-b border-gray-200 shadow-lg transition-all duration-200 ease-out ${
            isMenuOpen ? "opacity-100 visible max-h-[80vh]" : "opacity-0 invisible max-h-0 overflow-hidden pointer-events-none"
          }`}
          role="dialog"
          aria-label={t("landing.menu.title")}
        >
          <nav className="px-4 py-4 space-y-1" aria-label={t("landing.menu.title")}>
            {navLinks.map(({ id, labelKey }) => {
              const isActive = activeSectionId === id;
              return (
                <button
                  key={id}
                  type="button"
                  onClick={() => handleNavClick(id)}
                  className={`min-h-[44px] flex items-center w-full text-left px-4 py-3 rounded-md text-base font-medium focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2 ${
                    isActive ? "text-primary-600 bg-primary-50 font-semibold" : "text-gray-700 hover:text-primary-600 hover:bg-primary-50"
                  }`}
                  aria-current={isActive ? "true" : undefined}
                >
                  {t(labelKey)}
                </button>
              );
            })}
            <div className="border-t border-gray-200 mt-3 pt-3 space-y-1">
              <Link
                to="/aide"
                onClick={() => setIsMenuOpen(false)}
                className={`min-h-[44px] flex items-center px-4 py-3 rounded-md text-base font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50 ${focusRing}`}
              >
                {t("nav.aide")}
              </Link>
              <Link
                to="/login"
                onClick={() => setIsMenuOpen(false)}
                className={`min-h-[44px] flex items-center px-4 py-3 rounded-md text-base font-medium text-gray-700 hover:text-primary-600 hover:bg-primary-50 ${focusRing}`}
              >
                {t("common.login")}
              </Link>
              <Link
                to="/login"
                state={{ showRegister: true }}
                onClick={() => setIsMenuOpen(false)}
                className={`min-h-[44px] flex items-center px-4 py-3 rounded-md text-base font-medium text-primary-600 hover:bg-primary-50 ${focusRing}`}
              >
                {t("common.createAccount")}
              </Link>
            </div>
          </nav>
        </div>
      </header>
      {isMenuOpen && (
        <button
          type="button"
          className={`md:hidden fixed inset-0 bg-black/30 z-40 top-14 sm:top-16 ${focusRing}`}
          onClick={() => setIsMenuOpen(false)}
          aria-label={t("landing.menu.close")}
        />
      )}
    </>
  );
};

export default LandingNav;
