# Guide de dépannage - Frontend eConsulat

## Problèmes de connexion à http://localhost:5173

### 1. Vérification des prérequis

#### Node.js et npm

```bash
node --version  # Doit être 16+
npm --version   # Doit être 8+
```

Si Node.js n'est pas installé :

- Téléchargez depuis https://nodejs.org/
- Redémarrez votre terminal après installation

#### Dépendances

```bash
cd frontend
npm install
```

### 2. Problèmes courants

#### Port 5173 déjà utilisé

```bash
# Windows
netstat -ano | findstr :5173
taskkill /f /pid <PID>

# Ou utilisez le script
diagnostic_frontend.bat
```

#### Erreur "Cannot find module"

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

#### Erreur "ENOENT: no such file or directory"

```bash
# Vérifiez que vous êtes dans le bon dossier
cd frontend
ls package.json  # Doit exister
```

### 3. Démarrage manuel

#### Backend (Terminal 1)

```bash
cd backend
mvn spring-boot:run
```

#### Frontend (Terminal 2)

```bash
cd frontend
npm run dev
```

### 4. Vérification des services

#### Test du backend

```bash
curl http://localhost:8080/api/auth/test
# Doit retourner: {"message":"Backend is running"}
```

#### Test du frontend

- Ouvrez http://localhost:5173
- Vérifiez la console du navigateur (F12)
- Regardez les erreurs réseau

### 5. Configuration Vite

Vérifiez `frontend/vite.config.js` :

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
  },
});
```

### 6. Problèmes de CORS

Si vous avez des erreurs CORS :

- Vérifiez que le backend est démarré sur le port 8080
- Vérifiez la configuration CORS dans `SecurityConfig.java`

### 7. Scripts de diagnostic

#### Diagnostic complet

```bash
diagnostic_frontend.bat
```

#### Démarrage automatique

```bash
start_complete.bat
```

### 8. Logs utiles

#### Frontend (console navigateur)

- Erreurs JavaScript
- Erreurs réseau
- Erreurs de build

#### Backend (terminal)

- Erreurs de compilation
- Erreurs de base de données
- Erreurs de port

### 9. Solutions rapides

#### Redémarrage complet

1. Arrêtez tous les processus (Ctrl+C)
2. Fermez tous les terminaux
3. Exécutez `start_complete.bat`

#### Réinstallation des dépendances

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

#### Nettoyage du cache

```bash
cd frontend
npm run build
npm run dev
```

### 10. Contact

Si les problèmes persistent :

1. Vérifiez les logs d'erreur
2. Testez avec les scripts de diagnostic
3. Vérifiez la configuration de votre environnement
