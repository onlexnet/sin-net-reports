# Proposal

## Why

Customer credentials currently share one "Autoryzacje" section, so SinNet and customer authorizations cannot be kept distinct. Introduce two persistent groups while retaining all existing credentials and functionality.

## What Changes

- Replace the single section with "Autoryzacje SinNet" first and "Autoryzacje Klienta" second, including when either group is empty.
- Assign every existing authorization to SinNet; the customer group starts empty for existing customers.
- Give each section the same portal choices, standard and extended credential formats, TOTP support, change metadata, and add/edit/remove behavior.
- Persist an authorization group for both credential formats and carry it through database, gRPC, GraphQL, and frontend models.
- Preserve existing credentials and their change metadata during migration and unchanged saves.
- Keep current access rules and customer-level saving. Moving entries between groups, new portal types, encryption changes, and separate customer-facing access are out of scope.

## Capabilities

### New Capabilities

- `customer-authorizations`: Customer credential management in distinct SinNet and customer groups, including display ordering, equivalent functionality, persistence, and legacy-data compatibility.

### Modified Capabilities

None. No existing OpenSpec capabilities are registered.

## Impact

- `app_time/src/app/customer/`: group-aware loading, editing, rendering, and save payloads; GraphQL operations and generated frontend types.
- `svc_webapi`: GraphQL schema and DTOs, domain records, and GraphQL/gRPC mapping.
- `res_schema/uservice-timeentries.rpc/customers.proto` and generated `lib_api-java` clients: additive grouping fields.
- `svc_timeentries`: domain models, mapping/cloning, change comparison, JPA persistence, and a new Liquibase migration for `secret` and `secret_ex`.
- Existing consumers that derive customer search information from credentials must retain their current SinNet-based behavior.
- No new runtime dependencies or services. Coordinate the deployment because old full-customer writers do not preserve group information.
