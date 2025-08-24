@echo off
echo ========================================
echo 🔧 Redémarrage Backend avec Correction
echo    Mapping des Types de Documents
echo ========================================
echo.

echo 🛑 Arrêt des processus Java...
call stop_java_processes.bat
echo.

echo ⏳ Attente de 3 secondes...
timeout /t 3 /nobreak > nul
echo.

echo 🚀 Démarrage du backend avec les corrections...
echo.
echo ✅ Mapping des types de documents corrigé :
echo    - ACTE_NAISSANCE → ID 2 (Acte de naissance)
echo    - CARTE_IDENTITE → ID 5 (Carte d'identité)
echo    - AUTRE → ID 6 (Autre)
echo.

call start_backend.bat

echo.
echo ========================================
echo 🎯 Correction appliquée avec succès !
echo ========================================
echo.
echo 📋 Prochaines étapes :
echo    1. Vérifier que le backend démarre sans erreur
echo    2. Tester l'interface admin "Gestion des Demandes"
echo    3. Créer une demande de type "Acte de naissance"
echo    4. Vérifier que le type affiché est correct
echo    5. Créer une demande de type "Carte d'identité"
echo    6. Vérifier que le type affiché est correct
echo.
echo 💡 Si nécessaire, exécuter le script SQL :
echo    fix_document_type_mapping.sql
echo.
pause
