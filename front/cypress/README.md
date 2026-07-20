# Cypress E2E

## Prérequis

- API backend lancée (par défaut `http://localhost:3001`)
- Le front est réutilisé s'il tourne déjà sur `127.0.0.1:4200`, sinon il est lancé automatiquement

## Lancer les tests

```bash
npm run test:e2e
```

Cette commande exécute Cypress directement si le front répond déjà sur `127.0.0.1:4200`. Sinon, elle démarre `ng serve --host 127.0.0.1 --port 4200`, attend que le front soit disponible, puis exécute Cypress.

## Ouvrir Cypress

```bash
npm run test:e2e:open
```

## Variables utiles

- `CYPRESS_baseUrl` pour surcharger l'URL front
- `CYPRESS_apiUrl` pour surcharger l'URL API (`/api/v1`)

Exemple:

```bash
CYPRESS_baseUrl=http://localhost:4200 CYPRESS_apiUrl=http://localhost:3001/api/v1 npm run test:e2e
```
