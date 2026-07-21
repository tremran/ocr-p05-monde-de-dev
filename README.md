# MDD project

Réseau social pour développeurs

Ce projet contient deux dossiers principaux :

- `front` : projet angular
- `back` : api java

## Getting started

### Pré requis

- java 21
- angular 14

### Installation

- Back
```bash
# get in back folder
cd back
# launch dev server
mvn spring-boot:run

# run tests
./mvnw test
# check code coverage
./mvnw verify 
# afficher le code coverage
xdg-open target/site/jacoco/index.html
```
- Front

```bash
# get in the front folder
cd front
# install dependencies
npm install
# run application in dev mode
npm run start
# open your browser on http://localhost:4200


# run tests with code coverage
npm run test
```

## Tests e2e

Sur un environnement dédié il est possible de lancer les tests e2e depuis le front avec la commande `npm run test:e2e` après avoir lancé le front sur le port 4200 et le back sur le port 3001

### Technologies

- Angular 14
- java 21
- mysql

## Conception

Merci de lire 

- le [fichier de conception](./docs/conception.md)
- la [FAQ utilisateur](./docs/faq/faq.md)

## Problèmes courants


> je viens de merger une branche et des components existants ne sont plus trouvés

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