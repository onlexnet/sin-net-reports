## Plan: Direct OAuth Across WebAPI, Report1, and E2E

Move authentication ownership into `svc_webapi` using Azure AD B2C JWT validation, propagate user tokens to `fun_report1`, and add locally signed JWT support for e2e tests. Roll this out in small, independently deployable releases so each service remains compatible with the previous release of the services it talks to.

No runtime feature flag is needed. Keep the existing Container Apps authentication and legacy `X-MS-CLIENT-PRINCIPAL-*` path until the final cutover. Introduce JWT support additively, then enforce it only after the downstream service is ready and telemetry confirms all calls carry valid tokens. Roll back in reverse dependency order.

## Release sequence

### 1. Normalize principal handling in `svc_webapi`

- Add one principal resolver that supports the current `AuthenticationToken` and Spring JWT authentication, returning a consistent internal principal (subject/id, email, roles).
- Update GraphQL query/mutation and report flows to use the resolver instead of directly casting authentication types.
- Preserve the existing authentication behavior; no feature flags or enforcement changes.

**Gate:** existing GraphQL and report behavior passes with the current Container Apps authentication.

**Rollback:** redeploy the previous `svc_webapi` image.

### 2. Add JWT acceptance in `svc_webapi`

- Add Spring OAuth2 resource server JWT validation and map the `roles` claim to authorities.
- Configure B2C issuer, JWK and audience per environment.
- Accept a valid bearer token while continuing to support legacy X-MS headers when no bearer token is supplied.
- An invalid or expired bearer token must return 401; do not fall back to X-MS headers when a bearer token is present but invalid.
- Keep the existing Container Apps authentication enabled.

**Gate:** valid JWT and legacy-header requests both work; invalid/expired JWT requests return 401.

**Rollback:** redeploy the previous image, provided release 8 has not been deployed.

### 3. Add a local JWT test lane

- Configure an `issuerLocal` profile in `svc_webapi` with an explicit test-only signing key/source.
- Add e2e token fixtures that mint locally signed JWTs with subject, email and roles claims.
- Add positive and negative GraphQL authentication cases, including expired, malformed and role-missing tokens.
- Keep the existing UI smoke-login flow unchanged.

**Gate:** JWT e2e cases pass locally without depending on an external identity provider.

**Rollback:** test-only change; no production rollback required.

### 4. Send the B2C access token from the frontend

- Update frontend API requests to send the B2C access token as `Authorization: Bearer`.
- Do not yet require JWT at `svc_webapi`; requests without a bearer token continue through the legacy path.

**Gate:** frontend requests succeed and `svc_webapi` authenticates them as the expected principal.

**Rollback:** redeploy the previous frontend, provided releases 7 and 8 have not been deployed.

### 5. Forward the bearer token to `fun_report1`

- Forward the inbound `Authorization` bearer token from `svc_webapi` to `fun_report1`, alongside the existing `X-Report1-Secret`.
- Do not treat identity/role diagnostic headers as trusted authentication.

**Gate:** verify the expected token reaches `fun_report1` without exposing it in logs.

**Rollback:** redeploy the previous `svc_webapi` image, provided release 7 has not been deployed.

### 6. Observe JWT validity in `fun_report1` without enforcing it

- Add JWT validation using environment-configured B2C issuer, JWK and audience settings.
- When a bearer token is present, validate it and log the validation outcome and relevant non-sensitive identity/role fields.
- Do not reject calls based on JWT absence, invalidity or missing roles yet; retain the existing shared-secret check.
- Never log the raw token.

**Gate:** telemetry shows every report call has a valid token with the expected identity and roles, with no missing or invalid-token calls, for an agreed observation period.

**Rollback:** redeploy the previous `fun_report1` image.

### 7. Require JWT and roles in `fun_report1`

- Require a valid JWT and the required role(s) on report-generation endpoints, in addition to the existing shared-secret check.
- Return 401 for missing/invalid tokens and 403 when required roles are absent.

**Gate:** role-specific positive and negative tests pass in staging; monitor report success and authorization failures.

**Rollback:** roll back `fun_report1` to release 6 first. Only after that may releases 4 or 5 be rolled back.

### 8. Cut over `svc_webapi` and Container Apps authentication

- Require JWTs for protected `svc_webapi` endpoints.
- Remove the legacy X-MS header authentication filter; do not leave a path that trusts caller-supplied X-MS headers.
- Disable Container Apps authentication in the same coordinated release as the `svc_webapi` change.
- Treat the application image and Terraform/configuration change as one rollback unit.

**Gate:** verify valid-token success, missing/invalid-token 401s, authorization 403s, and end-to-end report generation in staging before promotion.

**Rollback:** revert the `svc_webapi` image and Container Apps authentication Terraform/configuration together. Do not roll back only one side of this cutover.

## Rollback rules

1. Roll back newest-first: remove or roll back every later release that depends on a release before rolling that earlier release back.
2. Keep the sender/consumer compatibility direction additive: deploy token sending/forwarding before requiring tokens at the receiver.
3. After release 7, roll `fun_report1` back to release 6 before rolling back the frontend or token forwarding in `svc_webapi`.
4. Release 8 is coupled: always revert the `svc_webapi` image and Container Apps authentication configuration together.
5. Do not disable the legacy header filter independently of the Container Apps authentication cutover. The filter trusts X-MS headers and is safe only while a trusted upstream sets them.

## Relevant files

- `svc_webapi/host/pom.xml` — OAuth2 resource server dependency.
- `svc_webapi/host/src/main/java/sinnet/web/WebSecurityConfig.java` — JWT resource-server configuration and filter ordering.
- `svc_webapi/host/src/main/java/sinnet/web/CustomAuthenticationFilter.java` — legacy header authentication, retained only until cutover.
- `svc_webapi/host/src/main/java/sinnet/web/AuthenticationToken.java` — compatibility token for the existing authentication path.
- `svc_webapi/host/src/main/java/sinnet/infra/adapters/gql/Query.java` — use normalized principal.
- `svc_webapi/host/src/main/java/sinnet/infra/adapters/gql/Mutation.java` — use normalized principal.
- `svc_webapi/host/src/main/java/sinnet/app/flow/reports/Report2Flow.java` — use normalized principal.
- `svc_webapi/host/src/main/java/sinnet/app/flow/reports/Report1Flow.java` — use normalized principal for downstream calls.
- `svc_webapi/host/src/main/java/sinnet/infra/adapters/fun/Report1FunctionAdapter.java` — forward bearer token.
- `svc_webapi/host/src/main/resources/application.properties` — default security properties.
- `svc_webapi/host/src/main/resources/resourceserver-issuerB2C.properties` — production B2C issuer/JWK configuration.
- `svc_webapi/host/src/main/resources/resourceserver-issuerLocal.properties` — local e2e issuer configuration.
- `fun_report1/src/auth.py` — shared-secret and JWT validation.
- `fun_report1/src/app.py` — apply JWT and role checks to report endpoints.
- `fun_report1/requirements.txt` — JWT validation dependencies.
- `fun_report1/local.settings.json.example` — local auth environment settings.
- `e2e_tests/e2e/conftest.py` — local JWT factory fixtures.
- `e2e_tests/e2e/step_defs/test_health_check_steps.py` — JWT-authenticated scenarios, if appropriate for the existing test structure.
- `e2e_tests/k8s/webapi.yaml` — local issuer profile and test-only token configuration.
- `infra/shared/module_container_app_webapi/main.tf` — final Container Apps authentication cutover and B2C configuration.
- `infra/shared/module_fun_report1/main.tf` — JWT validation settings and secret management.
- `app_time/src/store/session/reducers.ts` and `app_time/src/api/api.ts` — inspect only if the current frontend token flow needs changes; no UI login rewrite is in scope.

## Verification

- Run focused `svc_webapi` Maven tests for principal resolution, JWT acceptance, legacy compatibility and cutover enforcement.
- Verify valid JWT authentication and role extraction; requests with no token use legacy auth only before release 8; invalid/expired bearer tokens return 401 without legacy fallback.
- Verify `fun_report1` retains shared-secret behavior in release 6, reports JWT validation telemetry without rejecting calls, and enforces 401/403 behavior in release 7.
- Run local JWT e2e positive and negative cases, plus the existing UI smoke flow.
- Verify end-to-end report generation before and after each enforcement release.
- Validate the final release 8 image/configuration rollback as a coupled operation.

## Decisions

- Production identity provider: Azure AD B2C.
- Migration strategy: staged additive releases and deployment rollback; no runtime auth feature flags.
- `fun_report1`: validate JWTs in observe-only mode first, then require JWT and roles; retain the shared-secret check.
- Local e2e auth: locally signed JWTs, without introducing a local OAuth server.
- Roles claim: `roles`.
- In scope: `svc_webapi` JWT validation, `fun_report1` JWT recognition/enforcement, local JWT e2e lane, and staged production cutover.
- Out of scope: introducing Keycloak/local OAuth server and rewriting the frontend UI login flow.
