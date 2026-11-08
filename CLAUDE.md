# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Multi-module Maven project (Spring Boot 4.1.1, Java 25, Lombok). Each service module is an independent Spring Boot app with its own PostgreSQL database; modules share code only through `common`.

| Module | Main class | Port | Database |
|---|---|---|---|
| `user` | `top.creativelab.user.UserApplication` | 8081 | `user_db` |
| `access-control` | `top.creativelab.role.AccessControlApplication` | 8082 | `access_control_db` |
| `common` | (library, no app) | — | — |

Both apps expect local Postgres at `localhost:5432` (user/pass `postgres`/`postgres`). `user` also depends on Redis.

## Commands

Run from repo root with the system `mvn` (the per-module `mvnw` wrappers exist but the root aggregator is the normal entry point).

```bash
mvn clean install                                   # build all modules (common must be installed before services resolve it)
mvn -pl access-control -am spring-boot:run          # run one service (-am builds common too)
mvn -pl user -am spring-boot:run
mvn test                                            # all tests
mvn -pl access-control -am test -Dtest=RoleApplicationTests -Dsurefire.failIfNoSpecifiedTests=false   # single test class
mvn -pl access-control -am test -Dtest='RoleApplicationTests#contextLoads' -Dsurefire.failIfNoSpecifiedTests=false
```

`user` has `TestUserApplication` (src/test) which boots the app with a Testcontainers Redis — requires Docker. `UserApplicationTests` is currently `@Disabled`. `RoleApplicationTests` is a full `@SpringBootTest` and needs the real Postgres DB.

## Architecture: hexagonal (ports & adapters)

Every service follows the same package layout under its root package (`top.creativelab.user`, `top.creativelab.role`):

- `domain/` — plain Java aggregates (no JPA/Spring annotations except Lombok). Invariants enforced in constructors/methods (throw `IllegalArgumentException`). IDs are typed value objects in `domain/.../vo/` (`RoleId`, `UserId`, ...) wrapping a `UUID`, created via `XxxId.newId()`.
- `ports/inbound/` — use-case interfaces (`RoleUseCase`, `CreateUserUseCase`). Controllers depend only on these.
- `ports/outbound/` — repository interfaces expressed in domain types (`RoleRepository`, `UserRepository`).
- `application(s)/` — `@Service` classes implementing inbound ports; they orchestrate domain + outbound ports. Not-found exceptions live in `application(s)/exception(s)/`. Note package name differs: `applications` in access-control, `application` in user.
- `adapters/inbound/rest/<aggregate>/` — `@RestController` + request/response records. Responses use a static `XxxResponse.from(domain)` factory. Controllers convert raw `UUID`s into typed IDs before calling use cases.
- `adapters/outbound/persistence/<aggregate>/` — `XxxJpaEntity`, Spring Data `XxxJpaRepository`, and `XxxPersistenceAdapter` (`@Repository`) that implements the outbound port.

### Persistence pattern (`common` module)

`common/src/main/java/persistance/` (note spelling, no `top.creativelab` prefix):

- `JpaEntityBaseModel` — `@MappedSuperclass` with `UUID id` (assigned by domain, not generated), `createdAt`/`updatedAt` via JPA auditing. Apps must keep `@EnableJpaAuditing`.
- `BasePersistenceAdapter<D, E, ID>` — generic adapter providing `save/findAll/findById/isExistById/deleteById` over a `JpaRepository<E, ID>`; subclasses implement `mapToJpaEntity` and `mapToDomain`. Ports take typed IDs, so adapters add overloads that unwrap `.getValue()`. Custom queries cast `repository` to the concrete `XxxJpaRepository`.

Domain objects never leak JPA entities; all conversion happens in the persistence adapter. For updates of aggregates with child collections (e.g. Role → `RolePermissionJpaEntity`), the adapter loads the existing JPA entity and mutates it (`syncPermissions`) rather than building a new one, so orphan removal works.

### Schema

`spring.jpa.hibernate.ddl-auto=validate` — Hibernate never creates tables. Schema changes must be added as Flyway migrations in `<module>/src/main/resources/db/migration/` (`V<n>__description.sql`), and JPA entities must match them exactly.

### access-control domain

RBAC model: `Resource` + `Action` → `Permission`; `Role` holds a set of `PermissionId`s (join table via `RolePermissionJpaEntity`). `RoleService.update` silently drops permission IDs that don't exist.

## Deployment (Cloudflare Containers)

- `Dockerfile.user` and `Dockerfile.access-control` are root-context, `linux/amd64` GraalVM native builds. Build with `docker build -f Dockerfile.user .` from the repo root.
- `wrangler.jsonc` defines two container classes (`UserServiceContainer` on 8081, `AccessControlContainer` on 8082) and the Worker gateway.
- `cloudflare/src/index.ts` routes `/users`, `/user-roles`, `/graphql`, `/v3/api-docs`, `/swagger-ui` to `user`; `/acc-ctl-*` to `access-control`; `/health*` to Actuator.
- Runtime config is environment-driven: `SPRING_DATASOURCE_*`, `DB_POOL_SIZE`, `FLYWAY_CONNECT_RETRIES`, `GRAPHIQL_ENABLED`, `SPRINGDOC_ENABLED`. Passwords are Worker secrets.
- Full provisioning/deploy/verification steps are in `DEPLOYMENT.md`.
