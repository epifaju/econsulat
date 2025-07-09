# 🛂 Guide de Génération de Documents de Passeport

## Vue d'ensemble

Cette fonctionnalité permet de générer automatiquement des documents de passeport à partir du template Word "FORMULARIO DE PEDIDO DE PASSAPORTE.docx" en remplissant automatiquement les données du citoyen.

## Architecture

### Backend (Spring Boot)

#### Services

- **PassportDocumentService** : Service principal pour la génération de documents
  - `generatePassportDocument(Citizen citizen)` : Génère le document Word
  - `getDocument(String filename)` : Récupère le document pour téléchargement
  - `deleteDocument(String filename)` : Supprime le document

#### Contrôleurs

- **PassportController** : Endpoints REST pour la gestion des documents
  - `POST /api/passport/generate/{citizenId}` : Génère un document pour un citoyen
  - `GET /api/passport/download/{filename}` : Télécharge un document
  - `DELETE /api/passport/delete/{filename}` : Supprime un document

#### Dépendances

- **Apache POI** : Manipulation des fichiers Word (.docx)
  - `poi` : API de base
  - `poi-ooxml` : Support des formats OOXML
  - `poi-ooxml-schemas` : Schémas OOXML

### Frontend (React)

#### Composants modifiés

- **AdminDashboard** : Interface d'administration
  - Bouton 🛂 pour générer les documents de passeport
  - Intégration dans les vues tableau et cartes
  - Modal de détails avec action de génération

#### Fonctionnalités

- Génération automatique de documents
- Téléchargement immédiat après génération
- Gestion des erreurs avec notifications toast
- Interface responsive (desktop/mobile)

## Correspondance des données

Le service remplace automatiquement les placeholders suivants dans le template :

| Donnée citoyen    | Placeholder dans le template                     |
| ----------------- | ------------------------------------------------ |
| Prénom            | `Nome` ou `[Nome]`                               |
| Nom de famille    | `Apelido` ou `[Apelido]`                         |
| Date de naissance | `Data de nascimento` ou `[Data de nascimento]`   |
| Lieu de naissance | `Local de nascimento` ou `[Local de nascimento]` |

## Utilisation

### 1. Préparation du template

1. Placer le fichier "FORMULARIO DE PEDIDO DE PASSAPORTE.docx" dans `backend/src/main/resources/`
2. S'assurer que les placeholders sont présents dans le document
3. Le template peut contenir des placeholders avec ou sans crochets

### 2. Génération via l'interface admin

1. Se connecter en tant qu'administrateur
2. Aller sur le dashboard administrateur
3. Cliquer sur le bouton 🛂 à côté d'un citoyen
4. Le document sera généré et téléchargé automatiquement

### 3. Génération via API

```bash
# Générer un document
curl -X POST http://localhost:8080/api/passport/generate/1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Télécharger un document
curl -X GET http://localhost:8080/api/passport/download/filename.docx \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output document.docx
```

## Structure des fichiers générés

Les documents sont sauvegardés dans le dossier `backend/uploads/` avec le format :

```
passport_{citizenId}_{uuid}.docx
```

Exemple : `passport_1_550e8400-e29b-41d4-a716-446655440000.docx`

## Sécurité

- Seuls les utilisateurs avec le rôle `ADMIN` peuvent générer des documents
- Les documents sont générés avec des noms uniques (UUID)
- Validation des données d'entrée côté serveur
- Protection CSRF activée

## Gestion des erreurs

### Erreurs courantes

1. **Template non trouvé**

   - Vérifier que le fichier est dans `backend/src/main/resources/`
   - Vérifier le nom exact du fichier

2. **Citoyen non trouvé**

   - Vérifier que l'ID du citoyen existe
   - Vérifier les permissions d'accès

3. **Erreur de génération**
   - Vérifier les permissions d'écriture dans `backend/uploads/`
   - Vérifier l'intégrité du template Word

### Logs

Les erreurs sont loggées dans les logs Spring Boot. Vérifier :

```bash
# Logs du backend
tail -f backend/logs/application.log
```

## Tests

### Test manuel

Utiliser le fichier `test_passport_generation.html` pour tester :

1. L'authentification
2. La récupération des citoyens
3. La génération de documents
4. Le téléchargement

### Test automatisé

```bash
# Démarrer l'application
./start.sh

# Ouvrir le fichier de test
open test_passport_generation.html
```

## Maintenance

### Nettoyage des fichiers

Les documents générés peuvent être supprimés via l'API :

```bash
curl -X DELETE http://localhost:8080/api/passport/delete/filename.docx \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Sauvegarde du template

Le template original est préservé dans les ressources. Les modifications doivent être faites sur une copie.

## Évolutions futures

- Support de templates PDF
- Génération en lot
- Historique des documents générés
- Notifications par email
- Signature électronique
- Intégration avec des services d'impression

## Support

Pour toute question ou problème :

1. Vérifier les logs du backend
2. Tester avec le fichier HTML de test
3. Vérifier la configuration des dépendances Maven
4. Contacter l'équipe de développement
