# FAQ / assistant guidé « Quel document pour quel besoin ? » — Architecture et écrans

Document détaillant l’architecture et les écrans **sans code**, dans le même esprit que le document principal `services-integration-eConsulat.md`.

---

## 1. Objectif

- **Pour le citoyen** : Savoir quel type de document demander selon sa situation (mariage, naissance d’enfant, succession, voyage, administration locale, etc.) ; éviter les erreurs de choix et les demandes incomplètes.
- **Pour le consulat** : Réduire les appels et demandes mal orientées ; donner une information claire et homogène (FR/PT).

---

## 2. Deux modes d’usage

| Mode | Description | Usage typique |
|------|--------------|----------------|
| **FAQ** | Liste de questions fréquentes + réponses ; recherche par mot-clé ou par catégorie (type de document, thème). | « Je cherche une réponse précise » ; consultation rapide. |
| **Assistant guidé** | Parcours pas à pas par questions (arbre décisionnel) : « Quel est votre besoin ? » → propositions → « Vous avez besoin de… » → type de document + lien vers la demande. | « Je ne sais pas quel document choisir » ; orientation vers le bon formulaire. |

Les deux modes peuvent coexister sur la même zone (onglets ou entrée unique avec sous-pages).

---

## 3. Architecture globale

- **Contenu** : Piloté par des données (pas de logique métier lourde). Pas d’obligation d’authentification pour consulter la FAQ / l’assistant (accès possible depuis la landing et depuis l’espace citoyen).
- **Backend (optionnel en V1)** :  
  - Soit **contenu statique** : textes en i18n (clés FAQ + réponses) et structure de l’arbre (étapes, choix, type de document cible) dans des fichiers (JSON, YAML) ou en base (entités FaqEntry, AssistantStep, etc.).  
  - Soit **API légère** : `GET /api/faq` (liste catégories + entrées), `GET /api/faq/search?q=...`, `GET /api/assistant/steps` ou `GET /api/assistant/next?stepId=...&choice=...` pour l’arbre.  
- **Frontend** : Pages dédiées (route ex. `/faq`, `/assistant` ou `/aide`) ; composants FAQ (liste, détail, recherche) et Assistant (étapes, boutons de choix, récap + lien « Créer cette demande »).

---

## 4. Modèle de données (logique, si backend)

- **FaqCategory** : Identifiant, libellé i18n, ordre d’affichage ; lien vers des FaqEntry.
- **FaqEntry** : Question (i18n), réponse (i18n, éventuellement HTML), catégorie, mots-clés pour recherche, ordre.
- **AssistantStep** : Identifiant, question ou titre (i18n), type (choix unique, choix multiple, info seule) ; ordre.
- **AssistantChoice** : Pour chaque étape : libellé du choix (i18n), étape suivante (ou fin) ; si fin → type de document recommandé (référence DocumentType), texte de synthèse (i18n).
- **DocumentType** : Déjà existant (acte de naissance, certificat de mariage, etc.) ; l’assistant pointe vers un ou plusieurs types selon le parcours.

En V1 minimale, tout peut être en JSON/i18n côté front (sans tables) ; l’arbre et les entrées FAQ sont alors des constantes ou des fichiers chargés au build.

---

## 5. Écrans et parcours

### 5.1 Point d’entrée

- **Où** : Lien « FAQ » / « Aide » / « Quel document pour quel besoin ? » dans la navbar (landing + espace citoyen) et éventuellement dans le dashboard citoyen (encart ou lien).
- **Comportement** : Une seule page d’entrée avec deux onglets ou deux boutons : **« Consulter la FAQ »** et **« Trouver mon document (assistant) »**.

### 5.2 Écran FAQ

- **Vue liste** : Catégories (ex. Par type de document, Démarches courantes, Paiement et délais) ; sous chaque catégorie, liste de questions cliquables. Barre de recherche en haut (filtrage côté client ou via API).
- **Vue détail** : Clic sur une question → affichage de la réponse (texte ou HTML) ; bouton « Retour à la liste » ; pas de formulaire, lecture seule.
- **UX** : Accès sans connexion ; langue selon préférence navigateur ou sélecteur FR/PT ; mise en page lisible (titres, paragraphes, liens éventuels vers « Créer une demande » ou « Mon dossier » si connecté).

### 5.3 Écran Assistant guidé

- **Étape 1 – Besoin** : Question du type « Quel est votre besoin principal ? » avec boutons ou cartes (ex. « Me marier / Pacs », « Faire une démarche pour mon enfant », « Justifier mon identité ou ma résidence », « Autre démarche administrative »). Un seul choix.
- **Étapes suivantes** : Selon le choix, sous-questions (ex. « Souhaitez-vous un acte de naissance ou un certificat de mariage ? », « Pour vous ou pour un tiers ? ») jusqu’à aboutir à une **étape résultat**.
- **Étape résultat** : Texte de synthèse (« Pour votre situation, le document adapté est : Acte de naissance ») ; description courte ; **bouton principal « Créer une demande – Acte de naissance »** (lien vers `/new-demande` avec paramètre ou pré-sélection du type si l’app le permet) ; lien secondaire « Voir la FAQ sur ce document ».
- **Navigation** : Bouton « Retour » pour revenir à l’étape précédente ; indication « Étape 1 / 3 » ; possibilité de « Recommencer » depuis l’entrée.

### 5.4 Récapitulatif des écrans

| Écran | Contenu principal | Actions |
|-------|-------------------|--------|
| Entrée Aide / FAQ | Choix : FAQ ou Assistant | Clic « Consulter la FAQ » ou « Trouver mon document » |
| FAQ – Liste | Catégories + questions ; recherche | Clic question → détail |
| FAQ – Détail | Question + réponse | Retour liste ; lien « Créer une demande » si pertinent |
| Assistant – Étapes | Question + choix (boutons/cartes) | Sélection → étape suivante ; Retour ; Recommencer |
| Assistant – Résultat | Type de document recommandé + résumé | Bouton « Créer une demande » (lien formulaire) ; lien FAQ |

---

## 6. Contenu type (exemples)

- **FAQ** : « Quel document pour un mariage au Portugal ? », « Acte de naissance intégral ou extrait ? », « Délai de délivrance », « Paiement par carte : quelles sont les options ? », « Que faire si ma demande est rejetée ? ».
- **Assistant** : Besoins → Mariage / Enfant / Identité–résidence / Succession / Autre ; sous « Mariage » → Certificat de mariage ou Acte de naissance ; sous « Enfant » → Acte de naissance (avec précision intégral/extrait si applicable). Chaque feuille de l’arbre renvoie à un DocumentType existant et à la route de création de demande.

---

## 7. Intégration avec l’existant

- **Types de documents** : Les types proposés à la fin de l’assistant sont ceux configurés dans l’application (DocumentType) ; le lien « Créer une demande » mène vers la page de nouvelle demande (route dédiée) en conservant, si possible, une pré-sélection du type (query param ou state).
- **Langue** : Utilisation des clés i18n (FR/PT) comme pour le reste de l’app ; pas de nouveau canal d’envoi d’email/SMS, uniquement consultation.
- **Hors connexion** : La FAQ et l’assistant restent accessibles sans compte ; le bouton « Créer une demande » redirige vers la page de demande (avec redirection vers login si nécessaire, selon le comportement actuel).

---

## 8. Ordre d’implémentation suggéré

1. **Contenu** : Rédiger les entrées FAQ et l’arbre de l’assistant (structure + libellés FR/PT) ; les mettre en JSON ou en i18n.
2. **Frontend – FAQ** : Page liste + détail + recherche (données en dur ou chargées depuis un JSON).
3. **Frontend – Assistant** : Parcours par étapes (état local : stepId, choix) ; écran résultat avec lien vers `/new-demande` (et paramètre type si l’API/formulaire le supporte).
4. **Point d’entrée** : Lien navbar + page d’entrée avec onglets FAQ / Assistant.
5. **Optionnel** : Déplacer le contenu en base + API si le consulat doit pouvoir modifier FAQ et arbre sans redéploiement.
