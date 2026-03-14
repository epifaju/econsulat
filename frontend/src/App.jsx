import React, { useEffect } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import Navbar from "./components/Navbar";
import CookieBanner from "./components/CookieBanner";
import Login from "./components/Login";
import ForgotPassword from "./components/ForgotPassword";
import ResetPassword from "./components/ResetPassword";
import AdminDashboard from "./components/AdminDashboard";
import UserDashboard from "./components/UserDashboard";
import UserManagement from "./components/UserManagement";
import CitizenForm from "./components/CitizenForm";
import EmailVerification from "./components/EmailVerification";
import PaymentSuccess from "./components/PaymentSuccess";
import PaymentCancel from "./components/PaymentCancel";
import Profile from "./components/Profile";
import LandingPage from "./components/LandingPage";
import LegalMentions from "./components/LegalMentions";
import PrivacyPolicy from "./components/PrivacyPolicy";
import ErrorBoundary from "./components/ErrorBoundary";

// Met à jour document.lang, title, meta description, Open Graph, Twitter Card et JSON-LD selon la langue
const SeoHead = () => {
  const { t, i18n } = useTranslation();

  useEffect(() => {
    const lang = (i18n.language || "fr").split("-")[0];
    const title = t("common.seo.title");
    const description = t("common.seo.description");
    const origin = typeof window !== "undefined" ? window.location.origin : "";
    const url = typeof window !== "undefined" ? window.location.href : origin;
    const ogImage = `${origin}/images/og-image.png`;

    document.documentElement.lang = lang;
    document.title = title;

    let canonical = document.querySelector('link[rel="canonical"]');
    if (!canonical) {
      canonical = document.createElement("link");
      canonical.setAttribute("rel", "canonical");
      document.head.appendChild(canonical);
    }
    canonical.setAttribute("href", url);

    const setMeta = (attr, key, value) => {
      let el = document.querySelector(`meta[${attr}="${key}"]`);
      if (!el) {
        el = document.createElement("meta");
        el.setAttribute(attr, key);
        document.head.appendChild(el);
      }
      el.setAttribute("content", value);
    };

    setMeta("name", "description", description);

    setMeta("property", "og:type", "website");
    setMeta("property", "og:title", title);
    setMeta("property", "og:description", description);
    setMeta("property", "og:image", ogImage);
    setMeta("property", "og:url", url);
    setMeta("property", "og:locale", lang === "fr" ? "fr_FR" : "pt_PT");

    setMeta("name", "twitter:card", "summary_large_image");
    setMeta("name", "twitter:title", title);
    setMeta("name", "twitter:description", description);
    setMeta("name", "twitter:image", ogImage);

    let scriptJsonLd = document.getElementById("seo-json-ld");
    const orgLd = {
      "@context": "https://schema.org",
      "@type": "Organization",
      "@id": `${origin}#organization`,
      name: "eConsulat MultiServices",
      url: origin,
      logo: `${origin}/images/logo.svg`,
      description: description,
    };
    if (!scriptJsonLd) {
      scriptJsonLd = document.createElement("script");
      scriptJsonLd.id = "seo-json-ld";
      scriptJsonLd.type = "application/ld+json";
      document.head.appendChild(scriptJsonLd);
    }
    scriptJsonLd.textContent = JSON.stringify(orgLd);

    let scriptWebSite = document.getElementById("seo-json-ld-website");
    const websiteLd = {
      "@context": "https://schema.org",
      "@type": "WebSite",
      name: "eConsulat",
      url: origin,
      description: description,
      publisher: { "@id": `${origin}#organization` },
      inLanguage: [lang === "fr" ? "fr" : "pt"],
    };
    if (!scriptWebSite) {
      scriptWebSite = document.createElement("script");
      scriptWebSite.id = "seo-json-ld-website";
      scriptWebSite.type = "application/ld+json";
      document.head.appendChild(scriptWebSite);
    }
    scriptWebSite.textContent = JSON.stringify(websiteLd);
  }, [i18n.language, t]);
};

// Composant de protection des routes
const ProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { user, isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

// Composant principal de l'application
const AppContent = () => {
  const { user, isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return (
      <>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/verify-email" element={<EmailVerification />} />
          <Route path="/payment/success" element={<PaymentSuccess />} />
          <Route path="/payment/cancel" element={<PaymentCancel />} />
          <Route path="/mentions" element={<LegalMentions />} />
          <Route path="/confidentialite" element={<PrivacyPolicy />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </>
    );
  }

  return (
    <>
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <main className="pt-16">
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/payment/success" element={<PaymentSuccess />} />
            <Route path="/payment/cancel" element={<PaymentCancel />} />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  {user?.role === "ADMIN" ? (
                    <AdminDashboard />
                  ) : (
                    <UserDashboard />
                  )}
                </ProtectedRoute>
              }
            />
            <Route
              path="/admin"
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/users"
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <UserManagement />
                </ProtectedRoute>
              }
            />
            <Route
              path="/citizen-form"
              element={
                <ProtectedRoute>
                  <CitizenForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              }
            />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </main>
      </div>
    </>
  );
};

// Composant racine avec le provider d'authentification et l'ErrorBoundary
const App = () => {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <SeoHead />
        <AppContent />
        <CookieBanner />
        <ToastContainer position="top-center" autoClose={4000} hideProgressBar={false} theme="light" />
      </AuthProvider>
    </ErrorBoundary>
  );
};

export default App;
