# backv4: Switching between Oracle and SQLite for Local Testing

This module supports running against Oracle (default) or SQLite (for local testing/dev). SQLite DB files are created in `backv4/sqlite/` and the schema/data are managed by Hibernate.

## Hibernate config selection

- Default: Oracle config `hibernate.cfg.xml` is used when `HIBERNATE_CFG` is not set.
- Override via system property or env var:
  - System property: `-DHIBERNATE_CFG=hibernate-sqlite-USUARIODEV.cfg.xml`
  - Environment variable: `HIBERNATE_CFG=hibernate-sqlite-USUARIODEV.cfg.xml`

SQLite configs available under `src/main/resources/`:
- `hibernate-sqlite-USUARIODEV.cfg.xml`
- `hibernate-sqlite-USUARIOUAT.cfg.xml`
- `hibernate-sqlite-USUARIO.cfg.xml`

Each points to an absolute DB path under `${user.dir}/backv4/sqlite/` so app and tests write/read the same files.

## Maven profiles

Convenience profiles to set `HIBERNATE_CFG` for tests:
- Dev:  `-Psqlite-dev`  -> `hibernate-sqlite-USUARIODEV.cfg.xml`
- UAT:  `-Psqlite-uat`  -> `hibernate-sqlite-USUARIOUAT.cfg.xml`
- Prod: `-Psqlite-prod` -> `hibernate-sqlite-USUARIO.cfg.xml`

Examples:
- Run full test suite with SQLite DEV:
  ```bash
  mvn -f backv4 -Psqlite-dev test
  ```
- Run a single test with SQLite UAT and stream output:
  ```bash
  mvn -f backv4 -Psqlite-uat -Dtest=com.sources.app.sqlite.SQLiteConnectivityTest -Dsurefire.useFile=false test
  ```
- Use Oracle (default):
  ```bash
  mvn -f backv4 test
  ```

## Where the SQLite databases are

- Files are created here: `backv4/sqlite/`
  - `USUARIODEV.sqlite`
  - `USUARIOUAT.sqlite`
  - `USUARIO.sqlite`

Verify data (requires `sqlite3`):
```bash
awk 'BEGIN{print "DEV:"}'; sqlite3 backv4/sqlite/USUARIODEV.sqlite "SELECT COUNT(*) FROM USERS;"
awk 'BEGIN{print "UAT:"}'; sqlite3 backv4/sqlite/USUARIOUAT.sqlite "SELECT COUNT(*) FROM USERS;"
awk 'BEGIN{print "PROD:"}'; sqlite3 backv4/sqlite/USUARIO.sqlite "SELECT COUNT(*) FROM USERS;"
```

## Notes
- We excluded `ServiceApproval` from SQLite mappings because it uses SEQUENCE, unsupported by SQLite.
- Schema is managed with `hibernate.hbm2ddl.auto=update` in the SQLite configs.
- The test `com.sources.app.sqlite.SQLiteConnectivityTest` inserts a sample user and verifies via JDBC.

## Guía rápida (ES)

Comandos útiles para ejecutar pruebas bajo Oracle (por defecto) y perfiles SQLite.

- __Suite completa con Oracle (por defecto)__
  ```bash
  mvn -f backv4 test
  ```

- __Suite completa con SQLite DEV/UAT/PROD__
  ```bash
  mvn -f backv4 -Psqlite-dev test
  mvn -f backv4 -Psqlite-uat test
  mvn -f backv4 -Psqlite-prod test
  ```

- __Un test específico__
  ```bash
  # Oracle (por defecto)
  mvn -f backv4 -Dtest=com.sources.app.entities.TransactionPolicyTest test

  # SQLite DEV
  mvn -f backv4 -Psqlite-dev -Dtest=com.sources.app.sqlite.SQLiteConnectivityTest test
  ```

- __Un método específico dentro de un test__
  ```bash
  mvn -f backv4 -Dtest=NombreDeClaseTest#metodoEspecifico test
  # Ejemplo
  mvn -f backv4 -Dtest=com.sources.app.entities.TransactionPolicyTest#settersAndGetters_WorkCorrectly test
  ```

- __Ver logs de pruebas directamente en consola__
  ```bash
  mvn -f backv4 -Dsurefire.useFile=false test
  # Combinable con perfiles y -Dtest
  mvn -f backv4 -Psqlite-dev -Dtest=com.sources.app.sqlite.SQLiteConnectivityTest -Dsurefire.useFile=false test
  ```

- __Nota sobre exclusiones en SQLite__
  - En perfiles `sqlite-dev`, `sqlite-uat` y `sqlite-prod`, se excluyen automáticamente:
    - `**/ServiceApprovalTest.java`
    - `**/ServiceApprovalDAOTest.java`
  - Motivo: estas pruebas dependen de SEQUENCEs de Oracle, no soportadas por SQLite.
