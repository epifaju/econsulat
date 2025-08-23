# Spécification Fonctionnelle - Notifications Automatiques par Email (eConsulat)

## Objectif

Mettre en place un systéme complet de **notifications automatiques par email** dans le projet **eConsulat**.  
Le but est d'informer les citoyens par email lorsque le **statut de leur demande** change.  
Le citoyen doit également pouvoir consulter toutes ses notifications dans son **Tableau de Bord Utilisateur**.

---

## Fonctionnalités à implémenter

### 1. Backend (Spring Boot)

- Ajouter la dépendance **Spring Boot Starter Mail**.
- Configurer un service `EmailNotificationService` pour gérer l'envoi d'emails.
- Dans `application.properties`, ajouter la configuration SMTP.
- Créer une méthode `sendNotificationEmail(User user, Demande demande)` pour envoyer un email contenant :
  - Prénom & Nom du citoyen
  - Numéro de la demande
  - Nouveau statut
  - Lien vers l'espace citoyen.
- Créer une table `notifications` pour stocker :
  - `id_notification`
  - `id_demande`
  - `id_utilisateur`
  - `email_destinataire`
  - `objet`
  - `contenu`
  - `statut`
  - `date_envoi`
- Chaque email envoyé doit être **archivé** dans cette table.

---

### 2. Frontend (React)

#### a) **Tableau de Bord Administrateur**

- Dans la page **Gestion des Demandes** :
  - Permettre de modifier le **statut** d'une demande.
  - Lorsqu'un statut change :
    - Mettre à jour la base.
    - Déclencher l'envoi automatique d'un email via l'API backend.
    - Afficher une **confirmation visuelle**.
  - Ajouter une colonne **"Dernière Notification"** pour afficher la date du dernier email envoyé.

#### b) **Tableau de Bord Utilisateur**

- Ajouter une section **"Mes Notifications"** affichant :
  - La liste des emails envoyés.
  - Pour chaque email : **objet, date d'envoi, statut**.
  - Un bouton **"Voir le contenu"** pour consulter l'email complet.
- Empécher l'accès aux notifications d'autres utilisateurs.

---

### 3. Statuts des Demandes

- `En attente`
- `Approuvé`
- `Rejeté`
- `Terminé`

---

### 4. Contenu de l'Email

**Objet :** `[eConsulat] Mise à jour de votre demande`

**Corps de l'email :**

```
Bonjour ${prenom} ${nom},

Nous vous informons que le statut de votre demande n°${id_demande} a été mis à jour :
Nouveau statut : ${statut}.

Pour plus d'informations, connectez-vous à votre espace citoyen :
https://mon-econsulat.com/espace-citoyen

Cordialement,
L'équipe eConsulat
```

---

---

### 5. Sécurité

- Seuls les rôles **admin** et **agent** peuvent déclencher l'envoi d'emails.
- Les citoyens ne peuvent consulter **que leurs propres notifications**.

---

### 6. Résultat Attendu

- Quand un **admin** ou **agent** modifie le statut d'une demande :
  1. Le citoyen reçoit automatiquement un email.
  2. L'email est enregistré dans la table `notifications`.
  3. Le citoyen peut consulter ses emails dans son **Tableau de Bord Utilisateur**.
  4. L'admin peut visualiser l'historique complet des notifications dans son interface.

---

### 7. Bonus

- Ajouter un indicateur **"Notification envoyée"** dans la page Gestion des Demandes.
- Ajouter une **pagination** dans la page "Mes Notifications".
- Permettre à l'admin de **renvoyer** une notification manuellement.

---
