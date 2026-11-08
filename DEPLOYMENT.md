# Deploying to Cloudflare Containers

This repository now contains the deployment layer for running the two Spring
Boot services on Cloudflare Containers:

```text
Client
  │
  ▼
Cloudflare Worker  (creativelab-api)
  ├── /users, /user-roles, /graphql, /v3/api-docs, /swagger-ui
  │      └── UserServiceContainer        :8081  → user_db
  ├── /acc-ctl-role, /acc-ctl-permission,
  │   /acc-ctl-resource, /acc-ctl-action
  │      └── AccessControlContainer      :8082  → access_control_db
  ├── /health                    → gateway liveness (does not wake a container)
  ├── /health/user               → user /actuator/health
  └── /health/access-control     → access-control /actuator/health
```

Cloudflare Containers are only reachable through the Worker binding. The
Worker is the public entry point, the CORS layer, and the place where
authentication must be enforced.

---

## 1. Prerequisites

- A Cloudflare account on the **Workers Paid** plan (Containers is billed).
- Node.js 20+ and npm.
- Docker Desktop running locally. `wrangler deploy` builds the images with the
  local Docker daemon.
- Wrangler authenticated: `npx wrangler login`.
- Two PostgreSQL databases reachable from the public internet (Neon, Supabase,
  Aiven, RDS, …). Cloudflare does not host PostgreSQL; D1 is SQLite and is not
  a drop-in replacement for these services.
- JDBC URLs must use TLS, for example
  `jdbc:postgresql://host:5432/user_db?sslmode=require`.

---

## 2. Provision the databases

Create two databases and two least-privilege users:

| Service | Database | User |
|---|---|---|
| `user` | `user_db` | `user_service` |
| `access-control` | `access_control_db` | `access_control_service` |

The users need DDL privileges because Flyway runs on application startup.
`gen_random_uuid()` is used by the migrations and is built into PostgreSQL 13+.

If you prefer to run migrations outside the container, set
`SPRING_FLYWAY_ENABLED=false` for the service and run Flyway from a release
job instead.

---

## 3. Configure the deployment

Edit [`wrangler.jsonc`](wrangler.jsonc) and replace the placeholders:

```jsonc
"vars": {
  "USER_DB_URL": "jdbc:postgresql://<host>/user_db?sslmode=require",
  "USER_DB_USERNAME": "user_service",
  "ACCESS_CONTROL_DB_URL": "jdbc:postgresql://<host>/access_control_db?sslmode=require",
  "ACCESS_CONTROL_DB_USERNAME": "access_control_service",
  "CORS_ALLOWED_ORIGIN": "https://your-frontend.example.com",
  "GRAPHIQL_ENABLED": "false",
  "SPRINGDOC_ENABLED": "false"
}
```

Store passwords and the optional auth token as Worker secrets:

```bash
npx wrangler secret put USER_DB_PASSWORD
npx wrangler secret put ACCESS_CONTROL_DB_PASSWORD
npx wrangler secret put AUTH_TOKEN          # optional
```

The Worker injects these values into the containers as
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and
`SPRING_DATASOURCE_PASSWORD` (see [`cloudflare/src/index.ts`](cloudflare/src/index.ts)).

> Changing a secret does not update a running container. Redeploy, or restart
> the container instance, after rotating credentials.

For local development, copy [`.dev.vars.example`](.dev.vars.example) to
`.dev.vars` and fill in local values. `.dev.vars` is gitignored.

---

## 4. Validate locally

```bash
npm install
npm run typecheck
```

Build and run one service directly:

```bash
docker build -f Dockerfile.user --platform linux/amd64 -t creativelab-user:local .
docker run --rm -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/user_db" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  creativelab-user:local

curl http://localhost:8081/actuator/health
```

Repeat with `Dockerfile.access-control` and port `8082`.

To exercise the Worker and both containers together:

```bash
npm run dev
```

`wrangler dev` builds both images, starts the containers, and serves the Worker
locally. It requires Docker and can take several minutes on the first run.

---

## 5. Deploy

```bash
npx wrangler deploy
```

The first deploy builds both GraalVM native images and pushes them to the
Cloudflare registry. Native-image builds are CPU- and memory-intensive; expect
a long first deploy. If your CI or local machine times out, build the images in
CI and point `containers[].image` at the pushed registry URI instead of a
Dockerfile path.

To pre-build and push manually (useful in CI):

```bash
docker build --platform linux/amd64 -f Dockerfile.user -t creativelab-user:latest .
npx wrangler containers push creativelab-user:latest

docker build --platform linux/amd64 -f Dockerfile.access-control -t creativelab-access-control:latest .
npx wrangler containers push creativelab-access-control:latest
```

`wrangler containers push` prints the registry URI to use in
`containers[].image`. Note that `wrangler containers build <PATH>` expects a
directory containing a file named exactly `Dockerfile`, so use `docker build`
for the two root-level Dockerfiles above.

The Worker is published at:

```text
https://creativelab-api.<your-subdomain>.workers.dev
```

---

## 6. Verify

```bash
curl https://creativelab-api.<your-subdomain>.workers.dev/health
curl https://creativelab-api.<your-subdomain>.workers.dev/health/user
curl https://creativelab-api.<your-subdomain>.workers.dev/health/access-control

curl https://creativelab-api.<your-subdomain>.workers.dev/users
curl https://creativelab-api.<your-subdomain>.workers.dev/acc-ctl-role
```

Stream logs:

```bash
npx wrangler tail
```

The first request after `sleepAfter` (15 minutes of inactivity) pays the cold
start: image pull plus Spring Boot native startup.

---

## 7. Operations

### Scaling

Both container classes currently use a single Durable Object ID
(`idFromName("singleton")`) and `max_instances: 1`. That is intentional: it
gives predictable behavior and avoids Flyway running concurrently.

To scale a stateless service, shard requests across multiple Durable Object IDs
(for example, hash a user ID or use a random ID per request) and raise
`max_instances`. Each ID maps to one container instance; Cloudflare does not
load-balance across IDs for you.

### Instance size

`basic` gives 1/4 vCPU and 1 GiB of memory. A native Spring Boot service should
fit, but if you see OOM exits or slow cold starts, switch to `standard-1`
(1/2 vCPU, 4 GiB) in [`wrangler.jsonc`](wrangler.jsonc).

### Rollback

```bash
npx wrangler deployments list
npx wrangler rollback
```

### Database connections

Hikari is capped at 5 connections per service by default
(`DB_POOL_SIZE`). Lower it if your managed Postgres plan has a small connection
limit. Raise it only after checking the database-side cap.

---

## 8. Security checklist

- The Spring services permit all requests; the Worker is the only gate. Set
  `AUTH_TOKEN` or replace the shared-secret check in
  [`cloudflare/src/index.ts`](cloudflare/src/index.ts) with real JWT validation
  against your identity provider's JWKS before exposing the API publicly.
- Keep `GRAPHIQL_ENABLED=false` and `SPRINGDOC_ENABLED=false` in production.
- Set `CORS_ALLOWED_ORIGIN` to the exact frontend origin, not `*`.
- Use least-privilege database users and rotate passwords with
  `wrangler secret put`.
- Add Cloudflare WAF/rate-limiting rules in front of the Worker if the API is
  public.
- Do not commit `.dev.vars`, database passwords, or `AUTH_TOKEN`.

---

## 9. Troubleshooting

| Symptom | Likely cause / fix |
|---|---|
| `Missing required environment variable` | A `vars` entry or secret is missing. Check `wrangler.jsonc` and `wrangler secret list`. |
| Container exits during startup | Database unreachable, wrong TLS mode, bad credentials, or Flyway failure. Check `wrangler tail` and the container logs. |
| `502`/`503` from the Worker | Container is still starting or crashed. Check `/health/user` and logs; increase `sleepAfter` or the instance type. |
| Image build fails on Apple Silicon | The Dockerfiles pin `linux/amd64`; ensure Docker BuildKit is enabled and allow emulation. |
| `wrangler deploy` takes very long | Expected for GraalVM native builds. Build images in CI and reference the pushed image. |
| `too many connections` from Postgres | Lower `DB_POOL_SIZE` and check the database plan's connection cap. |
| Flyway migration race | Keep `max_instances: 1`, or run migrations as a separate job and disable startup migrations. |

---

## 10. Files added for deployment

| File | Purpose |
|---|---|
| [`Dockerfile.user`](Dockerfile.user) | amd64 GraalVM native image for the `user` service |
| [`Dockerfile.access-control`](Dockerfile.access-control) | amd64 GraalVM native image for the `access-control` service |
| [`wrangler.jsonc`](wrangler.jsonc) | Worker, container classes, bindings, migrations, vars |
| [`cloudflare/src/index.ts`](cloudflare/src/index.ts) | Gateway Worker, routing, CORS, health, optional auth |
| [`package.json`](package.json) | Wrangler/TypeScript tooling |
| [`tsconfig.json`](tsconfig.json) | Worker typechecking |
| [`.dev.vars.example`](.dev.vars.example) | Local Worker/container environment template |
