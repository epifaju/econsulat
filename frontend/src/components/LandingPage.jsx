import React from "react";
import { Link } from "react-router-dom";
import {
  DocumentTextIcon,
  CreditCardIcon,
  ChartBarIcon,
  ArrowDownTrayIcon,
  ShieldCheckIcon,
  UserPlusIcon,
  ArrowRightIcon,
} from "@heroicons/react/24/outline";

const LandingPage = () => {
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
                  eConsulat
                </span>
              </Link>
              <nav className="hidden md:flex gap-6">
                <button
                  onClick={() => scrollToSection("fonctionnalites")}
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  Fonctionnalités
                </button>
                <button
                  onClick={() => scrollToSection("comment-ca-marche")}
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  Comment ça marche
                </button>
              </nav>
            </div>
            <div className="flex items-center gap-3">
              <Link
                to="/login"
                className="text-sm font-medium text-gray-700 hover:text-primary-600"
              >
                Se connecter
              </Link>
              <Link
                to="/login"
                state={{ showRegister: true }}
                className="inline-flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium text-white bg-primary-600 hover:bg-primary-700"
              >
                <UserPlusIcon className="h-4 w-4" />
                Créer un compte
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
              Vos démarches consulat en ligne, simplifiées
            </h1>
            <p className="mt-6 text-lg text-gray-600">
              Demandez vos documents officiels (passeport, actes d'état civil…),
              payez en ligne de manière sécurisée et suivez l'avancement de vos
              demandes en temps réel.
            </p>
            <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
              <Link
                to="/login"
                className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-md text-base font-medium text-white bg-primary-600 hover:bg-primary-700"
              >
                Se connecter
                <ArrowRightIcon className="h-5 w-5" />
              </Link>
              <button
                type="button"
                onClick={() => scrollToSection("comment-ca-marche")}
                className="inline-flex items-center justify-center px-6 py-3 rounded-md text-base font-medium text-primary-600 bg-white border border-primary-200 hover:bg-primary-50"
              >
                Comment ça marche
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Fonctionnalités */}
      <section
        id="fonctionnalites"
        className="py-16 sm:py-24 bg-white"
      >
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-bold text-gray-900">
              Un service complet et sécurisé
            </h2>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              De la création de votre demande jusqu'à la récupération de votre
              document, tout se fait en ligne.
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <DocumentTextIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                Création de demandes
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                Remplissez vos formulaires en ligne pour les types de documents
                proposés par le consulat.
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <CreditCardIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                Paiement en ligne
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                Paiement sécurisé par carte bancaire. Les frais sont indiqués
                clairement avant validation.
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <ChartBarIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                Suivi en temps réel
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                Consultez le statut de vos demandes à tout moment depuis votre
                espace personnel.
              </p>
            </div>
            <div className="rounded-xl border border-gray-200 p-6 bg-gray-50/50">
              <div className="h-12 w-12 rounded-lg bg-primary-100 flex items-center justify-center">
                <ArrowDownTrayIcon className="h-6 w-6 text-primary-600" />
              </div>
              <h3 className="mt-4 font-semibold text-gray-900">
                Documents à télécharger
              </h3>
              <p className="mt-2 text-sm text-gray-600">
                Une fois votre demande traitée, récupérez vos documents
                générés directement en ligne.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Comment ça marche */}
      <section
        id="comment-ca-marche"
        className="py-16 sm:py-24 bg-gray-50"
      >
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <h2 className="text-3xl font-bold text-gray-900">
              Comment ça marche
            </h2>
            <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
              Quatre étapes simples pour obtenir vos documents.
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
            {[
              {
                step: 1,
                title: "Connectez-vous",
                desc: "Accédez à votre espace avec vos identifiants.",
              },
              {
                step: 2,
                title: "Créez votre demande",
                desc: "Choisissez le type de document et remplissez le formulaire.",
              },
              {
                step: 3,
                title: "Payez en ligne",
                desc: "Réglez les frais de manière sécurisée par carte bancaire.",
              },
              {
                step: 4,
                title: "Suivez et récupérez",
                desc: "Consultez le statut et téléchargez votre document une fois prêt.",
              },
            ].map((item) => (
              <div
                key={item.step}
                className="flex flex-col items-center text-center"
              >
                <div className="h-12 w-12 rounded-full bg-primary-600 text-white flex items-center justify-center font-bold text-lg">
                  {item.step}
                </div>
                <h3 className="mt-4 font-semibold text-gray-900">{item.title}</h3>
                <p className="mt-2 text-sm text-gray-600">{item.desc}</p>
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
              Un cadre officiel et sécurisé
            </h2>
            <p className="mt-4 text-gray-600 max-w-2xl mx-auto">
              eConsulat est le portail officiel pour vos démarches consulat. Vos
              données et vos paiements sont protégés. Les traitements sont
              effectués dans le respect des règles en vigueur.
            </p>
          </div>
        </div>
      </section>

      {/* CTA final */}
      <section className="py-16 sm:py-20 bg-gray-50">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-2xl font-bold text-gray-900">
            Prêt à commencer ?
          </h2>
          <p className="mt-4 text-gray-600">
            Connectez-vous à votre espace ou créez un compte pour déposer votre
            première demande.
          </p>
          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              to="/login"
              className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-md text-base font-medium text-white bg-primary-600 hover:bg-primary-700"
            >
              Se connecter
              <ArrowRightIcon className="h-5 w-5" />
            </Link>
            <Link
              to="/login"
              state={{ showRegister: true }}
              className="inline-flex items-center justify-center px-6 py-3 rounded-md text-base font-medium text-primary-600 bg-white border border-gray-300 hover:bg-gray-50"
            >
              Créer un compte
            </Link>
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
                eConsulat
              </span>
            </div>
            <div className="flex gap-6">
              <Link
                to="/login"
                className="text-sm text-gray-600 hover:text-primary-600"
              >
                Se connecter
              </Link>
            </div>
          </div>
          <p className="mt-6 text-center sm:text-left text-xs text-gray-500">
            © {new Date().getFullYear()} eConsulat. Tous droits réservés.
          </p>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
