# Services pouvant être intégrés à l'application eConsulat

Document de synthèse des échanges sur les types de services à intégrer, les courtes listes priorisées, et les architectures détaillées (sans code).

---

## 1. Types de services suggérés (réponse initiale)

### 1.1 Services administratifs supplémentaires

- **Autres types de documents** : Actes de mariage, décès, certificats de résidence, certificats de vie, attestations ; duplicatas et rectifications (ex. correction d’état civil).
- **Gestion des rendez-vous consulat** : Prise de rendez-vous en ligne pour dépôt de dossiers, entretiens, légalisation ; gestion de créneaux, files d’attente, rappels par email/SMS.
- **Légalisation / certification de documents** : Demande de légalisation de signature, certification conforme de copies ; suivi et paiement en ligne.

### 1.2 Services orientés citoyen

- **Suivi multi‑demandes / "dossier citoyen"** : Vue unifiée de toutes les demandes passées, en cours et archivées ; téléchargement des justificatifs validés (PDF, récépissés, attestations).
- **Assistance et FAQ avancées** : Base de connaissances interactive (par type de démarche, pays de résidence) ; assistant guidé "Quel document pour quel besoin ?".
- **Notifications multicanales** : Notifications par email/SMS/WhatsApp pour création, paiement, décision, document prêt, expiration de validité.

### 1.3 Services pour le consulat / back-office

- **Workflows avancés de traitement** : Attribution de dossiers à des agents, réassignation, commentaires internes ; étapes de validation (brouillon → en cours → en attente de pièces → approuvé/rejeté).
- **Reporting et statistiques avancés** : Tableaux de bord par pays de résidence, type de document, canal de paiement ; export CSV/Excel détaillé (performances agents, délais moyens).
- **Journal d’audit complet** : Historique de toutes les actions (qui a modifié quoi, quand, depuis quelle IP) ; conformité et contrôle interne.

### 1.4 Intégrations externes

- **Intégration avec registres d’état civil / identité** (si légalement possible) : Vérification automatique de certaines informations.
- **Intégration avec services postaux / messagerie** : Suivi d’envois physiques des documents (numéro de suivi, statut de livraison).
- **Intégration paiement étendue** : Support d’autres moyens (PayPal, virement SEPA, mobile money selon le pays).

### 1.5 Expérience utilisateur et accessibilité

- **Portail mobile ou PWA** : Version optimisée mobile avec ajout sur écran d’accueil, mode hors-ligne partiel.
- **Accessibilité renforcée** : Conformité WCAG (contrastes, navigation clavier, lecteurs d’écran).
- **Multi-langues étendu** : Ajout d’autres langues (anglais, arabe, etc.).

### 1.6 Services "plus innovants"

- **Assistant de remplissage intelligent** : Suggestions automatiques, pré-remplissage selon le profil et l’historique.
- **Simulation de coûts et délais** : Avant de démarrer : estimation des frais, délais moyens, documents à fournir.

---

## 2. Short-list 3–5 services (meilleur rapport valeur / complexité)

1. **Prise de rendez-vous consulaire en ligne**  
   - Valeur : très visible pour les citoyens et le consulat (moins d’appels, meilleure organisation).  
   - Complexité : modérée (modèle rendez-vous + créneaux + notifications standard).

2. **Notifications multicanales (email d’abord, SMS ensuite)**  
   - Valeur : rassure les citoyens, réduit les appels de suivi.  
   - Complexité : faible à modérée (email déjà en place ; ajout de scénarios et templates).

3. **Dossier citoyen / historique complet des demandes**  
   - Valeur : vue claire pour le citoyen et les agents (toutes les demandes, statuts, documents, paiements).  
   - Complexité : surtout front + quelques requêtes backend (données déjà présentes).

4. **Reporting & statistiques opérationnelles**  
   - Valeur : pilotage du consulat (volumes par type de document, pays, délais).  
   - Complexité : base existante avec le bilan comptable ; ajout d’agrégations et écrans.

5. **FAQ / assistant guidé "Quel document pour quel besoin ?"**  
   - Valeur : moins d’erreurs de choix, moins de demandes incomplètes.  
   - Complexité : contenu + logique questions/réponses simple (arbre décisionnel ou recherche par cas).

---

## 3. Dossier citoyen / historique complet des demandes — Architecture et écrans

### 3.1 Objectif

- **Pour le citoyen** : Une page unique avec toutes ses demandes (passées, en cours, rejetées, annulées, approuvées), les documents générés et les paiements associés.
- **Pour l’admin** : Même vue pour un utilisateur donné (historique global).

### 3.2 Architecture backend

- **Modèle** : Pas de nouvelle entité ; regrouper par utilisateur : User → liste de Demande ; chaque Demande → liste de Payment + documents générés (PDF). DTO type CitizenHistoryDTO (profil + liste DemandeHistoryDTO).
- **Endpoints** :  
  - `GET /api/me/history` — Dossier complet pour l’utilisateur connecté (JWT).  
  - `GET /api/admin/users/{userId}/history` — Pour l’admin, historique d’un citoyen (hasRole('ADMIN')).
- **Sécurité** : `/api/me/history` ne prend jamais d’ID utilisateur en paramètre ; `/api/admin/users/{userId}/history` réservé aux admins.

### 3.3 Architecture frontend

- **Navigation** : Lien "Mon dossier" / "Historique de mes demandes" (ex. `/history`) dans le dashboard citoyen ; bouton "Voir le dossier" dans la fiche utilisateur côté admin (ex. `/admin/users/:id/history`).
- **Composants** :  
  - **CitizenHistoryPage** : Appel `GET /api/me/history` ; en-tête (résumé profil, nombre de demandes, total payé) ; filtres (type, période, statut) ; tableau des demandes (type, date, statut, montant, actions Détails / Télécharger PDF) ; détail d’une demande (modal/panneau) : infos, paiements, documents générés.  
  - **AdminUserHistoryPage** : Même logique avec `GET /api/admin/users/{userId}/history` ; infos supplémentaires (notes internes, métadonnées admin).
- **UX** : Colonnes tableau (ID, type, date création, statut, montant payé, boutons) ; vue détail avec onglets Résumé / Paiements / Documents ; état vide "Vous n’avez pas encore de demande" + bouton "Créer une demande".

### 3.4 Logique métier

- Agrégation des montants par demande (somme des paiements PAID) ; statuts consolidés pour le front ; liens sécurisés vers le téléchargement des PDF existants.

---

## 4. Notifications multicanales — Architecture et écrans

### 4.1 Objectif

Informer le citoyen à chaque étape importante (création demande, paiement, décision, document prêt), réduire la charge du consulat, améliorer la perception de professionnalisme. Phase 1 : email obligatoire ; SMS plus tard.

### 4.2 Architecture backend

- **Couche notification** : Service métier (ex. NotificationService) avec méthodes du type notifyDemandCreated(Demande), notifyPaymentSucceeded(Payment), notifyDemandStatusChanged(Demande). Canaux : EmailChannel (existant), plus tard SmsChannel.
- **Templates** : i18n (messages_fr/pt) pour objet et corps (paramètres : nom, type, numéro, lien).
- **Intégration** : Les services métier appellent le NotificationService après les actions (création demande, webhook paiement, changement statut, génération document). Pas depuis les contrôleurs.

### 4.3 Événements à notifier (V1)

1. Création de demande  
2. Paiement réussi  
3. Décision (approuvée / rejetée / pièces complémentaires)  
4. Document prêt / disponible  

### 4.4 Frontend

- Retours immédiats après action (toasts) : "Un email de confirmation vous a été envoyé."
- Plus tard : page Préférences de notification (email oui/non, SMS si activé, numéro).

---

## 5. Événements métier à notifier (application actuelle) — Classes et services

### 5.1 Déjà couvert (partiellement)

- **Changement de statut de la demande** : Envoyé uniquement quand le statut est mis à jour via **DemandeController** (`PUT /api/demandes/{id}/status`) → **DemandeService.updateDemandeStatus** (après save, ligne ~196) → `emailNotificationService.sendStatusChangeNotification(...)`.  
- **À noter** : Si le front admin utilise **AdminController** (`PUT /api/admin/demandes/{id}/status`) → **AdminService.updateDemandeStatus**, aucune notification n’est envoyée. Il faut soit appeler la logique de notification aussi depuis AdminService, soit faire passer tous les changements de statut par DemandeService.

### 5.2 À ajouter

| Événement | Où brancher | Classe / méthode |
|-----------|-------------|------------------|
| **Demande créée** | Après persistance | **DemandeService.createDemande** — après `demandeRepository.save(demande)` (ligne 117) |
| **Paiement réussi** | Après mise à jour paiement et demande | **PaymentService.handleWebhook** — après `demandeRepository.save(demande)` (ligne 136) ; **PaymentService.confirmSessionBySessionId** — après save (ligne 171) |
| **Document prêt** | Après génération et sauvegarde du PDF | **PdfDocumentService.generatePdfDocument** après save, ou **DemandeController.generateDocumentForDemande** après succès |
| **Statut modifié (admin)** | Lors du changement via API admin | **AdminService.updateDemandeStatus** — après `demandeRepository.save(demande)` (ligne 64) — appeler sendStatusChangeNotification ou déléguer à DemandeService |

### 5.3 Optionnels

- **Inscription** : **AuthController.register** — après `userService.createUser(request)` (ligne 73).  
- **Mot de passe réinitialisé** : **AuthController.resetPassword** — après `userService.resetPasswordWithToken(...)` (ligne 161).  
- **Demande supprimée** : **AdminService.deleteDemande** (ligne 129) — notifier le citoyen si pertinent.

### 5.4 Récapitulatif par classe

| Classe | Méthode(s) | Événement(s) |
|--------|------------|---------------|
| DemandeService | createDemande | Demande créée |
| DemandeService | updateDemandeStatus | Changement de statut (déjà fait) |
| PaymentService | handleWebhook, confirmSessionBySessionId | Paiement réussi |
| PdfDocumentService ou DemandeController | generatePdfDocument / generateDocumentForDemande | Document prêt |
| AdminService | updateDemandeStatus | Changement de statut (à aligner) |
| AuthController (optionnel) | register, resetPassword | Inscription, mot de passe réinitialisé |
| AdminService (optionnel) | deleteDemande | Demande supprimée |

---

## 6. Prise de rendez-vous consulaire en ligne — Architecture et écrans

### 6.1 Objectif et périmètre

Permettre aux citoyens de prendre un rendez-vous en ligne (dépôt de dossier, retrait de document, entretien, légalisation) et aux agents de gérer créneaux et planning. V1 : un seul "guichet" ou type de RDV par créneau.

### 6.2 Modèle de données (logique)

- **RendezVous (Appointment)** : Lié à User, optionnellement à Demande ; type (Dépôt, Retrait, Entretien, Légalisation) ; date/heure début (et durée) ; statut (PROPOSED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW) ; commentaire interne admin.
- **Créneau (Slot)** : En V1 on peut éviter une entité Slot et calculer les créneaux à la volée (règles d’ouverture, durée 30 min, moins les RendezVous déjà confirmés).

### 6.3 Backend

- **RendezVousRepository** : findByUser, findByStartTimeBetweenAndStatusNot, findByDemandeId, etc.
- **RendezVousService** : création (vérifier créneau libre, utilisateur, date future) ; annulation (citoyen jusqu’à X h avant, admin toujours) ; liste créneaux disponibles pour une date/type ; liste des RDV (citoyen / admin).
- **Endpoints citoyen** : `GET /api/rendezvous`, `GET /api/rendezvous/slots?date=...&type=...`, `POST /api/rendezvous`, `DELETE ou PATCH .../cancel`.
- **Endpoints admin** : `GET /api/admin/rendezvous`, `PATCH /api/admin/rendezvous/{id}` (modifier, annuler, marquer COMPLETED/NO_SHOW).
- **Sécurité** : JWT pour citoyen (ses RDV uniquement) ; ADMIN pour routes admin.

### 6.4 Frontend

- **Citoyen** : Liste "Mes rendez-vous" ; parcours "Prendre un rendez-vous" (type → date → créneau → optionnel demande liée → récap → confirmer) ; détail + annulation si autorisée.
- **Admin** : Liste des RDV (filtres date, statut, type, utilisateur) ; vue planning (jour/semaine) ; détail + modifier / annuler / marquer effectué ou no-show.

### 6.5 Paramétrage

- Horaires d’ouverture (config ou table) ; durée créneau (ex. 30 min) ; jours fériés ; limite d’annulation (ex. 24 h avant) ; types de RDV (liste avec i18n).

---

## 7. Événements métier à notifier pour les rendez-vous (création, annulation, rappel)

### 7.1 Événements

| Événement | Destinataire | Contenu type |
|-----------|--------------|--------------|
| Rendez-vous créé | Citoyen | Confirmation : date, heure, type, lieu, récap, lien "Mes rendez-vous" |
| Rendez-vous annulé | Citoyen | Date/heure du RDV annulé, motif si communiqué, possibilité de reprendre un autre créneau |
| Rappel 24 h / 48 h avant | Citoyen | Rappel : date, heure, type, lieu, pièces à apporter si pertinent |

### 7.2 Où brancher

| Événement | Où brancher | Méthode / point d’appel |
|-----------|-------------|--------------------------|
| **Rendez-vous créé** | Après persistance | **RendezVousService** — méthode de création (ex. createRendezVous) — juste après `rendezVousRepository.save(rendezVous)` |
| **Rendez-vous annulé** | Après mise à jour statut en CANCELLED | **RendezVousService** — méthode d’annulation (ex. cancelRendezVous ou cancelByUser / cancelByAdmin) — après save ; ne notifier que si le RDV n’était pas déjà annulé |
| **Rappel 24 h / 48 h** | Job planifié | **Job** (ex. @Scheduled) appelle **RendezVousService** (ou RappelRendezVousService) — méthode du type sendRemindersForUpcomingAppointments() : récupère les RDV dans 24 h (ou 48 h), statut CONFIRMED, pas encore rappelé ; envoie l’email ; marque rappel envoyé (champ reminderSentAt ou équivalent) pour idempotence |

### 7.3 Service de notification

- Méthodes : notifyRendezVousCreated(RendezVous, User), notifyRendezVousCancelled(RendezVous, User, cancelledBy, motif), notifyRendezVousReminder(RendezVous, User).
- Templates i18n (FR/PT) : notification.rendezvous.created, .cancelled, .reminder (subject + body).
- Utiliser la langue préférée de l’utilisateur.

### 7.4 Ordre d’implémentation

1. Création : brancher "rendez-vous créé" dans la méthode de création après save.  
2. Annulation : brancher "rendez-vous annulé" dans la méthode d’annulation après mise à jour du statut.  
3. Rappel : champ "rappel envoyé" sur RendezVous ; logique de sélection des RDV à rappeler ; job planifié qui appelle le service.

---

*Document généré à partir des échanges sur l’intégration de services dans eConsulat. Aucun code n’a été écrit ; toutes les descriptions sont au niveau architecture et spécification.*
