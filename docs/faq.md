# FAQ

## je viens de merger une branche et des components existants ne sont plus trouvés

- arrête tes instances en cours et relance ton projet

```bash
# fermer les terminaux ou l'app front a été lancée
# aller dans le dossier front
cd front
# supprimer le dossier node_modules et le fichier package-lock.json
rm -rf node_modules package-lock.json
# relancer l'installation des dépendances
npm install
# relancer le front
npm run start -- --host 127.0.0.1 --port 4200
```