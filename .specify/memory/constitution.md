# CreativeLab Backend Java Constitution

<!--
  This constitution is the authoritative source of engineering rules for the
  CreativeLab multi-module Spring Boot backend. It is derived from CLAUDE.md and
  MUST be consulted before any spec, plan, task list, or implementation.
  All other guidance documents are subordinate to this file.
-->

## Core Principles

### I. Hexagonal Architecture (Ports & Adapters) — NON-NEGOTIABLE

Every service module MUST follow the same hexagonal package layout under its root package (`top.creativelab.user`, `top.creativelab.role`):

- `domain/` — plain Java aggregates. No JPA or Spring annotations (Lombok is the only permitted annotation). Invariants are enforced in constructors and methods by throwing `IllegalArgumentException`. Identifiers are typed value objects in `domain/.../vo/` (e.g. `RoleId`, `UserId`) wrapping a `UUID`, created via `XxxId.newId()`.
- `ports/inbound/` — use-case interfaces (e.g. `RoleUseCase`, `UserManagementUseCase`). Inbound adapters (controllers) depend ONLY on these interfaces, never on concrete services.
- `ports/outbound/` — repository interfaces expressed in domain types (e.g. `RoleRepository`, `UserRepository`). They MUST NOT expose JPA entities or framework types.
- `application(s)/` — `@Service` classes implementing inbound ports; they orchestrate domain objects and outbound ports. Not-found exceptions live in `application(s)/exception(s)/`. Note the intentional package-name difference: `applications` in `access-control`, `application` in `user`.
- `adapters/inbound/rest/<aggregate>/` — `@RestController` plus request/response records. Responses expose a static `XxxResponse.from(domain)` factory. Controllers convert raw `UUID`s into typed IDs before calling use cases.
- `adapters/outbound/persistence/<aggregate>/` — `XxxJpaEntity`, Spring Data `XxxJpaRepository`, and `XxxPersistenceAdapter` (`@Repository`) implementing the outbound port.

Dependency direction is strictly inward: adapters → ports → domain. The domain layer MUST NOT depend on any outer layer.

### II. Domain Purity & Typed Value Objects

- Domain objects MUST NOT leak JPA entities; all entity↔domain conversion happens inside the persistence adapter.
- All aggregate identifiers MUST be typed value objects wrapping `UUID`; raw `UUID`s are converted at the adapter boundary only.
- Business invariants MUST be validated in the domain (constructors/methods), not in controllers or services.
- Aggregates with child collections (e.g. `Role` → `RolePermissionJpaEntity`) MUST be updated by loading the existing JPA entity and mutating it (e.g. `syncPermissions`) rather than constructing a new one, so orphan removal works correctly.

### III. Test-First Development (TDD) — NON-NEGOTIABLE

- Tests MUST be written before implementation. The Red → Green → Refactor cycle is mandatory for every new behavior and every bug fix.
- A change is not complete until it is covered by automated tests at the appropriate layer:
  - Domain invariants and value objects → unit tests (e.g. `UserTest`, `EmailTest`, `PasswordTest`).
  - Application services → unit tests with mocked outbound ports (e.g. `UserManagementServiceTest`).
  - Persistence adapters → adapter tests (e.g. `UserPersistenceAdapterTest`).
  - REST/GraphQL inbound adapters → controller and schema tests (e.g. `UserControllerTest`, `UserGraphQlSchemaTest`).
- Tests MUST be deterministic and isolated. Integration tests that require infrastructure (Postgres, Redis via Testcontainers) MUST be explicitly identified and MUST NOT be silently disabled without a tracked reason.
- `mvn test` MUST pass before any change is considered mergeable. New code without tests is rejected in review.

### IV. Simplicity First (KISS / YAGNI)

- Prefer the simplest design that satisfies the current requirement. Do not introduce abstractions, layers, configuration, or dependencies for hypothetical future needs.
- No speculative generalization: a pattern is adopted only when at least two concrete cases require it.
- Favor small, focused classes and methods with a single responsibility. If a solution needs a paragraph to explain, it is probably too complex.
- Reuse the existing `common` building blocks (`BasePersistenceAdapter`, `JpaEntityBaseModel`, `PageResult`, `PageResponse`) instead of re-implementing equivalent logic per module.
- Any added complexity MUST be justified in the plan/review; unjustified complexity is a blocking review finding.

### V. Schema-as-Code & Persistence Isolation

- `spring.jpa.hibernate.ddl-auto=validate` — Hibernate NEVER creates or alters tables.
- Every schema change MUST be delivered as a Flyway migration in `<module>/src/main/resources/db/migration/` named `V<n>__description.sql`.
- JPA entities MUST match the migrations exactly; a mismatch is a build/runtime failure and MUST be fixed, not worked around.
- The shared persistence base lives in `common/src/main/java/persistance/` (note the spelling, no `top.creativelab` prefix):
  - `JpaEntityBaseModel` — `@MappedSuperclass` with a `UUID id` assigned by the domain (not generated) and `createdAt`/`updatedAt` via JPA auditing. Apps MUST keep `@EnableJpaAuditing`.
  - `BasePersistenceAdapter<D, E, ID>` — generic `save/findAll/findById/isExistById/deleteById` over a `JpaRepository<E, ID>`; subclasses implement `mapToJpaEntity` and `mapToDomain`. Because ports use typed IDs, adapters add overloads that unwrap `.getValue()`. Custom queries cast `repository` to the concrete `XxxJpaRepository`.

### VI. Module Independence & Shared Common

- Each service module (`user`, `access-control`) is an independent Spring Boot application with its own PostgreSQL database. Modules share code ONLY through `common`.
- Cross-module coupling MUST NOT be introduced via direct class references; integration happens over HTTP/GraphQL contracts.
- `common` MUST remain a library (no application entry point) and MUST NOT depend on any service module.
- Build order matters: `common` MUST be installed before services resolve it (`mvn clean install` from the repo root).

### VII. Native-Ready, Environment-Driven Deployment

- Services are deployed as GraalVM native images via `Dockerfile.user` and `Dockerfile.access-control` (root-context, `linux/amd64`). Build with `docker build -f Dockerfile.user .` from the repo root.
- New reflection-dependent code MUST be registered in the module's `META-INF/native-image/.../reachability-metadata.json` and/or `NativeRuntimeHints`.
- Runtime configuration MUST be environment-driven (`SPRING_DATASOURCE_*`, `DB_POOL_SIZE`, `FLYWAY_CONNECT_RETRIES`, `GRAPHIQL_ENABLED`, `SPRINGDOC_ENABLED`). Secrets (e.g. passwords) MUST be supplied as Worker secrets, never committed.
- Routing is defined in `cloudflare/src/index.ts` and container classes in `wrangler.jsonc`; changes to routes or ports MUST be reflected there and documented in `DEPLOYMENT.md`.

## Technology & Platform Constraints

- **Language/Runtime**: Java 25.
- **Framework**: Spring Boot 4.1.1 (parent POM), Lombok.
- **Build**: Multi-module Maven aggregator at the repo root; use the system `mvn` (per-module `mvnw` wrappers exist but the root aggregator is the normal entry point).
- **Modules**: `user` (`top.creativelab.user.UserApplication`, port 8081, `user_db`, also requires Redis), `access-control` (`top.creativelab.role.AccessControlApplication`, port 8082, `access_control_db`), `common` (library, no app).
- **Local infrastructure**: PostgreSQL at `localhost:5432` with `postgres`/`postgres`.
- **Standard commands**:
  - `mvn clean install` — build all modules.
  - `mvn -pl access-control -am spring-boot:run` / `mvn -pl user -am spring-boot:run` — run one service (`-am` builds `common` too).
  - `mvn test` — all tests.
  - `mvn -pl access-control -am test -Dtest=RoleApplicationTests -Dsurefire.failIfNoSpecifiedTests=false` — single test class.
- **Domain model (access-control)**: RBAC — `Resource` + `Action` → `Permission`; `Role` holds a set of `PermissionId`s (join table via `RolePermissionJpaEntity`). `RoleService.update` silently drops permission IDs that do not exist.

## Development Workflow & Quality Gates

1. **Specify** the change and confirm it complies with this constitution before planning.
2. **Plan** the design; explicitly justify any added complexity (Principle IV) and list the tests to be written first (Principle III).
3. **Write failing tests** that encode the required behavior.
4. **Implement** the minimal code to make tests pass, respecting the hexagonal boundaries (Principle I).
5. **Refactor** for clarity without changing behavior.
6. **Verify**: `mvn test` passes; schema changes have matching Flyway migrations; native metadata is updated when reflection is introduced.
7. **Review**: every PR/review MUST verify compliance with all principles. Violations are blocking.

## Governance

- This constitution supersedes all other engineering practices and guidance documents. `CLAUDE.md` is the descriptive source of architecture; this file is the normative rule set derived from it.
- Amendments require a documented rationale, review approval, and a migration plan for any existing code that violates the new rule.
- Versioning follows semantic versioning: MAJOR for principle removals/redefinitions, MINOR for new principles or material expansions, PATCH for clarifications and wording.
- All code reviews MUST confirm compliance; any deviation MUST be recorded and time-boxed with a remediation task.
- When this constitution and an implementation disagree, the constitution wins and the implementation MUST be corrected.

**Version**: 1.0.0 | **Ratified**: 2026-11-18 | **Last Amended**: 2026-11-18
