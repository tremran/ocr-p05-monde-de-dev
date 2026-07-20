# Cypress E2E

## Prérequis

- API backend lancée (par défaut `http://localhost:3001`)
- Front Angular lancé (par défaut `http://localhost:4200`)

## Lancer les tests

```bash
npm run test:e2e
```

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
