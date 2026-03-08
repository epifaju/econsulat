import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import axios from "axios";

import fr from "./locales/fr.json";
import pt from "./locales/pt.json";

const supportedLngs = ["fr", "pt"];
const fallbackLng = "fr";

// Ne garder que fr ou pt comme langue active ; si le navigateur envoie autre chose, utiliser fr
const languageDetectorOptions = {
  order: ["localStorage", "navigator"],
  caches: ["localStorage"],
  lookupLocalStorage: "i18nextLng",
  checkWhitelist: true,
};

i18n.use(LanguageDetector).use(initReactI18next).init({
  resources: {
    fr: { translation: fr },
    pt: { translation: pt },
  },
  supportedLngs,
  fallbackLng,
  detection: languageDetectorOptions,
  interpolation: {
    escapeValue: false,
  },
  // Si la langue détectée n'est pas fr ni pt, forcer fr
  load: "languageOnly",
  react: {
    useSuspense: false,
  },
});

// Au premier chargement, si la langue n'est pas supportée (ex. navigateur en anglais), forcer fr
i18n.on("initialized", () => {
  const currentLng = i18n.language?.split("-")[0];
  if (currentLng && !supportedLngs.includes(currentLng)) {
    i18n.changeLanguage(fallbackLng);
  }
});

// Envoyer la langue courante à l'API à chaque requête
const setAcceptLanguage = (lng) => {
  const code = (lng || i18n.language || fallbackLng).split("-")[0];
  const lang = supportedLngs.includes(code) ? code : fallbackLng;
  if (typeof axios !== "undefined" && axios.defaults?.headers?.common) {
    axios.defaults.headers.common["Accept-Language"] = lang;
  }
};

setAcceptLanguage(i18n.language);
i18n.on("languageChanged", setAcceptLanguage);

export default i18n;
