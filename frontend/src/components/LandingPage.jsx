import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import {
  DocumentTextIcon,
  CreditCardIcon,
  ChartBarIcon,
  ArrowDownTrayIcon,
  MapPinIcon,
  PhoneIcon,
  EnvelopeIcon,
  ClockIcon,
} from "@heroicons/react/24/outline";
import LandingNav from "./landing/LandingNav";
import LandingHero from "./landing/LandingHero";
import LandingFeatures from "./landing/LandingFeatures";
import LandingHowItWorks from "./landing/LandingHowItWorks";
import LandingTrust from "./landing/LandingTrust";
import LandingCta from "./landing/LandingCta";
import LandingContact from "./landing/LandingContact";
import LandingFAQ from "./landing/LandingFAQ";
import LandingFooter from "./landing/LandingFooter";

const SECTION_IDS = ["fonctionnalites", "comment-ca-marche", "cta", "contact", "faq"];

const LandingPage = () => {
  const { t } = useTranslation();
  const { pathname, hash } = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [activeSectionId, setActiveSectionId] = useState(null);
  const observerRef = useRef(null);

  /** Au chargement ou changement de hash (ex. /#contact), scroll vers la section après rendu. */
  useEffect(() => {
    if (pathname !== "/" || !hash) return;
    const id = hash.slice(1);
    if (!id) return;
    const timer = setTimeout(() => {
      const el = document.getElementById(id);
      if (el) el.scrollIntoView({ behavior: "smooth" });
    }, 100);
    return () => clearTimeout(timer);
  }, [pathname, hash]);

  /** Scroll vers une section par id. Aucun effet si l’élément n’existe pas (ex. page partielle). */
  const scrollToSection = (id) => {
    try {
      const el = document.getElementById(id);
      if (el) el.scrollIntoView({ behavior: "smooth" });
    } catch (_) {
      // scrollIntoView peut échouer dans certains contextes (iframe, prérendu) ; on ignore.
    }
  };

  const handleNavClick = (id) => {
    setIsMenuOpen(false);
    // Sur mobile, déclencher le scroll après fermeture du menu pour éviter que overflow:hidden sur body n’annule scrollIntoView.
    const scrollAfterClose = () => {
      const el = document.getElementById(id);
      if (el) el.scrollIntoView({ behavior: "smooth", block: "start" });
    };
    requestAnimationFrame(() => {
      requestAnimationFrame(scrollAfterClose);
    });
  };

  useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === "Escape") setIsMenuOpen(false);
    };
    if (isMenuOpen) {
      document.addEventListener("keydown", handleEscape);
      document.body.style.overflow = "hidden";
    }
    return () => {
      document.removeEventListener("keydown", handleEscape);
      document.body.style.overflow = "";
    };
  }, [isMenuOpen]);

  /** Indicateur de section active au scroll (Intersection Observer). */
  useEffect(() => {
    const elements = SECTION_IDS.map((id) => document.getElementById(id)).filter(Boolean);
    if (elements.length === 0) return;

    const visible = new Set();
    const checkVisible = () => {
      const first = SECTION_IDS.find((id) => visible.has(id));
      setActiveSectionId(first || null);
    };

    observerRef.current = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          const id = entry.target.id;
          if (entry.isIntersecting) visible.add(id);
          else visible.delete(id);
        });
        checkVisible();
      },
      { rootMargin: "-15% 0px -60% 0px", threshold: 0 }
    );

    elements.forEach((el) => observerRef.current?.observe(el));
    return () => observerRef.current?.disconnect();
  }, []);

  const navLinks = [
    { id: "fonctionnalites", labelKey: "landing.nav.features" },
    { id: "comment-ca-marche", labelKey: "landing.nav.howItWorks" },
    { id: "faq", labelKey: "landing.nav.faq" },
    { id: "contact", labelKey: "landing.nav.contact" },
    { id: "cta", labelKey: "landing.nav.getStarted" },
  ];

  const featuresData = [
    { Icon: DocumentTextIcon, titleKey: "landing.features.createRequests", descKey: "landing.features.createRequestsDesc" },
    { Icon: CreditCardIcon, titleKey: "landing.features.onlinePayment", descKey: "landing.features.onlinePaymentDesc" },
    { Icon: ChartBarIcon, titleKey: "landing.features.realtimeTracking", descKey: "landing.features.realtimeTrackingDesc" },
    { Icon: ArrowDownTrayIcon, titleKey: "landing.features.downloadDocs", descKey: "landing.features.downloadDocsDesc" },
  ];

  const howItWorksData = [
    { step: 1, titleKey: "landing.howItWorks.step1Title", descKey: "landing.howItWorks.step1Desc" },
    { step: 2, titleKey: "landing.howItWorks.step2Title", descKey: "landing.howItWorks.step2Desc" },
    { step: 3, titleKey: "landing.howItWorks.step3Title", descKey: "landing.howItWorks.step3Desc" },
    { step: 4, titleKey: "landing.howItWorks.step4Title", descKey: "landing.howItWorks.step4Desc" },
  ];

  const contactData = [
    { Icon: MapPinIcon, titleKey: "landing.contact.name", contentKey: "landing.contact.address", type: "text", preLine: true },
    { Icon: PhoneIcon, titleKey: "landing.contact.phoneLabel", contentKey: "landing.contact.phone", type: "link", href: "tel:+33155531122" },
    { Icon: EnvelopeIcon, titleKey: "landing.contact.emailLabel", contentKey: "landing.contact.email", type: "link", href: "mailto:contact@econsulat.com", breakAll: true },
    { Icon: ClockIcon, titleKey: "landing.contact.hoursLabel", contentKey: "landing.contact.hours", type: "text" },
  ];

  const faqData = [
    { id: "faq1", questionKey: "landing.faq.q1", answerKey: "landing.faq.a1" },
    { id: "faq2", questionKey: "landing.faq.q2", answerKey: "landing.faq.a2" },
    { id: "faq3", questionKey: "landing.faq.q3", answerKey: "landing.faq.a3" },
    { id: "faq4", questionKey: "landing.faq.q4", answerKey: "landing.faq.a4" },
  ];

  const focusRing =
    "focus:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2";

  return (
    <div className="min-h-screen bg-white">
      <a
        href="#main-content"
        className="sr-only rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white focus:not-sr-only focus:fixed focus:left-4 focus:top-4 focus:z-[100] focus:inline-block focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-offset-2 focus-visible:ring-offset-primary-600"
      >
        {t("landing.a11y.skipToContent")}
      </a>
      <LandingNav
        navLinks={navLinks}
        activeSectionId={activeSectionId}
        scrollToSection={scrollToSection}
        handleNavClick={handleNavClick}
        isMenuOpen={isMenuOpen}
        setIsMenuOpen={setIsMenuOpen}
        focusRing={focusRing}
      />
      <LandingHero scrollToSection={scrollToSection} focusRing={focusRing} />
      <LandingFeatures featuresData={featuresData} />
      <LandingHowItWorks howItWorksData={howItWorksData} />
      <LandingTrust />
      <LandingCta focusRing={focusRing} />
      <LandingContact contactData={contactData} focusRing={focusRing} />
      <LandingFAQ faqData={faqData} focusRing={focusRing} />
      <LandingFooter scrollToSection={scrollToSection} focusRing={focusRing} />
    </div>
  );
};

export default LandingPage;
