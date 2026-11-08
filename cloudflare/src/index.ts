import { Container } from "@cloudflare/containers";

/**
 * Environment bindings for the gateway Worker.
 *
 * Non-secret values live in `wrangler.jsonc` under `vars`. Passwords and the
 * optional shared auth token must be created with `wrangler secret put`.
 */
interface Env {
  USER_SERVICE: DurableObjectNamespace;
  ACCESS_CONTROL_SERVICE: DurableObjectNamespace;

  USER_DB_URL: string;
  USER_DB_USERNAME: string;
  USER_DB_PASSWORD: string;

  ACCESS_CONTROL_DB_URL: string;
  ACCESS_CONTROL_DB_USERNAME: string;
  ACCESS_CONTROL_DB_PASSWORD: string;

  CORS_ALLOWED_ORIGIN?: string;
  GRAPHIQL_ENABLED?: string;
  SPRINGDOC_ENABLED?: string;
  AUTH_TOKEN?: string;
}

function requireEnv(env: Env, name: keyof Env): string {
  const value = env[name];
  if (typeof value !== "string" || value.length === 0) {
    throw new Error(`Missing required environment variable: ${String(name)}`);
  }
  return value;
}

/**
 * Spring Boot service listening on 8081.
 *
 * `envVars` are injected into the container process at start. Changing a
 * secret requires restarting the container instance (redeploy or restart).
 */
export class UserServiceContainer extends Container<Env> {
  defaultPort = 8081;
  sleepAfter = "15m";

  envVars = {
    SPRING_DATASOURCE_URL: requireEnv(this.env, "USER_DB_URL"),
    SPRING_DATASOURCE_USERNAME: requireEnv(this.env, "USER_DB_USERNAME"),
    SPRING_DATASOURCE_PASSWORD: requireEnv(this.env, "USER_DB_PASSWORD"),
    GRAPHIQL_ENABLED: this.env.GRAPHIQL_ENABLED ?? "false",
    SPRINGDOC_ENABLED: this.env.SPRINGDOC_ENABLED ?? "false",
  };
}

/**
 * Spring Boot service listening on 8082.
 */
export class AccessControlContainer extends Container<Env> {
  defaultPort = 8082;
  sleepAfter = "15m";

  envVars = {
    SPRING_DATASOURCE_URL: requireEnv(this.env, "ACCESS_CONTROL_DB_URL"),
    SPRING_DATASOURCE_USERNAME: requireEnv(this.env, "ACCESS_CONTROL_DB_USERNAME"),
    SPRING_DATASOURCE_PASSWORD: requireEnv(this.env, "ACCESS_CONTROL_DB_PASSWORD"),
  };
}

const ACCESS_CONTROL_PREFIXES = [
  "/acc-ctl-role",
  "/acc-ctl-permission",
  "/acc-ctl-resource",
  "/acc-ctl-action",
];

function isAccessControlPath(pathname: string): boolean {
  return ACCESS_CONTROL_PREFIXES.some(
    (prefix) => pathname === prefix || pathname.startsWith(`${prefix}/`),
  );
}

function corsHeaders(request: Request, env: Env): Headers {
  const headers = new Headers();
  headers.set("Access-Control-Allow-Origin", env.CORS_ALLOWED_ORIGIN ?? "*");
  headers.set("Vary", "Origin");
  headers.set("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS");
  headers.set(
    "Access-Control-Allow-Headers",
    request.headers.get("Access-Control-Request-Headers") ??
      "Content-Type,Authorization,X-User-Id",
  );
  headers.set("Access-Control-Max-Age", "86400");
  return headers;
}

function withCors(response: Response, request: Request, env: Env): Response {
  const headers = new Headers(response.headers);
  corsHeaders(request, env).forEach((value, key) => headers.set(key, value));
  return new Response(response.body, {
    status: response.status,
    statusText: response.statusText,
    headers,
  });
}

/**
 * Optional shared-secret gate. Leave `AUTH_TOKEN` unset to keep the current
 * pass-through behavior; set it to require `Authorization: Bearer <token>`.
 *
 * Replace this with real JWT validation (for example against your IdP's JWKS)
 * before exposing the API publicly.
 */
function rejectUnauthorized(request: Request, env: Env): Response | null {
  if (!env.AUTH_TOKEN) {
    return null;
  }

  const header = request.headers.get("Authorization") ?? "";
  const token = header.startsWith("Bearer ") ? header.slice("Bearer ".length) : "";

  if (token === env.AUTH_TOKEN) {
    return null;
  }

  return new Response(JSON.stringify({ error: "unauthorized" }), {
    status: 401,
    headers: { "Content-Type": "application/json" },
  });
}

function healthRequest(request: Request, path: string): Request {
  const url = new URL(request.url);
  url.pathname = path;
  url.search = "";
  return new Request(url.toString(), {
    method: "GET",
    headers: request.headers,
  });
}

function containerStub(env: Env, pathname: string) {
  return isAccessControlPath(pathname)
    ? env.ACCESS_CONTROL_SERVICE.get(
        env.ACCESS_CONTROL_SERVICE.idFromName("singleton"),
      )
    : env.USER_SERVICE.get(env.USER_SERVICE.idFromName("singleton"));
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);

    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders(request, env) });
    }

    // Gateway liveness: does not wake a container.
    if (url.pathname === "/health") {
      return withCors(
        new Response(JSON.stringify({ status: "ok", service: "gateway" }), {
          headers: { "Content-Type": "application/json" },
        }),
        request,
        env,
      );
    }

    // Per-service readiness: proxies to Spring Boot Actuator.
    if (url.pathname === "/health/user") {
      const response = await env.USER_SERVICE.get(
        env.USER_SERVICE.idFromName("singleton"),
      ).fetch(healthRequest(request, "/actuator/health"));
      return withCors(response, request, env);
    }

    if (url.pathname === "/health/access-control") {
      const response = await env.ACCESS_CONTROL_SERVICE.get(
        env.ACCESS_CONTROL_SERVICE.idFromName("singleton"),
      ).fetch(healthRequest(request, "/actuator/health"));
      return withCors(response, request, env);
    }

    const denied = rejectUnauthorized(request, env);
    if (denied) {
      return withCors(denied, request, env);
    }

    const response = await containerStub(env, url.pathname).fetch(request);
    return withCors(response, request, env);
  },
};
