# 🛂 Projet eConsulat – Système de gestion administrative consulaire

**Version** : 1.0  
**Date** : 5 juillet 2025  
**Auteur** : [Ton Nom]

---

## 🎯 Objectif

Créer une application web nommée **eConsulat** permettant de **dématérialiser et gérer les demandes administratives consulaires** (actes de naissance, passeports, certificats, etc.) entre les citoyens et les agents d’un consulat ou d’une ambassade.

---

## 👥 Utilisateurs cibles

- **Citoyens / Usagers** :
  - Soumettent leurs demandes en ligne (acte de naissance, certificat, etc.)
  - Téléversent les documents justificatifs
  - Reçoivent une réponse ou un document PDF généré

- **Agents consulaires (Admins)** :
  - Consultent et traitent les demandes
  - Génèrent les actes PDF (ex : acte de naissance)
  - Téléversent ou délivrent les documents officiels

---

## 🔑 Fonctionnalités principales

### 🔐 Authentification (JWT)
- Authentification sécurisée avec JWT
- Deux rôles : `ADMIN`, `USER`
- Middleware de sécurité côté backend

### 👤 Portail Citoyen (ROLE: USER)
- Formulaire de demande d’acte (nom, prénom, naissance...)
- Upload fichier justificatif (PDF/image)
- Message de confirmation après soumission

### 🛠️ Portail Admin (ROLE: ADMIN)
- Dashboard de toutes les demandes
- Affichage des informations + fichier justificatif
- Traitement de la demande : validation / suppression
- Génération automatique d’un PDF
- Téléchargement du document PDF généré

### 📁 Gestion des fichiers
- Upload multipart/form-data
- Stockage local temporaire (`/uploads`)
- Fichiers accessibles depuis l’interface admin

### 🧾 PDF
- Génération de l’acte de naissance en PDF via iText/OpenPDF

### 🗄️ Base de données PostgreSQL
- Tables : `citizen`, `user`
- Script SQL fourni
- JPA / Hibernate utilisé pour la persistance

---

## 🧱 Architecture technique

| Côté | Stack |
|------|-------|
| Frontend | React.js (Vite), Axios, Bootstrap ou Tailwind |
| Backend | Spring Boot 3, Spring Security, JWT |
| Base de données | PostgreSQL |
| PDF | iText ou OpenPDF |
| Auth | JWT avec rôles |
| Déploiement | Docker + VPS (optionnel) |

---

## ✅ Critères d’acceptation

| ID | Fonctionnalité | Critère |
|----|----------------|---------|
| A1 | Authentification | Connexion sécurisée JWT selon rôle |
| U1 | Demande usager | Soumission avec fichier |
| U2 | Confirmation | Message de succès visible |
| A2 | Dashboard admin | Liste et traitement des demandes |
| A3 | PDF | Génération correcte du PDF |
| F1 | Fichier | Fichier consultable par l’admin |

---

## 🧪 Livrables attendus

- Code backend complet (Spring Boot, JWT, PostgreSQL)
- Frontend React.js fonctionnel (formulaires, dashboard)
- Génération automatique de PDF
- Script `.sql` de base PostgreSQL
- Manuel de lancement local (README)

---

## ⬆️ Comment l’utiliser avec Cursor ?

1. Ouvre [https://www.cursor.sh](https://www.cursor.sh)
2. Clique sur `+ New Workspace` ou `Upload`
3. Glisse ce fichier `.md` ou clique sur “Upload File” et choisis ce fichier
4. Demande à Cursor : **“Génère-moi une application complète basée sur ce PRD”**
5. Cursor générera automatiquement les fichiers backend, frontend et base de données

---

Fichier généré par ChatGPT le 5 juillet 2025 📄