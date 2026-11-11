# Tasks

## 1. Add the grouping contract and migrate storage

- [x] 1.1 Extend `customers.proto` with the authorization-group enum (`SINNET` as zero, `CUSTOMER` as the other value) and append group fields to both credential messages without renumbering existing fields; verify `mvn install -f lib_api-java -DskipTests` regenerates and compiles clients.
- [x] 1.2 Add a numbered Liquibase migration after `0019` for both credential tables, with `SINNET` backfill/default, non-null group columns, and allowed-value constraints; verify it applies to a fresh SQL Server database and a pre-change fixture with both formats while preserving all credential fields and change metadata.

## 2. Carry groups through the credential backend

- [x] 2.1 Add typed group membership to both timeentries credential models and JPA embeddables, preserving string enum storage, set equality, and every manual/MapStruct/clone path; extend backend mapping and persistence tests to verify both groups and otherwise identical cross-group entries survive round trips.
- [x] 2.2 Include group in both normalization equality comparers, retain legacy omitted-group defaults, and reject unknown transport values explicitly; extend `CustomersRpcUpdateImplTest` to verify new/edited metadata, unchanged metadata per group, and no matching across groups for both formats.
- [x] 2.3 Verify new-customer and existing-customer saves stamp added entries correctly in either group and preserve unchanged entries; run focused backend tests, including the SQL Server round-trip coverage, and build `svc_timeentries` with `mvn install -pl host -am -f svc_timeentries -DskipTests`, explicitly reporting any blocked database validation.

## 3. Expose groups through GraphQL

- [x] 3.1 Add the GraphQL group enum, non-null credential output fields, and input fields with omitted-value default `SINNET`; update GraphQL DTOs, domain records, gateway/adapter mappings, and dependent constructors/fixtures, then verify both existing `CustomerMapperTest` suites cover both groups and both formats.
- [x] 3.2 Extend existing GraphQL/BDD customer tests to verify group round trips, omission defaulting to SinNet, explicit-null/invalid-group rejection, and mixed-group saves without credential loss; run focused tests and build with `mvn install -f svc_webapi -Drevision=$(cat svc_webapi/.semversion) -DskipTests`.

## 4. Render and edit the two authorization sections

- [x] 4.1 Add group selection fields to customer detail/list GraphQL operations and group membership to frontend credential models; run `npm run generate` in `app_time` and verify detail loading and both save-input mappings retain each entry's group without hand-editing generated types.
- [x] 4.2 Render "Autoryzacje SinNet" first and "Autoryzacje Klienta" second, each with existing standard/extended controls and a group-bound add control; preserve group in replacement models produced by item change handlers, and add focused React tests for empty sections, ordering, both credential formats, and independent add/edit/remove behavior.
- [x] 4.3 Add focused customer-form save tests covering an edited customer-group entry, identical credentials in both groups, TOTP/recovery fields, and unrelated customer changes; verify submitted payloads and reloaded sections retain both groups, and verify existing missing/invalid TOTP feedback remains available in either section.
- [x] 4.4 Keep existing NFZ provider-code lookup/search/display consumers SinNet-based in `useListCustomers.ts`, `CustomerComboBox.tsx`, and `ActionView.Edit.CustomerView.tsx`; extend nearby tests with conflicting cross-group provider codes and verify search ranking and displayed values remain unchanged for SinNet data.
- [x] 4.5 Run the focused frontend tests with the existing non-watch test runner, then `npm run build` in `app_time`; verify generated types compile and the existing build/lint checks pass.

## 5. Verify the integrated change and document rollout

- [x] 5.1 Exercise migration-to-UI behavior with an existing customer: verify legacy entries appear only in SinNet with unchanged metadata, add standard/extended entries to both groups, save/reopen, edit/remove one group, and verify the other remains intact.
- [x] 5.2 Verify end-to-end that otherwise identical cross-group credentials remain separate with independent metadata, customer creation works for both groups, and current access rules are unchanged; record executed checks and any environment blockers.
- [x] 5.3 Update the relevant existing operational documentation with the additive migration, coordinated write pause, service/frontend upgrade order, stale-session reload requirement, and rollback limitations; verify the documented sequence prevents old full-customer writers from resuming against customer-group data.

## Verification record

- Passed SQL Server migration checks for fresh and legacy databases, backend mapping/normalization/persistence tests, the full webapi test suite, and the timeentries/webapi builds.
- Passed three focused frontend suites (15 tests), GraphQL generation, and the production frontend build. Build warnings remain in unchanged code/dependencies.
- With user approval, rebuilt and upgraded the existing local k3d stack. Seeded synthetic standard/extended credentials before migration, paused the frontend/gateway, upgraded timeentries before webapi/frontend, and verified unchanged credential fields, TOTP/recovery values, and audit metadata through the real API.
- Passed headless Chromium checks against the deployed UI, GraphQL gateway, Dapr, and SQL Server: ordered sections, legacy SinNet-only display, both credential formats/groups, TOTP, save/reopen, independent edit/remove, identical cross-group credentials, metadata preservation across unrelated edits, and new-customer creation with saving-user metadata.
- Compared unauthenticated and unrelated-user customer access before/after the upgrade; existing local behavior is unchanged. Production Azure B2C authentication was not exercised.
- Missing host k3d/kubectl and Chromium libraries were worked around using the cluster node's kubectl/ctr and containerized Playwright. No outstanding environment blockers remain.
- Fixture setup/cleanup encountered existing project-version and unimplemented GraphQL project-removal issues. Reused the test-owned project, removed both synthetic customers through the API, and removed the remaining empty project with an ID/owner-guarded SQL delete; no project lifecycle code was changed.
- The upgraded local frontend and both backend services are healthy. Temporary browser containers, unused host test installations, and synthetic database fixtures were cleaned up.
