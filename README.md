# Hostel Expense Tracker — Backend

Spring Boot API for the hostel expense tracker. The Angular app is in `F:\grok\hostel-expense-tracker`.

Configuration uses **`.properties` files** (not YAML). Persistence is **JPA / Hibernate**. The only JDBC driver in the project is **SQLite**. Entities do not change if you later add a different driver.

## Config files

| File | In git? | Role |
|------|---------|------|
| `src/main/resources/application.properties` | yes | Shared defaults (SQLite file `./data/hostel.db`) |
| `application-local.properties.example` | yes | Template for your machine |
| `application-local.properties` | **no** (gitignored) | Your environment: DB URL, JWT secret, seed passwords |
| `docker-compose.yml` | yes | Optional Postgres for later, if you install Docker |

```bash
copy application-local.properties.example application-local.properties
```

Then edit `application-local.properties`. The app imports that file automatically when you run it (dev profile).

## Run

```bash
cd F:\grok\hostel-expense-tracker-BE
mvn spring-boot:run
```

API: `http://localhost:8080`

**Hot reload:** `spring-boot-devtools` restarts the app when classpath classes change. Run with `mvn spring-boot:run` (or the IDE Spring Boot run). In the IDE, turn on Build automatically / compile on save so a file save triggers the restart. DevTools is not packaged into the production jar.

Default logins (override in `application-local.properties`):

| Username | Password   | Role  |
|----------|------------|-------|
| admin    | admin123   | ADMIN |
| viewer   | viewer123  | USER  |

## Switch database later

Hibernate is the ORM — do not add extra database engines “just in case”. To move off SQLite:

1. Add **one** JDBC driver to `pom.xml` (for example `org.postgresql:postgresql`).
2. Change `spring.datasource.url` / username / password in `application-local.properties`.
3. Hibernate detects the dialect for most engines.

`docker-compose.yml` is still in the repo if you later run Postgres in Docker. It is unused until you add the Postgres driver and point the properties at it.

## Auth

`POST /api/auth/login` `{ "username", "password" }` → `{ accessToken, expiresAt, user }`

Send `Authorization: Bearer <token>` on every other `/api/**` request.

`USER` can `GET` only. `ADMIN` can create / update / delete.

## Import existing localStorage data

Logged in as admin:

`POST /api/admin/import` with the `hostel-expense-tracker-data-v1` JSON object (`residents`, `months`, `payments`, `expenses`, `customCategories`).
