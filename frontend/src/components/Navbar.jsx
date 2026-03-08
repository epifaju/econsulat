import React, { useState } from "react";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "../contexts/AuthContext";
import {
  Bars3Icon,
  XMarkIcon,
  UserCircleIcon,
  ArrowRightOnRectangleIcon,
  Cog6ToothIcon,
} from "@heroicons/react/24/outline";
import LanguageSwitcher from "./LanguageSwitcher";

const Navbar = () => {
  const { t } = useTranslation();
  const { user, logout } = useAuth();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setIsUserMenuOpen(false);
  };

  const getRoleLabel = (role) => {
    if (!role) return "";
    const key = `nav.roles.${role}`;
    const label = t(key);
    return label !== key ? label : role;
  };

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo et titre */}
          <div className="flex items-center">
            <div className="flex-shrink-0 flex items-center">
              <div className="h-8 w-8 bg-primary-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-sm">EC</span>
              </div>
              <span className="ml-3 text-xl font-semibold text-gray-900">
                {t("common.appName")}
              </span>
            </div>
          </div>

          {/* Navigation desktop */}
          <div className="hidden md:flex md:items-center md:space-x-6">
            <LanguageSwitcher />
            <span className="text-sm text-gray-500">
              {t("nav.connectedAs")}{" "}
              <span className="font-medium text-gray-900">
                {user?.firstName} {user?.lastName}
              </span>
            </span>

            {/* Menu utilisateur */}
            <div className="relative">
              <button
                onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
                className="flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
              >
                <div className="h-8 w-8 rounded-full bg-primary-100 flex items-center justify-center">
                  <UserCircleIcon className="h-6 w-6 text-primary-600" />
                </div>
                <span className="ml-2 text-gray-700">{user?.email}</span>
              </button>

              {isUserMenuOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200">
                  <div className="px-4 py-2 text-sm text-gray-700 border-b border-gray-100">
                    <div className="font-medium">
                      {user?.firstName} {user?.lastName}
                    </div>
                    <div className="text-gray-500">{user?.email}</div>
                    <div className="mt-1">
                      <span className="badge badge-admin">
                        {getRoleLabel(user?.role)}
                      </span>
                    </div>
                  </div>

                  <Link
                    to="/profile"
                    onClick={() => setIsUserMenuOpen(false)}
                    className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  >
                    <Cog6ToothIcon className="h-4 w-4 mr-2" />
                    {t("nav.myProfile")}
                  </Link>

                  <button
                    onClick={handleLogout}
                    className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  >
                    <ArrowRightOnRectangleIcon className="h-4 w-4 mr-2" />
                    {t("nav.logout")}
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Bouton menu mobile */}
          <div className="md:hidden flex items-center">
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-primary-500"
            >
              <span className="sr-only">{t("nav.openMainMenu")}</span>
              {isMobileMenuOpen ? (
                <XMarkIcon className="block h-6 w-6" />
              ) : (
                <Bars3Icon className="block h-6 w-6" />
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Menu mobile */}
      {isMobileMenuOpen && (
        <div className="md:hidden">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3 bg-white border-t border-gray-200">
            <div className="px-3 py-2 text-sm text-gray-500">
              <div className="font-medium">
                {user?.firstName} {user?.lastName}
              </div>
              <div className="text-gray-400">{user?.email}</div>
              <div className="mt-1">
                <span className="badge badge-admin">
                  {getRoleLabel(user?.role)}
                </span>
              </div>
            </div>

            <Link
              to="/profile"
              onClick={() => setIsMobileMenuOpen(false)}
              className="flex items-center w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
            >
              <Cog6ToothIcon className="h-4 w-4 mr-2" />
              {t("nav.myProfile")}
            </Link>

            <button
              onClick={handleLogout}
              className="flex items-center w-full px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
            >
              <ArrowRightOnRectangleIcon className="h-4 w-4 mr-2" />
              {t("nav.logout")}
            </button>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
