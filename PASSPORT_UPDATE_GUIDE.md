# 🛂 Guide de Mise à Jour - Génération de Documents de Passeport

## ✅ Fonctionnalités Implémentées

### 1. Support des Placeholders avec Syntaxe `{{}}`

Le service supporte maintenant les placeholders suivants dans le template Word :

| Placeholder               | Donnée citoyen            | Exemple    |
| ------------------------- | ------------------------- | ---------- |
| `{{Prénom}}`              | Prénom                    | Jean       |
| `{{Nom de famille}}`      | Nom de famille            | Dupont     |
| `{{Date de naissance}}`   | Date de naissance         | 15/03/1985 |
| `{{Local de nascimento}}` | Lieu de naissance         | Paris      |
| `{{Lieu de naissance}}`   | Lieu de naissance (alias) | Paris      |

### 2. Endpoints API REST

#### Génération de documents

- `POST /api/passport/generate/{citizenId}` - Génère un document Word
- `POST /api/passport/generate/{citizenId}/pdf` - Génère un document PDF

#### Gestion des fichiers

- `GET /api/passport/download/{filename}` - Télécharge un document
- `DELETE /api/passport/delete/{filename}` - Supprime un document

### 3. Interface Frontend Améliorée

#### Dashboard Administrateur

- Bouton 📄 pour générer des documents Word
- Bouton 📋 pour générer des documents PDF
- Téléchargement automatique après génération
- Interface responsive (desktop/mobile)

#### Modal de détails

- Options de génération Word et PDF
- Actions contextuelles selon le statut

## 🔧 Modifications Techniques

### Backend (Spring Boot)

#### Nouvelles dépendances Maven

```xml
<!-- Word Document Generation -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.3</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml-schemas</artifactId>
    <version>4.1.2</version>
</dependency>
```

#### Nouveaux fichiers créés

- `PassportDocumentService.java` - Service de génération de documents
- `PassportController.java` - Contrôleur REST pour les endpoints

#### Fonctionnalités du service

- Remplacement automatique des placeholders dans tous les éléments du document
- Support des paragraphes, tableaux, en-têtes, pieds de page
- Génération de noms de fichiers uniques avec UUID
- Gestion des erreurs et validation

### Frontend (React)

#### Modifications du composant AdminDashboard

- Ajout des fonctions `generatePassportDocument()` et `downloadPassportDocument()`
- Intégration des boutons de génération dans les vues tableau et cartes
- Gestion des états de chargement et des erreurs
- Notifications toast pour le feedback utilisateur

## 📋 Préparation du Template

### 1. Structure du template Word

Le fichier `FORMULARIO DE PEDIDO DE PASSAPORTE.docx` doit contenir les placeholders exactement comme suit :

```
Nom: {{Prénom}}
Apelido: {{Nom de famille}}
Data de nascimento: {{Date de naissance}}
Local de nascimento: {{Local de nascimento}}
```

### 2. Emplacement du template

Le template doit être placé dans :

```
backend/src/main/resources/FORMULARIO DE PEDIDO DE PASSAPORTE.docx
```

### 3. Format des placeholders

- Utiliser la syntaxe `{{placeholder}}` avec des doubles accolades
- Les placeholders sont sensibles à la casse
- Espaces autorisés dans les noms de placeholders

## 🚀 Utilisation

### 1. Via l'interface web

1. Se connecter en tant qu'administrateur
2. Aller sur le dashboard administrateur
3. Cliquer sur 📄 pour générer un document Word
4. Cliquer sur 📋 pour générer un document PDF
5. Le document sera téléchargé automatiquement

### 2. Via l'API REST

```bash
# Générer un document Word
curl -X POST http://localhost:8080/api/passport/generate/1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Générer un document PDF
curl -X POST http://localhost:8080/api/passport/generate/1/pdf \
  -H "Authorization: Bearer YOUR_TOKEN"

# Télécharger un document
curl -X GET http://localhost:8080/api/passport/download/filename.docx \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output document.docx
```

## 🧪 Tests

### Fichiers de test créés

- `test_passport_generation.html` - Test général de l'API
- `test_passport_template.html` - Test spécifique avec placeholders

### Procédure de test

1. Démarrer l'application : `./start.sh`
2. Ouvrir `test_passport_template.html` dans un navigateur
3. Suivre les étapes de test :
   - Authentification
   - Récupération des citoyens
   - Génération de documents
   - Téléchargement

## 🔒 Sécurité

- Seuls les utilisateurs avec le rôle `ADMIN` peuvent générer des documents
- Validation des données d'entrée côté serveur
- Noms de fichiers uniques pour éviter les conflits
- Protection CSRF activée

## 📁 Structure des fichiers générés

Les documents sont sauvegardés dans `backend/uploads/` avec le format :

```
passport_{citizenId}_{uuid}.docx
passport_{citizenId}_{uuid}.pdf
```

## ⚠️ Limitations actuelles

1. **Conversion PDF** : La conversion Word vers PDF n'est pas encore implémentée
2. **Templates complexes** : Support limité pour les documents avec des éléments très complexes
3. **Images** : Les placeholders dans les images ne sont pas supportés

## 🔮 Évolutions futures

- Implémentation de la conversion PDF avec iText ou Apache FOP
- Support de templates plus complexes
- Génération en lot de documents
- Historique des documents générés
- Notifications par email
- Signature électronique

## 🐛 Dépannage

### Erreurs courantes

1. **Template non trouvé**

   ```
   Template de passeport non trouvé: FORMULARIO DE PEDIDO DE PASSAPORTE.docx
   ```

   - Vérifier que le fichier est dans `backend/src/main/resources/`

2. **Placeholders non remplacés**

   - Vérifier la syntaxe exacte des placeholders
   - S'assurer que les données du citoyen sont complètes

3. **Erreur de permissions**
   - Vérifier les permissions d'écriture dans `backend/uploads/`
   - Créer le dossier s'il n'existe pas

### Logs utiles

```bash
# Logs du backend
tail -f backend/logs/application.log

# Vérifier les fichiers générés
ls -la backend/uploads/
```

## 📞 Support

Pour toute question ou problème :

1. Vérifier les logs du backend
2. Tester avec les fichiers HTML de test
3. Vérifier la configuration des dépendances Maven
4. Contacter l'équipe de développement
