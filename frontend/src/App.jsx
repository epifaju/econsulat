import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import Navbar from "./components/Navbar";
import Login from "./components/Login";
import AdminDashboard from "./components/AdminDashboard";
import UserDashboard from "./components/UserDashboard";
import UserManagement from "./components/UserManagement";
import CitizenForm from "./components/CitizenForm";
import EmailVerification from "./components/EmailVerification";
import PaymentSuccess from "./components/PaymentSuccess";
import PaymentCancel from "./components/PaymentCancel";
import Profile from "./components/Profile";
import LandingPage from "./components/LandingPage";
import ErrorBoundary from "./components/ErrorBoundary";

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
          <Route path="/verify-email" element={<EmailVerification />} />
          <Route path="/payment/success" element={<PaymentSuccess />} />
          <Route path="/payment/cancel" element={<PaymentCancel />} />
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
        <AppContent />
      </AuthProvider>
    </ErrorBoundary>
  );
};

export default App;
