# Design

## Context

See `proposal.md` for motivation and `specs/customer-authorizations/spec.md` for the behavior contract.

The customer form currently renders extended entries, standard entries, and one `NewSecret` control under a single heading in `app_time/src/app/customer/CustomerView.tsx`. `CustomerView.Edit.tsx` loads the two formats into separate arrays, and the GraphQL save mutation submits both complete arrays with the customer.

GraphQL DTOs and domain records in `svc_webapi` map to `customers.proto` messages. `svc_timeentries` stores the two formats as JPA element collections in `secret` and `secret_ex`. Repository collections are sets, and `CustomersRpcUpdateImpl` matches credentials by content to retain last-change metadata. Both manual and MapStruct mapping paths exist, so the new field must survive all paths, including `Clone`.

Liquibase discovers numbered migrations through `includeAll` in `db/changelog-master.yaml`. No existing main OpenSpec capabilities cover this behavior.

## Goals / Non-Goals

**Goals:**

- Add one orthogonal group dimension throughout the existing customer aggregate without changing credential formats.
- Make group membership survive mapping, set equality, metadata comparison, persistence, and full-customer save/reload.
- Reuse existing credential controls and avoid duplicating portal definitions or TOTP logic.

**Non-Goals:**

- No standalone secret entity, identifier, API, or per-secret save operation.
- No new permission model: "Klienta" is a credential category, not a new account role or customer-facing feature.
- No drag/drop or transfer operation between groups, portal catalog redesign, or unrelated credential-security work.

## Decisions

### 1. Add a typed discriminator, not new credential collections

Add `authorizationGroup` with values `SINNET` and `CUSTOMER` to both standard and extended models. Keep existing `secrets` and `secretsEx` collections, each capable of containing both groups. Use service-local domain enums with explicit boundary mappings rather than depending on transport enums in domain models.

Alternatives: four collections would multiply schema fields and save paths; separate group tables would duplicate persistence structures. A discriminator adds the grouping without conflating it with the two credential formats.

### 2. Make transport additions compatible with legacy SinNet payloads

- Append an enum field to each protobuf credential message using unused field numbers (8 for standard, 10 for extended); do not change existing tags. The protobuf enum's zero value means `SINNET`, so older payloads preserve their historical interpretation. Reject unrecognized enum values explicitly.
- Expose a GraphQL `AuthorizationGroup` enum and non-null `authorizationGroup` output on both credential types. Use `authorizationGroup: AuthorizationGroup! = SINNET` on both inputs: omission defaults to SinNet, while explicit null and unsupported values are rejected.
- Propagate the field through GraphQL DTOs, domain records, both adapter mappers, the gRPC gateway, and backend DTO/JPA mapping paths. Preserve existing constructors' call sites and fixtures consistently when record signatures change.
- Include the output field in the frontend detail and list queries and regenerate types with the existing code generator, not manual edits to generated files.

Alternative: a required input without a default would break existing requests. Free-form strings would weaken validation and make accidental grouping errors easier.

This is additive wire compatibility, not safe arbitrary mixed-version operation. Older applications ignore the group and replace whole collections on save; deployment must prevent old writers from running after customer-group entries can exist.

### 3. Backfill group columns without touching credential metadata

Add a new numbered Liquibase migration, following `0019`, for `authorization_group` in both tables. Use stable string values, backfill all existing rows to `SINNET`, and apply a non-null/default constraint of `SINNET` plus allowed-value constraints. Map enums by string rather than ordinal.

The migration must not update credential columns, `changed_who`, or `changed_when`. A default supports inserts that omit the new column during the controlled upgrade.

Alternative: interpreting null as SinNet forever avoids backfill but leaves ambiguous storage and increases fallback logic in every consumer.

### 4. Include group in identity-by-content and change comparison

Ensure the group participates in JPA embeddable equality/hash code so identical credentials from different groups do not collapse in repository sets. Include it in both standard and extended equality comparers used by `CustomersRpcUpdateImpl`, and preserve it in cloning and every mapper.

Normalize recognized legacy omissions before comparisons. Unchanged credentials retain metadata only from an entry in the same group. New or edited entries retain the existing requestor/time stamping behavior in either group, including customer creation.

Alternative: comparing only existing credential fields could attach SinNet metadata to a matching customer-group credential and incorrectly classify an addition as unchanged.

### 5. Render two group-scoped sections over the existing arrays

Keep the two arrays for credential formats and add group membership to their entries. Render the SinNet section first and the customer section second; within each, preserve the current extended-then-standard order. Both sections remain visible when empty.

Each section filters entries by its group, reuses `UserPasswordItemExt` and `UserPasswordItem`, and has its own `NewSecret` control bound to that group. New entries receive group membership at creation. Reuse a small section renderer/component rather than duplicating portal or TOTP behavior.

Continue identifying edits/removals by local React keys, never by portal name or filtered-array index. Preserve group membership when item components construct replacement models in change handlers; include it in both save input mappings.

Alternative: splitting state into four separately managed arrays increases update/serialization paths and makes whole-customer saves harder to keep consistent.

### 6. Preserve existing credential-derived customer lookups

Compatibility assumption: existing NFZ provider-code searches and displays continue to use SinNet's "Portal swiadczeniodawcy" authorization rather than arbitrarily selecting the new customer-group entry.

Update the list query and dependent shapes and constrain selection to `SINNET` in `useListCustomers.ts`, `CustomerComboBox.tsx`, and `ActionView.Edit.CustomerView.tsx`. Keep current search ranking and first-match semantics within that group. This avoids nondeterministic changes when both groups contain the same portal.

Alternative: searching both groups would expand unrelated search behavior and requires a separate product decision on conflicting provider codes.

## Risks / Trade-offs

- Old full-customer writers discard or misclassify group information -> use a coordinated write pause and refresh old browser sessions before accepting customer-group data.
- Multiple mapping implementations can omit the field -> extend existing adapter tests and cover backend manual/MapStruct round trips and cloning.
- Set persistence can collapse identical cross-group entries -> include group in equality and test identical credentials in both groups against SQL Server.
- Reconstructed UI item models can lose group membership -> test editing an existing customer-group entry and inspect the submitted save payload.
- Testcontainers-based database verification needs Docker/SQL Server -> run focused database tests where available; report blocked database validation explicitly rather than substituting a compilation result.
- Shared fields do not provide isolation or encryption -> retain existing access rules and document that grouping alone adds neither.

## Migration Plan

1. Build regenerated gRPC clients, updated services, and frontend before rollout.
2. Verify the migration on a fresh database and on a legacy fixture containing both credential formats and known metadata.
3. Back up the database and pause customer writes during the coordinated deployment.
4. Apply the additive migration, deploy the group-aware backend services, then deploy the frontend and require stale editing sessions to reload before writes resume.
5. Verify existing entries are in SinNet, the customer section starts empty, and entries added to each group survive save/reload independently.
6. For rollback, pause writes and prefer a forward correction. Do not drop group columns or reopen old writers once customer-group entries exist. A rollback to old application binaries requires restoring the pre-upgrade backup or explicitly reconciling/exporting new entries first; either operation needs separate operational approval.
