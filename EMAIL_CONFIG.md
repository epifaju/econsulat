# Configuration Email pour eConsulat

## Configuration requise

Pour que la fonctionnalité de validation d'email fonctionne, vous devez configurer un service SMTP.

### 1. Configuration Gmail (Recommandé)

1. Activez l'authentification à deux facteurs sur votre compte Gmail
2. Générez un mot de passe d'application :

   - Allez dans les paramètres de votre compte Google
   - Sécurité > Authentification à 2 facteurs > Mots de passe d'application
   - Générez un nouveau mot de passe d'application pour "Mail"

3. Mettez à jour `backend/src/main/resources/application.properties` :

```properties
# Configuration email Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-d-application
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### 2. Configuration Outlook/Hotmail

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=votre-email@outlook.com
spring.mail.password=votre-mot-de-passe
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3. Configuration avec un service SMTP externe

Vous pouvez utiliser des services comme :

- SendGrid
- Mailgun
- Amazon SES
- Brevo (anciennement Sendinblue)

Exemple avec SendGrid :

```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=votre-api-key-sendgrid
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Test de la configuration

1. Redémarrez le backend après avoir configuré l'email
2. Connectez-vous en tant qu'admin
3. Créez un nouvel utilisateur
4. Vérifiez que l'email de validation est reçu

## Dépannage

### Erreur "Authentication failed"

- Vérifiez que le mot de passe d'application est correct
- Assurez-vous que l'authentification à 2 facteurs est activée

### Erreur "Connection timeout"

- Vérifiez que le port et l'hôte sont corrects
- Vérifiez votre connexion internet
- Vérifiez que le pare-feu n'empêche pas la connexion

### Emails non reçus

- Vérifiez le dossier spam
- Vérifiez que l'adresse email de destination est correcte
- Vérifiez les logs du backend pour les erreurs d'envoi

## Sécurité

⚠️ **Important** : Ne committez jamais vos identifiants email dans le code source. Utilisez des variables d'environnement ou un fichier de configuration séparé.

Exemple avec des variables d'environnement :

```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```
