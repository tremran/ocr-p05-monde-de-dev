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
```

- run app `mvn spring-boot:run`