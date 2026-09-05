## Plan: Direct OAuth Across WebAPI, Report1, and E2E

Move authentication ownership into svc_webapi using Azure AD B2C JWT validation, preserve a temporary dual-mode bridge for legacy X-MS headers, propagate user identity to fun_report1, and add locally signed JWT support for e2e so roles can be tested without external IdP dependency. This minimizes production risk while enabling role-aware authorization end-to-end.

**Steps**
1. Phase 1 - Foundation and compatibility guardrails
2. Add auth feature flags in svc_webapi configuration:
3. Add flag to allow legacy header auth during migration (default true in prod for transition, false target-state).
4. Add flag to require JWT for protected endpoints once migration completes.
5. Define one principal extraction utility in svc_webapi to normalize current AuthenticationToken and Spring JWT authentication into a single internal principal model (id, email, roles).
6. Replace direct casts to AuthenticationToken in GraphQL and report flows with the principal utility so both auth modes are supported safely.
7. Phase 2 - Enable direct OAuth in svc_webapi
8. Enable Spring OAuth2 resource server dependency and configuration.
9. Update WebSecurityConfig to register OAuth2 resource server JWT handling while keeping legacy header filter path behind the migration flag.
10. Add JWT-to-authorities converter mapping roles claim into GrantedAuthority entries.
11. Ensure JWT issuer/JWK/audience are environment-driven for B2C in production.
12. Add explicit unauthorized/forbidden behavior and consistent audit logging fields (subject/email/roles/auth-mode).
13. Phase 3 - Keep legacy bridge during rollout (parallel with Phase 2 verification)
14. Refactor CustomAuthenticationFilter to execute only when legacy-header mode is enabled and no valid JWT authentication already exists.
15. Keep existing X-MS header test helpers operational under dual mode for short-term compatibility.
16. Publish migration timeline to disable legacy-header mode after e2e and staging sign-off.
17. Phase 4 - Propagate user identity and enforce in fun_report1
18. Propagate inbound Authorization bearer token from svc_webapi to fun_report1 calls (in addition to existing X-Report1-Secret).
19. Optionally propagate normalized identity headers from svc_webapi (email/id/roles) for diagnostics only; do not trust them as primary auth.
20. Implement JWT validation in fun_report1 using B2C issuer/JWK/audience settings and roles claim extraction.
21. Keep shared-secret validation as defense-in-depth.
22. Add role guard dependency in fun_report1 endpoints for report generation permissions.
23. Add structured audit logging in fun_report1 for authenticated user and role decisions.
24. Phase 5 - Local JWT lane for e2e tests
25. Add local JWT profile in svc_webapi (issuerLocal) with deterministic test validation strategy and explicit test-only secret/source.
26. Add pytest token factory in e2e tests to mint locally signed JWTs containing email and roles claims.
27. Add e2e request path that sends generated JWT to GraphQL endpoints for auth validation scenarios.
28. Keep existing UI smoke login path during transition; add a dedicated JWT-auth e2e suite first, then optionally migrate UI flow to consume generated tokens.
29. Add negative e2e cases: expired token, missing roles, malformed token.
30. Phase 6 - Hardening and cutover
31. Disable legacy header mode in non-local environments after successful rollout.
32. Remove dead/commented auth config and obsolete assumptions in docs.
33. Re-run full service and e2e verification, then promote.

**Relevant files**
- /workspaces/sin-net-reports/svc_webapi/host/pom.xml — enable OAuth2 resource server dependency.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/web/WebSecurityConfig.java — configure JWT auth and dual-mode filter ordering.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/web/CustomAuthenticationFilter.java — gate legacy header auth by feature flag and precedence.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/web/AuthenticationToken.java — compatibility token for legacy mode.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/infra/adapters/gql/Query.java — replace direct token casting with principal resolver.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/infra/adapters/gql/Mutation.java — replace direct token casting with principal resolver.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/app/flow/reports/Report2Flow.java — consume normalized principal.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/app/flow/reports/Report1Flow.java — consume normalized principal for downstream calls.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/java/sinnet/infra/adapters/fun/Report1FunctionAdapter.java — forward bearer token and diagnostics headers.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/resources/application.properties — auth flags and default security properties.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/resources/resourceserver-issuerB2C.properties — B2C issuer/JWK config.
- /workspaces/sin-net-reports/svc_webapi/host/src/main/resources/resourceserver-issuerLocal.properties — local e2e token profile config.
- /workspaces/sin-net-reports/fun_report1/src/auth.py — keep shared secret and add JWT auth dependencies.
- /workspaces/sin-net-reports/fun_report1/src/app.py — apply JWT+role dependencies to report endpoints.
- /workspaces/sin-net-reports/fun_report1/requirements.txt — JWT validation dependencies.
- /workspaces/sin-net-reports/fun_report1/local.settings.json.example — local auth environment knobs for development/testing.
- /workspaces/sin-net-reports/e2e_tests/e2e/conftest.py — local JWT factory fixtures.
- /workspaces/sin-net-reports/e2e_tests/e2e/step_defs/test_health_check_steps.py — attach generated JWTs to test requests and scenarios.
- /workspaces/sin-net-reports/e2e_tests/k8s/webapi.yaml — enable local issuer profile and test JWT secret wiring for local stack.
- /workspaces/sin-net-reports/app_time/src/store/session/reducers.ts — optional later change if UI login flow is migrated to real JWTs.
- /workspaces/sin-net-reports/app_time/src/api/api.ts — optional later change if UI test flow sends generated JWT instead of placeholder token.
- /workspaces/sin-net-reports/infra/shared/module_container_app_webapi/main.tf — production env wiring for B2C auth and migration flag.
- /workspaces/sin-net-reports/infra/shared/module_fun_report1/main.tf — JWT settings for fun_report1 and secret management.

**Verification**
1. Build and unit/integration checks for webapi auth path:
2. Maven build and tests for svc_webapi host including authentication-related tests.
3. Verify both auth modes during transition:
4. Request with valid JWT succeeds and principal/roles are extracted.
5. Request with legacy X-MS headers succeeds only when legacy mode is enabled.
6. Request with invalid/expired JWT returns 401.
7. fun_report1 verification:
8. Direct call with correct shared secret but invalid JWT returns 401.
9. Direct call with valid shared secret + valid JWT + required role returns report link.
10. Role-missing token returns 403.
11. e2e verification:
12. Smoke UI suite still passes in transition mode.
13. New JWT-auth e2e scenarios pass with locally minted tokens and role-specific assertions.
14. Negative e2e scenarios fail with expected HTTP status and error shape.
15. Local stack verification via setup-k3d and pytest smoke/integration markers.

**Decisions**
- Identity provider: Azure AD B2C is retained for production.
- Migration strategy: dual mode (JWT + legacy headers) with feature flag.
- fun_report1 strategy: validate JWT directly and use roles claim for authorization decisions.
- e2e local auth: locally signed JWTs first (no mandatory local OAuth server initially).
- Roles source claim: roles.
- Included scope: svc_webapi direct OAuth, fun_report1 JWT recognition, local JWT e2e lane, migration safety controls.
- Excluded for now: introducing Keycloak/local OAuth server, frontend UI login full rewrite in this first iteration.

**Further Considerations**
1. Audience claim value must be finalized for both svc_webapi and fun_report1 validators (recommended: one shared API audience constant per environment).
2. Decide whether fun_report1 should trust only JWT from svc_webapi origin or also accept operator calls with JWT directly (recommended: keep both, constrained by shared secret + JWT).
3. Decide cutoff date for disabling legacy header mode in production (recommended: after two successful release cycles with JWT telemetry showing no legacy traffic).