# MDD project

This project contains 2 main folders :

- `front` : contains the angular front project
- `back` : contains the java api

## Conception

check the [conception file](./docs/conception.md) for detailed informations about the structure

## Back

### Installation

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

## Front

### Installation

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
