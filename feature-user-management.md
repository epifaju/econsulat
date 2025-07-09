# Fonctionnalité : Gestion des utilisateurs avec validation e-mail et rôles

## 🎯 Objectif
Mettre en place un système de gestion des comptes dans l'application eConsulat :

- Seuls les **administrateurs connectés** peuvent créer de nouveaux utilisateurs.
- Chaque utilisateur reçoit un **e-mail de validation** avec un lien d’activation unique.
- Tant que l’utilisateur n’a pas cliqué sur ce lien, il ne peut pas se connecter.
- Authentification sécurisée via JWT.

---

## 👤 Rôles disponibles
- **admin** : peut créer tous types d’utilisateurs
- **agent** : accès limité aux données citoyennes
- **user** : citoyen / usager

---

## 🧩 Fonctionnalités demandées

### 1. Création de compte (par admin uniquement)
- Route `/api/users` accessible uniquement par les administrateurs connectés
- Champs : prénom, nom, email, rôle (`user`, `admin`, `agent`), mot de passe
- Envoi d’un e-mail avec un **token de confirmation**

### 2. Validation de l’e-mail
- Route `/api/auth/confirm?token=...`
- Le token confirme l’e-mail et active le compte (`emailVerified = true`)
- Le token expire après 24h

### 3. Connexion (Login)
- Route `/api/auth/login`
- JWT délivré uniquement si l’adresse e-mail a été validée

### 4. Sécurité
- Authentification par JWT
- Hash des mots de passe avec Bcrypt
- Middleware ou Filter qui empêche l’accès aux routes protégées si le JWT est invalide

---

## 📦 Backend : Spring Boot
- Entité `User` :
  - `id`, `firstName`, `lastName`, `email`, `password`, `role`, `emailVerified`, `createdAt`
- Service pour création, envoi de mail, validation token
- Token JWT pour les utilisateurs connectés
- Filtrage des rôles (seuls les admins peuvent créer un utilisateur)

---

## 💻 Frontend : React
- Interface d’administration :
  - Formulaire pour créer un utilisateur
  - Gestion des rôles
  - Liste des utilisateurs (facultatif)
- Interface login simple
- Page de confirmation de l’e-mail

---

## 🔧 Fonctionnement des rôles
- Le rôle est sélectionné dans un menu déroulant (admin uniquement)
- Un middleware empêche les accès aux zones protégées selon le rôle

---

## 🛠️ Outils
- Backend : Spring Boot + Spring Security + JWT
- Base de données : PostgreSQL
- Frontend : React + Axios
- Envoi de mail : JavaMailSender (SMTP)
