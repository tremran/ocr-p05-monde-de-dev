# MDD API

## install

- clone project
- create mysql database

```sql
-- connect to your mysql db
-- create a database
create DATABASE p05_mdd;
-- create a dedicated user
create USER 'mdd_user'@'localhost' IDENTIFIED BY 'mdd_pwd';
GRANT ALL PRIVILIEGES ON p05_mdd.* TO 'mdd_user'@'localhost';
-- exit mysql
exit
```
- create tables
```bash
mysql -u mdd_user -p p05_mdd < ./schema.sql
```

- run app `mvn spring-boot:run`
- run tests ` ./mvnw test`

## Swagger / OpenAPI

Une fois l'application démarrée sur le port `3001`:

- Swagger UI: `http://localhost:3001/swagger-ui.html`
- OpenAPI JSON: `http://localhost:3001/v3/api-docs`

Les endpoints protégés utilisent un JWT Bearer.
Dans Swagger UI, cliquer sur `Authorize` puis saisir:

```text
Bearer <votre_token_jwt>
```

## TODO

- add DTO
    - remove password from user DTO