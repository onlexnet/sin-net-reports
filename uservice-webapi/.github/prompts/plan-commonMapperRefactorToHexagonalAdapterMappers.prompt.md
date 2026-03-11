## Plan: CommonMapper Refactor to Hexagonal Adapter Mappers

Refine the migration into small, low-risk phases: first introduce adapter mappers, then migrate call sites by boundary, then remove CommonMapper. Enforce strict mapping flow GraphQL↔Domain and Domain↔gRPC, with no GraphQL↔gRPC direct mapping.

**Steps**
1. Phase 0 — Baseline safety checks
   - Confirm all current CommonMapper usages (production + tests) compile on branch before touching signatures.
   - Freeze naming decisions: EntityGqlMapper in gql adapter, EntityGrpcMapper in grpc adapter.

2. Phase 1 — Introduce new mappers without removing old one (parallelizable)
   - Create host/src/main/java/sinnet/infra/adapters/gql/EntityGqlMapper.java for GraphQL↔Domain entity mapping.
   - Create host/src/main/java/sinnet/infra/adapters/grpc/EntityGrpcMapper.java for Domain↔gRPC entity/date mappings.
   - Keep both non-Spring MapStruct style (no componentModel=spring), factory-based like existing adapter mappers.
   - Add mapper unit tests first so behavior parity is explicit before call-site migration.

3. Phase 2 — Migrate grpc-side dependencies first (depends on 2)
   - Replace CommonMapper in ActionsGrpcFacadeImpl and Maping with EntityGrpcMapper.
   - Keep external behavior unchanged; only dependency and mapping source changes.
   - Verify targeted tests after grpc-side migration.

4. Phase 3 — Migrate app/port signatures (depends on 3)
   - Replace CommonMapper type in TimeentriesServicePortIn, TimeentriesService, and ActionsGrpcPortOut method signatures.
   - Use explicit mapper type(s) reflecting needed direction only.
   - Ensure no lower layer depends on gql.api package.

5. Phase 4 — Migrate GraphQL controllers (depends on 2,4)
   - Replace CommonMapper usage in ActionsQueryGet, ActionsQuerySearch, CustomersMutationReserve.
   - For gRPC response IDs used in gql layer, normalize through Domain: grpc→domain via EntityGrpcMapper, then domain→gql via EntityGqlMapper.
   - Keep resolver outputs unchanged.

6. Phase 5 — Test migration and cleanup (depends on 4,5)
   - Split CommonMapperTest into adapter-specific tests:
     - host/src/test/java/sinnet/infra/adapters/gql/EntityGqlMapperTest.java
     - host/src/test/java/sinnet/infra/adapters/grpc/EntityGrpcMapperTest.java
   - Update BDD classes that autowire CommonMapper to use new mapper access pattern.
   - Remove host/src/main/java/sinnet/gql/api/CommonMapper.java only after zero remaining references.

7. Phase 6 — Architecture and build validation (depends on all prior phases)
   - Run focused tests first, then architecture test, then host build, then full build.
   - Stop and triage only refactor-caused regressions.

**Relevant files**
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/gql/api/CommonMapper.java — source to decompose and delete last.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/infra/adapters/gql/CustomerMapper.java — adapter MapStruct style reference.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/infra/adapters/grpc/TimeEntryModelMapper.java — grpc mapper style reference.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/infra/adapters/grpc/ActionsGrpcFacadeImpl.java — grpc mapping dependency migration.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/infra/adapters/grpc/Maping.java — grpc date conversion dependency migration.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/app/ports/out/ActionsGrpcPortOut.java — signature/default method migration.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/app/ports/in/TimeentriesServicePortIn.java — port type replacement.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/app/flow/timeentries/TimeentriesService.java — pass-through type replacement.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/gql/api/ActionsQueryGet.java — gql resolver migration.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/gql/api/ActionsQuerySearch.java — gql resolver migration.
- /workspaces/sin-net-reports/uservice-webapi/host/src/main/java/sinnet/gql/api/CustomersMutationReserve.java — gql resolver migration.
- /workspaces/sin-net-reports/uservice-webapi/host/src/test/java/sinnet/gql/api/CommonMapperTest.java — split and replace.
- /workspaces/sin-net-reports/uservice-webapi/host/src/test/java/sinnet/bdd/ActionsSteps.java — CommonMapper wiring removal.
- /workspaces/sin-net-reports/uservice-webapi/host/src/test/java/sinnet/bdd/StepDefinitions.java — CommonMapper wiring removal.
- /workspaces/sin-net-reports/uservice-webapi/host/src/test/java/sinnet/AppArchTest.java — architecture compliance validation.

**Verification**
1. Mapper-level tests for new EntityGqlMapper and EntityGrpcMapper.
2. Impacted suites around Actions query/mutation and grpc facade.
3. Architecture rule check: mvn test -Dtest=AppArchTest
4. Host module build: cd host && mvn clean package
5. Full build: mvn clean install

**Decisions**
- Confirmed scope: GraphQL↔Domain + Domain↔gRPC only.
- Confirmed constraint: no Spring component model for replacement mappers.
- Included: package relocation to proper adapters and all dependent signature updates.
- Excluded: unrelated model redesign, unrelated cleanup, unrelated failing tests.

**Further Considerations**
1. Preferred PR slicing for low review risk:
   - PR1: introduce mappers + tests
   - PR2: migrate grpc/app/ports
   - PR3: migrate gql + remove CommonMapper
2. Keep temporary coexistence (old + new) only until all references are migrated; then remove immediately to avoid drift.
3. If BDD wiring becomes difficult with non-Spring mappers, use factory access in tests rather than reintroducing Spring component model.
