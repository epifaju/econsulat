# Feature: Interface Administrateur Complète — Projet eConsulat

## 📅 Date
2025-07-09

## 🎯 Objectif
Mettre en place une interface d'administration complète permettant à l'administrateur de gérer les demandes, les utilisateurs, les types de documents, et de générer des documents officiels avec filigrane pour garantir leur authenticité.

---

## 🧩 Fonctionnalités à implémenter

### 🔍 Gestion des Demandes
- Afficher toutes les demandes dans un tableau avec :
  - Pagination (5 par page)
  - Tri par date, statut, ou type
  - Barre de recherche par nom ou identifiant
- Consulter les détails complets d’une demande
- Changer le statut d’une demande (Ex. : `En cours`, `Validée`, `Refusée`)
- Voir et télécharger les pièces jointes liées à chaque demande
- Bouton "Générer Document" pour générer :
  - Acte de naissance
  - Passeport
  - Certificat de mariage
  - Etc.

### 🧾 Génération de Documents Officiels
- Utiliser un template Word (`.docx`) pour chaque type de document
- Remplir dynamiquement les champs du document à partir des données de la demande
- Convertir le document en PDF
- Ajouter un **filigrane visible** : `"Document émis par le eConsulat - République de Guinée-Bissau"`
- Stocker le document généré dans le dossier du dossier de l'usager et rendre le fichier téléchargeable

---

### 👤 Gestion des Utilisateurs
- Afficher la liste de tous les utilisateurs avec leurs rôles
- Créer un nouvel utilisateur avec :
  - Nom
  - Email
  - Mot de passe (crypté)
  - Rôle (admin, agent, usager)
- Modifier les informations ou rôle d’un utilisateur
- Supprimer un utilisateur

---

### 🔐 Gestion des Rôles
- Ajouter, modifier ou supprimer des rôles (admin, agent, usager)
- Associer des permissions spécifiques par rôle (lecture, écriture, génération document, etc.)

---

### 📄 Gestion des Types de Documents
- Ajouter de nouveaux types de documents (ex : certificat d'hébergement)
- Associer chaque type à un **template Word**
- Modifier ou supprimer un type de document

---

## ✅ Contraintes techniques
- Backend : Spring Boot (Java)
- Frontend : React.js
- Base de données : PostgreSQL
- Authentification : JWT
- Les fichiers uploadés doivent être gérés via Multipart et stockés avec un lien dans la base
- Tous les documents générés doivent être traçables dans la base (champ `documentType`, `createdAt`, `createdBy`)

---

## 📁 Nom du projet
`eConsulat`
