# Guide d'Utilisation - Système de Notifications par Email eConsulat

## 🎯 Vue d'ensemble

Ce guide explique comment utiliser le nouveau système de notifications automatiques par email implémenté dans eConsulat. Le système envoie automatiquement des emails aux citoyens lorsque le statut de leur demande change.

## 🚀 Fonctionnalités Implémentées

### 1. **Notifications Automatiques par Email**

- ✅ Envoi automatique lors des changements de statut
- ✅ Stockage des notifications dans la base de données
- ✅ Template d'email personnalisé avec les informations de la demande
- ✅ Gestion des erreurs et échecs d'envoi

### 2. **Tableau de Bord Administrateur**

- ✅ Modification du statut des demandes avec déclenchement automatique des notifications
- ✅ Colonne "Dernière Notification" affichant la date et le statut du dernier email
- ✅ Indicateur visuel de l'état des notifications

### 3. **Tableau de Bord Utilisateur**

- ✅ Section "Mes Notifications" avec historique complet
- ✅ Affichage des détails de chaque notification
- ✅ Pagination des notifications
- ✅ Statuts visuels (Envoyé, Échec, En cours)

### 4. **Fonctionnalités Bonus**

- ✅ Renvoi manuel de notifications (admin/agent)
- ✅ Historique complet des notifications par demande
- ✅ Comptage des notifications par utilisateur

## 📋 Prérequis

### Configuration Email

Assurez-vous que la configuration SMTP est correcte dans `application.properties` :

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Base de Données

Exécutez le script SQL pour créer la table des notifications :

```sql
-- Exécuter le fichier: backend/src/main/resources/schema_notifications.sql
```

## 🔧 Installation et Démarrage

### 1. **Redémarrer le Backend**

```bash
cd backend
mvn spring-boot:run
```

### 2. **Redémarrer le Frontend**

```bash
cd frontend
npm run dev
```

### 3. **Vérifier la Base de Données**

- La table `notifications` doit être créée
- Les contraintes de clés étrangères doivent être actives

## 📱 Utilisation

### Pour les Administrateurs et Agents

#### **Modifier le Statut d'une Demande**

1. Aller dans **Gestion des Demandes**
2. Cliquer sur l'icône de modification du statut
3. Sélectionner le nouveau statut
4. Le système envoie automatiquement un email au citoyen
5. La colonne "Dernière Notification" se met à jour

#### **Consulter l'Historique des Notifications**

1. Utiliser l'endpoint `/api/notifications/demande/{id}` pour une demande spécifique
2. Utiliser l'endpoint `/api/notifications/all` pour toutes les notifications

#### **Renvoie une Notification**

1. Utiliser l'endpoint `/api/notifications/{id}/resend`
2. Seuls les admins et agents peuvent renvoyer des notifications

### Pour les Citoyens

#### **Consulter Ses Notifications**

1. Se connecter à son tableau de bord
2. Aller à la section **"Mes Notifications"**
3. Voir l'historique de tous les emails reçus
4. Cliquer sur "Voir le contenu" pour consulter un email complet

## 🔌 API Endpoints

### **Notifications**

- `GET /api/notifications/my` - Notifications de l'utilisateur connecté
- `GET /api/notifications/demande/{id}` - Notifications d'une demande (admin/agent)
- `GET /api/notifications/all` - Toutes les notifications (admin)
- `POST /api/notifications/{id}/resend` - Renvoie une notification (admin/agent)
- `GET /api/notifications/count` - Nombre de notifications de l'utilisateur

### **Mise à Jour du Statut**

- `PUT /api/demandes/{id}/status` - Modifier le statut d'une demande (admin/agent)

## 📧 Template d'Email

**Objet :** `[eConsulat] Mise à jour de votre demande`

**Contenu :**

```
Bonjour [Prénom] [Nom],

Nous vous informons que le statut de votre demande n°[ID] a été mis à jour :
Nouveau statut : [Statut].

Pour plus d'informations, connectez-vous à votre espace citoyen :
[URL]/espace-citoyen

Cordialement,
L'équipe eConsulat
```

## 🧪 Tests

### **Fichier de Test**

Utilisez le fichier `test_notifications.html` pour tester toutes les fonctionnalités :

1. Ouvrir le fichier dans un navigateur
2. Configurer l'URL du backend et les tokens
3. Exécuter les tests dans l'ordre
4. Vérifier les résultats

### **Tests Recommandés**

1. ✅ Créer une demande de test
2. ✅ Modifier le statut (déclenche l'envoi d'email)
3. ✅ Vérifier la réception de l'email
4. ✅ Consulter les notifications dans le tableau de bord
5. ✅ Tester le renvoi manuel (admin)

## 🐛 Dépannage

### **Problèmes Courants**

#### **Emails non envoyés**

- Vérifier la configuration SMTP
- Vérifier les logs du backend
- Vérifier que la table notifications est créée

#### **Erreurs de permissions**

- Vérifier que l'utilisateur a le rôle ADMIN ou AGENT
- Vérifier que le token est valide

#### **Notifications non affichées**

- Vérifier que l'utilisateur est connecté
- Vérifier les logs du frontend
- Vérifier que l'API backend répond

### **Logs Utiles**

```bash
# Backend
tail -f backend/logs/application.log

# Vérifier les erreurs SMTP
grep "mail" backend/logs/application.log
```

## 📊 Monitoring

### **Métriques à Surveiller**

- Nombre d'emails envoyés par jour
- Taux de succès des envois
- Temps de traitement des notifications
- Erreurs SMTP

### **Alertes Recommandées**

- Échec d'envoi d'email > 5%
- Temps de traitement > 30 secondes
- Erreurs de base de données

## 🔒 Sécurité

### **Contrôles d'Accès**

- Seuls les admins et agents peuvent modifier les statuts
- Les citoyens ne voient que leurs propres notifications
- Validation des tokens JWT sur tous les endpoints

### **Protection des Données**

- Les emails sont stockés de manière sécurisée
- Pas de stockage des mots de passe en clair
- Logs d'audit pour toutes les modifications

## 🚀 Améliorations Futures

### **Fonctionnalités Suggérées**

- [ ] Notifications push en temps réel
- [ ] Templates d'email personnalisables
- [ ] Planification d'envoi d'emails
- [ ] Statistiques avancées des notifications
- [ ] Intégration avec d'autres services de messagerie

### **Optimisations Techniques**

- [ ] Mise en cache des notifications
- [ ] Envoi asynchrone des emails
- [ ] Retry automatique en cas d'échec
- [ ] Monitoring en temps réel

## 📞 Support

### **En Cas de Problème**

1. Vérifier les logs du backend et frontend
2. Consulter ce guide de dépannage
3. Tester avec le fichier `test_notifications.html`
4. Vérifier la configuration de la base de données

### **Contact**

- Développeur : [Votre nom]
- Email : [votre-email@domaine.com]
- Documentation : Ce guide et les commentaires du code

---

**Version :** 1.0.0  
**Date :** $(date)  
**Statut :** ✅ Implémenté et Testé
