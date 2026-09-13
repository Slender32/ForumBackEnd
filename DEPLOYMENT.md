# Deployment

The cloud deployment is managed by one Compose file: `docker-compose.deploy.yml`.
Keep `ForumBackEnd`, `ForumWebSite`, and `ForumAdmin` as sibling directories under
the same workspace directory. The production build is offline: it only needs
these artifacts and never downloads Gradle or npm dependencies on the server:

- `ForumBackEnd/app.jar`
- `ForumWebSite/dist/index.html` and its assets
- `ForumAdmin/dist/index.html` and its assets

Configure production secrets in `ForumBackEnd/.env`, then deploy from the backend
directory:

```sh
sh deploy.sh
```

The script validates all artifacts, builds the runtime images, starts all four
services, and checks the website, administrator console, and public APIs. Only
Nginx publishes a host port (`80`). PostgreSQL, Redis, and Spring Boot are
reachable only through the Compose network.
Demo data seeding is explicitly disabled in this persistent deployment so an
application restart cannot insert the same fixture rows twice.

All four containers belong to the `forumbackend` Compose project. The database
and Redis use the fixed volumes `forumbackend_postgres_data` and
`forumbackend_redis_data`. Never use `docker compose down -v` in production.

For a new release, build the backend jar and both frontends locally, synchronize
the three artifacts, then run `sh deploy.sh`. `ForumWebSite/Dockerfile.runtime`
packages both frontend distributions with Nginx; `ForumBackEnd/Dockerfile.runtime`
packages the jar with the JRE.

The website is served at `/`, the administrator console at `/console/`, and both
clients reach Spring Boot through `/api/`. Nginx resolves the backend by the
Compose service name `app`.

Run the authenticated, read-only administrator checks from `ForumAdmin`:

```powershell
$env:API_CHECK_URL='http://8.163.82.216/api'
$env:ADMIN_EMAIL='<administrator email>'
$env:ADMIN_PASSWORD='<administrator password>'
npm run check:admin

$env:E2E_BASE_URL='http://8.163.82.216'
npm run test:e2e
```
