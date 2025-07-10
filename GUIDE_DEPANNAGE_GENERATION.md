# 🔧 Guide de Dépannage - Génération de Document

## ✅ Vérifications préalables

### 1. **Backend démarré**

```bash
# Vérifier que le backend écoute sur le port 8080
netstat -an | findstr :8080
```

**Résultat attendu :** `TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING`

### 2. **Frontend démarré**

```bash
# Vérifier que le frontend écoute sur le port 5173
netstat -an | findstr :5173
```

**Résultat attendu :** `TCP    127.0.0.1:5173         0.0.0.0:0              LISTENING`

### 3. **Template disponible**

Vérifier que le fichier existe :

```
backend/src/main/resources/templates/default_template.docx
```

## 🧪 Tests de diagnostic

### Test 1 : Connexion de base

Ouvrez `test_connection_simple.html` dans votre navigateur et cliquez sur "Tester la connexion"

**Résultat attendu :** `✅ Connexion OK - Authentification requise (normal)`

### Test 2 : Test CORS

Dans la même page, cliquez sur "Tester CORS"

**Résultat attendu :** `✅ CORS configuré correctement`

### Test 3 : Test avec authentification

1. Connectez-vous à l'interface admin (http://localhost:5173)
2. Ouvrez les outils de développement (F12)
3. Allez dans Application → Local Storage
4. Copiez la valeur de la clé "token"
5. Dans `test_connection_simple.html`, cliquez sur "Tester Authentification"
6. Collez le token

**Résultat attendu :** `✅ Authentification réussie`

## 🚨 Erreurs courantes et solutions

### Erreur : "Problème de connexion lors de la génération"

**Causes possibles :**

1. **Backend non démarré** → Redémarrez avec `mvn spring-boot:run`
2. **Problème CORS** → Vérifiez la configuration dans `SecurityConfig.java`
3. **Token invalide** → Reconnectez-vous à l'interface admin
4. **Port bloqué** → Vérifiez le pare-feu Windows

**Solution :**

```bash
# Redémarrer le backend
cd backend
mvn clean compile
mvn spring-boot:run
```

### Erreur : "Erreur lors de la génération du document"

**Causes possibles :**

1. **Template manquant** → Vérifiez que `default_template.docx` existe
2. **Données manquantes** → Vérifiez que la demande a tous les champs requis
3. **Permissions** → Vérifiez les droits d'écriture dans le dossier `documents`

**Solution :**

```bash
# Vérifier le template
ls backend/src/main/resources/templates/

# Créer le dossier documents s'il n'existe pas
mkdir backend/documents
```

### Erreur : "Impossible de télécharger le document"

**Causes possibles :**

1. **Fichier non généré** → Vérifiez que la génération a réussi
2. **Permissions** → Vérifiez les droits de lecture
3. **Type MIME incorrect** → Vérifiez la configuration du contrôleur

## 🔍 Logs de débogage

### Backend

Les logs du backend montrent :

- ✅ Authentification réussie
- ✅ Demande trouvée
- ✅ Type de document trouvé
- ✅ Document généré
- ✅ Fichier sauvegardé

### Frontend

Ouvrez la console du navigateur (F12) pour voir :

- Les requêtes HTTP
- Les erreurs JavaScript
- Les réponses du serveur

## 📋 Checklist de résolution

- [ ] Backend démarré sur le port 8080
- [ ] Frontend démarré sur le port 5173
- [ ] Template `default_template.docx` présent
- [ ] Dossier `documents` créé avec permissions
- [ ] Token JWT valide
- [ ] CORS configuré correctement
- [ ] Demande avec tous les champs requis
- [ ] Type de document existant en base

## 🆘 Si rien ne fonctionne

1. **Redémarrez tout :**

   ```bash
   # Arrêter tous les processus
   taskkill /f /im java.exe
   taskkill /f /im node.exe

   # Redémarrer le backend
   cd backend
   mvn spring-boot:run

   # Redémarrer le frontend (dans un autre terminal)
   cd frontend
   npm run dev
   ```

2. **Vérifiez les logs complets :**

   - Backend : Regardez la console où `mvn spring-boot:run` est lancé
   - Frontend : Console du navigateur (F12)

3. **Testez avec curl :**
   ```bash
   curl -X POST "http://localhost:8080/api/admin/documents/generate?demandeId=1&documentTypeId=1" \
        -H "Authorization: Bearer YOUR_TOKEN" \
        -H "Content-Type: application/json"
   ```

## 📞 Support

Si le problème persiste, fournissez :

1. Les logs du backend
2. Les erreurs de la console du navigateur
3. Le résultat des tests de diagnostic
4. La version de Java et Node.js utilisée
