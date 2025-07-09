# 🔧 Guide de Dépannage Maven - eConsulat

## 🚨 Problème: "No plugin found for prefix 'spring-boot'"

### Symptômes

- Erreur lors de l'exécution de `mvn spring-boot:run`
- Message "No plugin found for prefix 'spring-boot'"
- Échec de build Maven

### Solutions

#### Solution 1: Correction automatique (Recommandée)

```bash
# Exécuter le script de correction Maven
fix_maven_backend.bat
```

#### Solution 2: Nettoyage complet du cache

```bash
# Nettoyer complètement le cache Maven
clean_maven_cache.bat
```

#### Solution 3: Démarrage avec correction automatique

```bash
# Démarrage complet avec correction Maven et base de données
start_with_maven_fix.bat
```

## 🔍 Diagnostic étape par étape

### Étape 1: Vérifier l'installation de Maven

```bash
mvn --version
```

**Résultat attendu:**

```
Apache Maven 3.x.x
Maven home: C:\...
Java version: 17.x.x
```

### Étape 2: Vérifier le fichier pom.xml

Le fichier `backend/pom.xml` doit contenir:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Étape 3: Tester la connexion internet

```bash
# Tester l'accès au repository Maven central
ping repo.maven.apache.org
```

### Étape 4: Vérifier le cache Maven

```bash
# Vérifier si le cache existe
dir %USERPROFILE%\.m2\repository
```

## 🛠️ Solutions manuelles

### Solution 1: Nettoyage manuel

```bash
# Aller dans le répertoire backend
cd backend

# Nettoyer le projet
mvn clean

# Télécharger les dépendances
mvn dependency:resolve

# Compiler
mvn compile

# Tester le plugin Spring Boot
mvn spring-boot:help
```

### Solution 2: Suppression du cache

```bash
# Supprimer le cache Maven
rmdir /s /q %USERPROFILE%\.m2\repository

# Reconstruire le projet
cd backend
mvn clean compile
```

### Solution 3: Vérification des dépendances

```bash
# Vérifier les dépendances manquantes
mvn dependency:tree

# Télécharger les dépendances manquantes
mvn dependency:resolve
```

## 📋 Checklist de vérification

- [ ] Maven est installé et dans le PATH
- [ ] Java 17+ est installé et dans le PATH
- [ ] Le fichier `backend/pom.xml` est correct
- [ ] La connexion internet fonctionne
- [ ] Le cache Maven n'est pas corrompu
- [ ] Les dépendances sont téléchargées
- [ ] Le plugin Spring Boot est disponible

## 🐛 Erreurs courantes

### Erreur: "Could not resolve dependencies"

**Cause:** Problème de connexion internet ou repository inaccessible
**Solution:**

```bash
# Vérifier la connexion
ping repo.maven.apache.org

# Forcer le téléchargement
mvn dependency:resolve -U
```

### Erreur: "Plugin execution not covered by lifecycle"

**Cause:** Configuration Maven incorrecte
**Solution:** Vérifier le fichier `pom.xml` et s'assurer qu'il hérite de `spring-boot-starter-parent`

### Erreur: "No compiler is provided in this environment"

**Cause:** Java n'est pas installé ou pas dans le PATH
**Solution:** Installer Java 17+ et l'ajouter au PATH

### Erreur: "Permission denied"

**Cause:** Droits d'accès insuffisants
**Solution:** Exécuter en tant qu'administrateur

## 🔄 Procédure de récupération complète

Si rien ne fonctionne, suivez cette procédure complète:

1. **Arrêter tous les processus**

   ```bash
   # Fermer toutes les fenêtres de commande
   # Arrêter les services Java si nécessaire
   ```

2. **Nettoyer complètement**

   ```bash
   clean_maven_cache.bat
   ```

3. **Redémarrer l'ordinateur**

4. **Reconstruire le projet**
   ```bash
   start_with_maven_fix.bat
   ```

## 📞 Support avancé

Si les problèmes persistent:

1. **Vérifier les logs Maven**

   ```bash
   mvn spring-boot:run -X
   ```

2. **Vérifier la configuration Maven**

   ```bash
   mvn help:effective-settings
   ```

3. **Tester avec un projet simple**

   ```bash
   mvn archetype:generate -DgroupId=com.test -DartifactId=test-project
   ```

4. **Vérifier les variables d'environnement**
   ```bash
   echo %JAVA_HOME%
   echo %MAVEN_HOME%
   echo %PATH%
   ```

## 🎯 Solutions rapides

### Pour un démarrage rapide:

```bash
start_with_maven_fix.bat
```

### Pour un nettoyage complet:

```bash
clean_maven_cache.bat
```

### Pour un diagnostic:

```bash
fix_maven_backend.bat
```
